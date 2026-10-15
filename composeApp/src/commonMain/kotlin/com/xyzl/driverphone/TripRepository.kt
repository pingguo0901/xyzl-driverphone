package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * 行程（《行程页面接线表》+《行程详情页面接线表》）
 *
 * 列表：se_order_parents where assigned_driver_id = 我 order by accepted_at desc
 * 起点 / 目的地：se_bookings（parent_id 关联订单）；详情用 pickup_points / dropoff_points（jsonb 数组）
 * 金额：settlement_method='Cash' 且 customer_pay_amount > 0 → 币种 + 金额，否则「无需收钱」
 *   —— 优先读 se_order_parents（2026-10-16 起这三个字段已加到主表，直接派单也有）；
 *      主表为空时退回 se_order_bookings（丢单/拿单表）
 */

/** 列表用的一行 */
data class TripItem(
    val orderId: String,
    val acceptedAt: String?,
    val pickup: String?,
    val dropoff: String?,
    val customerName: String?,
    val customerPhone: String?,
    val executionStatus: String?,
    val orderStatus: String?,
    val settlementMethod: String?,
    val payAmount: Double?,
    val payCurrency: String?,
) {
    val cancelled: Boolean get() = orderStatus == "cancelled"
    val completed: Boolean get() = executionStatus == "已完成"
    val moneyText: String get() = payText(settlementMethod, payCurrency, payAmount)
}

/** 详情页用的一整单 */
data class TripDetail(
    val orderId: String,
    val acceptedAt: String?,
    /** 放人时间：se_order_parents.completed_at */
    val completedAt: String?,
    val whatsapp: String?,
    val wechat: String?,
    val adults: Int?,
    val children: Int?,
    val luggage: Int?,
    val pickups: List<String>,
    val dropoffs: List<String>,
    val settlementMethod: String?,
    val payAmount: Double?,
    val payCurrency: String?,
    val salary: Double?,
    val salaryCurrency: String?,
    val executionStatus: String?,
    val orderStatus: String?,
) {
    val cancelled: Boolean get() = orderStatus == "cancelled"
    val completed: Boolean get() = executionStatus == "已完成"
    val statusLabel: String get() = if (cancelled) "已取消" else (executionStatus ?: "待完成")
    val moneyText: String get() = payText(settlementMethod, payCurrency, payAmount)
    val salaryText: String
        get() {
            val value = salary ?: return "—"
            return (salaryCurrency.orEmpty().trim() + " " + formatMoney(value)).trim()
        }
}

class TripRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    // ------------------------------------------------------------ 列表
    suspend fun list(userId: String): List<TripItem> {
        val driverId = driverProfileId(userId)
        var orders = fetchOrders("assigned_driver_id", driverId ?: userId)
        if (orders.isEmpty() && driverId != null) {
            orders = fetchOrders("assigned_driver_id", userId)
        }
        if (orders.isEmpty()) return emptyList()

        val ids = orders.mapNotNull { it.str("id") }
        val bookings = byParentId("se_bookings", Columns.list("parent_id", "pickup_address", "dropoff_address"), ids)
        val pays = byParentId(
            "se_order_bookings",
            Columns.list("parent_id", "settlement_method", "customer_pay_amount", "customer_pay_currency"),
            ids,
        )

        return orders.map { row ->
            val id = row.str("id").orEmpty()
            val booking = bookings[id]
            val pay = pays[id]
            TripItem(
                orderId = id,
                acceptedAt = row.str("accepted_at"),
                pickup = booking?.str("pickup_address"),
                dropoff = booking?.str("dropoff_address"),
                customerName = row.str("customer_name"),
                customerPhone = row.str("customer_phone"),
                executionStatus = row.str("execution_status"),
                orderStatus = row.str("status"),
                settlementMethod = row.str("settlement_method") ?: pay?.str("settlement_method"),
                payAmount = row.dbl("customer_pay_amount") ?: pay?.dbl("customer_pay_amount"),
                payCurrency = row.str("customer_pay_currency") ?: pay?.str("customer_pay_currency"),
            )
        }
    }

    // ------------------------------------------------------------ 详情
    suspend fun detail(orderId: String): TripDetail? {
        val orderRow = try {
            supabase.postgrest.from("se_order_parents")
                .select(DETAIL_COLUMNS) {
                    filter { eq("id", orderId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        val order = orderRow ?: return null

        val booking = try {
            supabase.postgrest.from("se_bookings")
                .select(
                    Columns.list(
                        "pickup_address", "dropoff_address", "luggage_count",
                        "pickup_points", "dropoff_points",
                    ),
                ) {
                    filter { eq("parent_id", orderId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

        val pay = try {
            supabase.postgrest.from("se_order_bookings")
                .select(Columns.list("settlement_method", "customer_pay_amount", "customer_pay_currency")) {
                    filter { eq("parent_id", orderId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

        // 起点 / 目的地：优先 jsonb 数组，空的话退回单值字段
        val pickups = booking?.places("pickup_points").orEmpty().ifEmpty {
            listOfNotNull(booking?.str("pickup_address"))
        }
        val dropoffs = booking?.places("dropoff_points").orEmpty().ifEmpty {
            listOfNotNull(booking?.str("dropoff_address"))
        }

        return TripDetail(
            orderId = orderId,
            acceptedAt = order.str("accepted_at"),
            completedAt = order.str("completed_at"),
            whatsapp = order.str("customer_whatsapp"),
            wechat = order.str("customer_wechat_id"),
            adults = order.int("adult_count"),
            children = order.int("child_count"),
            luggage = booking?.int("luggage_count"),
            pickups = pickups,
            dropoffs = dropoffs,
            settlementMethod = order.str("settlement_method") ?: pay?.str("settlement_method"),
            payAmount = order.dbl("customer_pay_amount") ?: pay?.dbl("customer_pay_amount"),
            payCurrency = order.str("customer_pay_currency") ?: pay?.str("customer_pay_currency"),
            salary = order.dbl("driver_income"),
            salaryCurrency = order.str("currency"),
            executionStatus = order.str("execution_status"),
            orderStatus = order.str("status"),
        )
    }

    // ------------------------------------------------------------ 内部
    private suspend fun driverProfileId(userId: String): String? = try {
        supabase.postgrest.from("se_driver_profiles")
            .select(Columns.list("id")) {
                filter { eq("user_id", userId) }
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()
            ?.str("id")
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private suspend fun fetchOrders(column: String, value: String): List<JsonObject> = try {
        supabase.postgrest.from("se_order_parents")
            .select(TRIP_COLUMNS) {
                filter { eq(column, value) }
                order("accepted_at", Order.DESCENDING)
                limit(200)
            }
            .decodeList<JsonObject>()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        emptyList()
    }

    private suspend fun byParentId(
        table: String,
        columns: Columns,
        ids: List<String>,
    ): Map<String, JsonObject> {
        if (ids.isEmpty()) return emptyMap()
        val rows = try {
            supabase.postgrest.from(table)
                .select(columns) { filter { isIn("parent_id", ids) } }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
        return rows.mapNotNull { row ->
            val parentId = row.str("parent_id") ?: return@mapNotNull null
            parentId to row
        }.toMap()
    }
}

private val TRIP_COLUMNS = Columns.list(
    "id", "accepted_at", "customer_name", "customer_phone",
    "execution_status", "status", "deleted_at",
    "settlement_method", "customer_pay_amount", "customer_pay_currency",
)

private val DETAIL_COLUMNS = Columns.list(
    "id", "accepted_at", "completed_at", "customer_whatsapp",
    "customer_wechat_id", "adult_count", "child_count", "driver_income",
    "currency", "execution_status", "status",
    "settlement_method", "customer_pay_amount", "customer_pay_currency",
)

/** numeric 字段 */
private fun JsonObject.dbl(key: String): Double? {
    val element = this[key] ?: return null
    return (element as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()
}

/** integer 字段 */
private fun JsonObject.int(key: String): Int? {
    val element = this[key] ?: return null
    return (element as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()?.toInt()
}

/** jsonb 地点数组 → List<String>（兼容 ["A","B"] 和 [{"address":"A"}]） */
private fun JsonObject.places(key: String): List<String> {
    val element = this[key] ?: return emptyList()
    val array = element as? JsonArray ?: return emptyList()
    return array.mapNotNull { item ->
        when (item) {
            is JsonPrimitive -> item.contentOrNull?.takeIf { it.isNotBlank() && it != "null" }
            is JsonObject -> listOf("address", "name", "text", "label", "title", "place", "value")
                .firstNotNullOfOrNull { candidate -> item.str(candidate) }
            else -> null
        }
    }
}

/** 金额显示规则：需要代收才显示币种 + 金额，否则「无需收钱」 */
fun payText(settlementMethod: String?, currency: String?, amount: Double?): String =
    if (settlementMethod == "Cash" && amount != null && amount > 0) {
        (currency.orEmpty().trim() + " " + formatMoney(amount)).trim()
    } else {
        "无需收钱"
    }

/** 120.0 → "120"；12.5 → "12.50" */
fun formatMoney(amount: Double): String {
    val cents = (amount * 100 + 0.5).toLong()
    return if (cents % 100 == 0L) {
        (cents / 100).toString()
    } else {
        (cents / 100).toString() + "." + (cents % 100).toString().padStart(2, '0')
    }
}
