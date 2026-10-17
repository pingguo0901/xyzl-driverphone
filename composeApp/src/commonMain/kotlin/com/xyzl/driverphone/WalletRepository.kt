package com.xyzl.driverphone

import com.xyzl.driverphone.model.LedgerEntry
import com.xyzl.driverphone.model.LedgerKind
import com.xyzl.driverphone.model.formatMoney
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

/** 钱包（se_wallets） */
data class WalletInfo(
    val id: String,
    val accountId: String,
    val walletType: String,       // main / collection
    val currency: String,         // MYR / SGD
    val totalBalance: Double,
    val availableBalance: Double,
    val accountNumber: String,
) {
    val title: String get() = if (walletType == "collection") "代收 " + currency else "钱包余额"
}

/** 联系人（se_friend_connections + se_users + se_wallets） */
data class PayeeInfo(
    val userId: String,
    val name: String,
    val accountNumber: String,
    val accountId: String?,
    val walletId: String?,
)

/** 收款二维码（se_user_qr_codes + se_users） */
data class WalletQrInfo(
    val name: String,
    val token: String,
    val imageUrl: String?,
)

/**
 * 我的钱包 / 转账 / 收款二维码。
 *
 * 余额口径：se_wallets.total_balance / available_balance（持续性，只有上交/提现才扣减）
 * 流水：se_financial_ledger where account_id = 我 + currency
 * 转账：只 INSERT se_transfer_records（transfer_status='pending_send'），
 *       前端不 UPDATE se_wallets —— 真正扣减由后台/接收方处理。
 */
class WalletRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    suspend fun accountId(userId: String): String? = safe {
        supabase.postgrest.from("se_account_members")
            .select(Columns.list("account_id")) {
                filter { eq("user_id", userId) }
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()?.s("account_id")
    }

