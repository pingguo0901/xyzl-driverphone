package com.xyzl.driverphone

import com.xyzl.driverphone.model.LedgerEntry
import com.xyzl.driverphone.model.LedgerKind
import com.xyzl.driverphone.screens.currentTimeParts
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

/** 财务页汇总（《财务页面接线表》） */
data class FinanceSummary(
    val todaySalary: Double = 0.0,
    val todayExpense: Double = 0.0,
    val todayTrips: Int = 0,
    val monthExpense: Double = 0.0,
    val monthSalary: Double = 0.0,
    val sgdCollection: Double = 0.0,
    val myrCollection: Double = 0.0,
)

/** 提交报销：归属车辆下拉项 */
data class VehicleOption(val id: String, val label: String)

/**
 * 财务 / 流水 / 报销
 * - 薪资：se_financial_ledger.sum(amount) where category='trip_income'
 * - 报销：se_expense_records.sum(amount)
 * - 单数：se_order_parents count where assigned_driver_id=我 and execution_status='已完成'
 * - 代收：se_wallets where wallet_type='collection'
 */
class FinanceRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    /** 我的账户 id（se_account_members.user_id → account_id） */
    suspend fun accountId(userId: String): String? = runQuery {
        supabase.postgrest.from("se_account_members")
            .select(Columns.list("account_id")) {
                filter { eq("user_id", userId) }
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()
            ?.fStr("account_id")
    }

    suspend fun summary(userId: String, accountId: String?): FinanceSummary {
        val today = currentTimeParts().first                       // yyyy-MM-dd
        val monthStart = today.take(7) + "-01"

        val salaryToday = ledgerSum(accountId, "trip_income", today)
        val salaryMonth = ledgerSum(accountId, "trip_income", monthStart)
        val expenseToday = expenseSum(userId, accountId, today)
        val expenseMonth = expenseSum(userId, accountId, monthStart)
        val trips = tripsDone(userId, today)
        val wallets = collection(userId, accountId)

        return FinanceSummary(
            todaySalary = salaryToday,
            todayExpense = expenseToday,
            todayTrips = trips,
            monthExpense = expenseMonth,
            monthSalary = salaryMonth,
            sgdCollection = wallets["SGD"] ?: 0.0,
            myrCollection = wallets["MYR"] ?: 0.0,
        )
    }

    /** 流水记录：se_financial_ledger where account_id = 我 order by created_at desc */
    suspend fun ledger(accountId: String): List<LedgerEntry> {
        val rows = try {
            supabase.postgrest.from("se_financial_ledger")
                .select(
                    Columns.list(
                        "id", "account_id", "category", "amount", "currency", "status", "note",
                        "order_no", "transaction_no", "receipt_url", "created_at",
                    ),
                ) {
                    filter { eq("account_id", accountId) }
                    order("created_at", Order.DESCENDING)
                    limit(200)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }

        val accountNames = accountsOf(rows.mapNotNull { it.fStr("account_id") }.distinct())

        return rows.map { row ->
            val category = row.fStr("category").orEmpty()
            val amount = row.fDbl("amount") ?: 0.0
            LedgerEntry(
                id = row.fStr("id").orEmpty(),
                kind = kindOf(category),
                title = titleOf(category, row.fStr("note")),
                currency = row.fStr("currency") ?: "MYR",
                amount = amount,
                date = formatHomeTimestamp(row.fStr("created_at")).orEmpty(),
                status = statusOf(row.fStr("status")),
                from = accountNames[row.fStr("account_id").orEmpty()].orEmpty(),
                expenseType = if (category == "expense") row.fStr("note") else null,
                orderNo = row.fStr("order_no"),
                remark = row.fStr("note").orEmpty(),
                income = category != "expense",
                receiptUrl = row.fStr("receipt_url"),
                txNo = row.fStr("transaction_no") ?: row.fStr("id"),
            )
        }
    }

    /** 提交报销：归属车辆（se_vehicle_profiles where owner_id = 我的 account_id） */
    suspend fun vehicles(accountId: String): List<VehicleOption> {
        val rows = try {
            supabase.postgrest.from("se_vehicle_profiles")
                .select(Columns.list("id", "plate_no", "make", "model", "owner_id", "account_id")) {
                    filter {
                        eq("owner_id", accountId)
                        eq("is_active", true)
                    }
                    limit(50)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
        return rows.mapNotNull { row ->
            val id = row.fStr("id") ?: return@mapNotNull null
            val plate = row.fStr("plate_no").orEmpty()
            val name = listOfNotNull(row.fStr("make"), row.fStr("model")).joinToString(" ")
            VehicleOption(id, (plate + " " + name).trim().ifBlank { "车辆" })
        }
    }

    /** 收据上传：Storage se_trip-receipts，返回对象路径 */
    suspend fun uploadReceipt(uri: String, userId: String): String? = try {
        val bytes = readFileBytes(uri) ?: return null
        val ext = uri.substringAfterLast('/').substringAfterLast('.', "jpg").take(4).ifBlank { "jpg" }
        val path = "expense/" + userId + "/" + kotlin.random.Random.nextLong().toString(16).removePrefix("-") + "." + ext
        supabase.storage.from("se_trip-receipts").upload(path, bytes)
        path
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    /** 确认提交 → INSERT se_expense_records（service_date 有就写，没有就退回 created_at） */
    suspend fun submitExpense(
        userId: String,
        accountId: String?,
        category: String,
        amount: Double,
        currency: String,
        vehicleId: String?,
        receiptUrl: String?,
        serviceDate: String?,
        note: String?,
    ): Boolean {
        fun values(withServiceDate: Boolean) = buildJsonObject {
            if (accountId != null) put("account_id", accountId)
            put("owner_type", "account")
            if (accountId != null) put("owner_id", accountId)
            put("category", category)
            put("amount", amount)
            put("currency", currency)
            if (vehicleId != null) put("vehicle_id", vehicleId)
            put("driver_id", userId)
            if (receiptUrl != null) put("receipt_url", receiptUrl)
            if (!note.isNullOrBlank()) put("note", note)
            put("status", "pending")
            put("created_by_user_id", userId)
            if (withServiceDate && !serviceDate.isNullOrBlank()) put("service_date", serviceDate)
        }
        val first = try {
            supabase.postgrest.from("se_expense_records").insert(values(true))
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
        if (first) return true
        // service_date 列还不存在时退回（只写 created_at）
        return try {
            supabase.postgrest.from("se_expense_records").insert(values(false))
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    // ------------------------------------------------------------ 内部
    private suspend fun ledgerSum(accountId: String?, category: String, since: String): Double {
        if (accountId == null) return 0.0
        val rows = try {
            supabase.postgrest.from("se_financial_ledger")
                .select(Columns.list("amount")) {
                    filter {
                        eq("account_id", accountId)
                        eq("category", category)
                        gte("created_at", since)
                    }
                    limit(1000)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
        return rows.sumOf { it.fDbl("amount") ?: 0.0 }
    }

    private suspend fun expenseSum(userId: String, accountId: String?, since: String): Double {
        val rows = try {
            supabase.postgrest.from("se_expense_records")
                .select(Columns.list("amount", "account_id", "driver_id")) {
                    filter {
                        if (accountId != null) eq("account_id", accountId) else eq("driver_id", userId)
                        gte("created_at", since)
                    }
                    limit(1000)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
        return rows.sumOf { it.fDbl("amount") ?: 0.0 }
    }

    private suspend fun tripsDone(userId: String, since: String): Int = try {
        supabase.postgrest.from("se_order_parents")
            .select(Columns.list("id")) {
                filter {
                    eq("assigned_driver_id", userId)
                    eq("execution_status", "已完成")
                    gte("completed_at", since)
                }
                limit(500)
            }
            .decodeList<JsonObject>()
            .size
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        0
    }

    private suspend fun collection(userId: String, accountId: String?): Map<String, Double> {
        val rows = try {
            supabase.postgrest.from("se_wallets")
                .select(Columns.list("currency", "total_balance", "wallet_type", "account_id", "owner_id")) {
                    filter {
                        eq("wallet_type", "collection")
                        if (accountId != null) eq("account_id", accountId) else eq("owner_id", userId)
                    }
                    limit(20)
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
        return rows
            .mapNotNull { row -> row.fStr("currency")?.let { it to (row.fDbl("total_balance") ?: 0.0) } }
            .toMap()
    }

    private suspend fun accountsOf(ids: List<String>): Map<String, String> {
        if (ids.isEmpty()) return emptyMap()
        return try {
            supabase.postgrest.from("se_accounts")
                .select(Columns.list("id", "display_name")) { filter { isIn("id", ids) } }
                .decodeList<JsonObject>()
                .mapNotNull { row -> row.fStr("id")?.let { id -> row.fStr("display_name")?.let { id to it } } }
                .toMap()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private suspend fun <T> runQuery(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private fun kindOf(category: String): LedgerKind = when (category) {
        "expense" -> LedgerKind.REIMBURSEMENT
        "transfer" -> LedgerKind.TRANSFER
        else -> LedgerKind.SALARY
    }

    private fun titleOf(category: String, note: String?): String = when (category) {
        "expense" -> note?.takeIf { it.isNotBlank() } ?: "报销"
        "transfer" -> "转账"
        else -> "行程薪资"
    }

    private fun statusOf(status: String?): String = when (status) {
        null, "" -> "已入账"
        "pending" -> "待审批"
        "approved" -> "已通过"
        "rejected" -> "已驳回"
        "completed", "success", "paid" -> "已入账"
        else -> status
    }
}

private fun JsonObject.fStr(key: String): String? = (this[key] as? kotlinx.serialization.json.JsonPrimitive)
    ?.takeIf { it !is kotlinx.serialization.json.JsonNull }
    ?.contentOrNull

private fun JsonObject.fDbl(key: String): Double? = (this[key] as? kotlinx.serialization.json.JsonPrimitive)
    ?.contentOrNull
    ?.toDoubleOrNull()