    /** 我的钱包（按 owner_id = 我；主钱包 MYR + 两个代收） */
    suspend fun wallets(userId: String): List<WalletInfo> {
        val rows = safe {
            supabase.postgrest.from("se_wallets")
                .select(Columns.list("id", "account_id", "owner_id", "wallet_type", "currency", "total_balance", "available_balance", "account_number")) {
                    filter { eq("owner_id", userId) }
                    order("wallet_type", Order.ASCENDING)
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        var list = rows.mapNotNull { it.toWallet() }
        if (list.isEmpty()) {
            // 兼容：钱包挂在 account 上（owner_id 不是 user）
            val acc = accountId(userId) ?: return emptyList()
            val byAccount = safe {
                supabase.postgrest.from("se_wallets")
                    .select(Columns.list("id", "account_id", "owner_id", "wallet_type", "currency", "total_balance", "available_balance", "account_number")) {
                        filter { eq("account_id", acc) }
                        order("wallet_type", Order.ASCENDING)
                    }
                    .decodeList<JsonObject>()
            } ?: emptyList()
            list = byAccount.mapNotNull { it.toWallet() }
        }
        return list
    }

    /** 账户流水：se_financial_ledger where account_id + currency（可选 category 过滤） */
    suspend fun ledger(
        accountId: String,
        currency: String,
        categories: List<String>? = null,
    ): List<LedgerEntry> {
        val rows = try {
            supabase.postgrest.from("se_financial_ledger")
                .select(
                    Columns.list(
                        "id", "category", "amount", "currency", "direction", "status", "note",
                        "order_no", "transaction_no", "created_at",
                    ),
                ) {
                    filter {
                        eq("account_id", accountId)
                        eq("currency", currency)
                    }
                    order("created_at", Order.DESCENDING)
                    limit(100)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
        return rows
            .filter { r -> categories.isNullOrEmpty() || categories.contains(r.s("category")) }
            .mapNotNull { r ->
            val id = r.s("id") ?: return@mapNotNull null
            val category = r.s("category") ?: ""
            val amount = r.dbl("amount") ?: 0.0
            val direction = r.s("direction") ?: ""
            val kind = when (category) {
                "trip_income" -> LedgerKind.SALARY
                "expense" -> LedgerKind.REIMBURSEMENT
                else -> LedgerKind.TRANSFER
            }
            LedgerEntry(
                id = id,
                kind = kind,
                title = categoryLabel(category),
                currency = r.s("currency") ?: currency,
                amount = amount,
                date = prettyTime(r.s("created_at")),
                status = statusLabel(r.s("status")),
                from = r.s("note") ?: "",
                orderNo = r.s("order_no"),
                remark = r.s("note") ?: "",
                income = direction == "credit" || direction == "in" || direction == "income",
                txNo = r.s("transaction_no"),
            )
        }
    }

    /** 联系人：accepted 的 friend connections（双向）→ 名字 + 钱包账号 */
    suspend fun payees(userId: String): List<PayeeInfo> {
        val links = mutableListOf<JsonObject>()
        val a = safe {
            supabase.postgrest.from("se_friend_connections")
                .select(Columns.list("user_a_id", "user_b_id", "status")) {
                    filter { eq("user_a_id", userId); eq("status", "accepted") }
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        val b = safe {
            supabase.postgrest.from("se_friend_connections")
                .select(Columns.list("user_a_id", "user_b_id", "status")) {
                    filter { eq("user_b_id", userId); eq("status", "accepted") }
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        links += a
        links += b
        val otherIds = links.mapNotNull { row ->
            val ua = row.s("user_a_id")
            val ub = row.s("user_b_id")
            when (userId) {
                ua -> ub
                ub -> ua
                else -> null
            }
        }.distinct()
        if (otherIds.isEmpty()) return emptyList()

        val names = mutableMapOf<String, String>()
        val wallets = mutableListOf<JsonObject>()
        for (uid in otherIds) {
            val user = safe {
                supabase.postgrest.from("se_users")
                    .select(Columns.list("full_name")) { filter { eq("id", uid) } }
                    .decodeList<JsonObject>()
                    .firstOrNull()?.s("full_name")
            }
            names[uid] = user ?: "未命名"
            val ws = safe {
                supabase.postgrest.from("se_wallets")
                    .select(Columns.list("id", "account_id", "owner_id", "wallet_type", "currency", "account_number")) {
                        filter { eq("owner_id", uid) }
                    }
                    .decodeList<JsonObject>()
            } ?: emptyList()
            wallets += ws
        }
        val walletByUser = wallets.groupBy { it.s("owner_id") ?: "" }.mapValues { (_, list) ->
            list.firstOrNull { w -> w.s("wallet_type") == "main" } ?: list.first()
        }

        return otherIds.mapNotNull { uid ->
            val name = names[uid] ?: return@mapNotNull null
            val w = walletByUser[uid]
            PayeeInfo(
                userId = uid,
                name = name,
                accountNumber = w?.s("account_number") ?: "",
                accountId = w?.s("account_id"),
                walletId = w?.s("id"),
            )
        }
    }

    /** 收款二维码：se_user_qr_codes (user_id=我, qr_category='payment') + se_users.full_name */
    suspend fun qrInfo(userId: String): WalletQrInfo {
        val name = safe {
            supabase.postgrest.from("se_users")
                .select(Columns.list("full_name")) { filter { eq("id", userId) } }
                .decodeList<JsonObject>()
                .firstOrNull()?.s("full_name")
        } ?: "我"
        val row = safe {
            supabase.postgrest.from("se_user_qr_codes")
                .select(Columns.list("qr_token", "qr_image_url", "qr_category", "status")) {
                    filter {
                        eq("user_id", userId)
                        eq("qr_category", "payment")
                        eq("status", "active")
                    }
                    order("created_at", Order.DESCENDING)
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        }
        return WalletQrInfo(
            name = name,
            token = row?.s("qr_token") ?: "https://xyzl.my/pay/" + userId.take(8),
            imageUrl = row?.s("qr_image_url"),
        )
    }

    /**
     * 发起转账：只写 se_transfer_records，不动 se_wallets。
     * transfer_status = pending_send（我方待发送）。
     */
    suspend fun createTransfer(
        userId: String,
        from: WalletInfo,
        to: PayeeInfo,
        amount: Double,
        remark: String,
        paymentDate: String,
    ): Result<String> = try {
        val toAccountId = to.accountId
            ?: return Result.failure(IllegalStateException("对方还没有钱包账号，无法转账"))
        supabase.postgrest.from("se_transfer_records").insert(
            buildJsonObject {
                put("account_id", from.accountId)
                put("from_account_id", from.accountId)
                put("from_owner_type", "user")
                put("from_owner_id", userId)
                put("from_currency", from.currency)
                put("to_account_id", toAccountId)
                put("to_owner_type", "user")
                put("to_owner_id", to.userId)
                put("to_currency", from.currency)
                put("amount", amount)
                put("status", "pending")
                put("transfer_status", "pending_send")
                put("initiated_by_user_id", userId)
                put("from_wallet_id", from.id)
                if (to.walletId != null) put("to_wallet_id", to.walletId)
                put("from_account_number", from.accountNumber)
                put("to_account_number", to.accountNumber)
                put("remark", remark)
                if (paymentDate.isNotBlank()) put("payment_date", paymentDate)
            },
        )
        Result.success("pending_send")
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ---------------------------------------------------------------- 工具

    private inline fun <T> safe(block: () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
}

private fun JsonObject.toWallet(): WalletInfo? {
    val id = s("id") ?: return null
    return WalletInfo(
        id = id,
        accountId = s("account_id") ?: "",
        walletType = s("wallet_type") ?: "main",
        currency = s("currency") ?: "MYR",
        totalBalance = dbl("total_balance") ?: 0.0,
        availableBalance = dbl("available_balance") ?: 0.0,
        accountNumber = s("account_number") ?: "-",
    )
}

private fun categoryLabel(category: String): String = when (category) {
    "trip_income" -> "薪资"
    "expense" -> "报销"
    "collection" -> "代收"
    "transfer" -> "转账"
    "withdraw" -> "提现"
    "hand_in" -> "上交"
    else -> category.ifBlank { "流水" }
}

private fun statusLabel(status: String?): String = when (status) {
    null, "" -> ""
    "completed", "success" -> "已完成"
    "pending" -> "处理中"
    "failed" -> "失败"
    else -> status
}

private fun prettyTime(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val s = raw.replace("T", " ")
    return if (s.length >= 16) s.take(16) else s
}

private fun JsonObject.s(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() && it != "null" }

private fun JsonObject.dbl(key: String): Double? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()

/** 钱包卡片统一显示金额 */
fun walletBalanceText(currency: String, amount: Double): String = formatMoney(currency, amount)
