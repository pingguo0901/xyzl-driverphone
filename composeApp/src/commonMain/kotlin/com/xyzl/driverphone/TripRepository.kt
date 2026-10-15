package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * 行程（《行程页面接线表》）
 *
 * Tab：全部 / 已完成 / 未完成 / 已取消（已取消 = se_order_parents.status = 'cancelled'）
 * 列表：where assigned_driver_id = 我 order by accepted_at desc
 * 起点 / 目的地：se_bookings（parent_id 关联订单）
 * 金额：settlement_method='Cash' 且 customer_pay_amount > 0 时显示 customer_pay_currency + 金额，否则「无需收钱」
 *   —— 注意：这三个字段在库里的实际位置是 se_order_bookings（同样用 parent_id 关联），不是 se_order_parents
 */
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

    /** 金额列显示什么 */
    val moneyText: String
        get() {
            val amount = payAmount
            return if (settlementMethod == "Cash" && amount != null && amount > 0) {
                (payCurrency.orEmpty().trim() + " " + formatMoney(amount)).trim()
            } else {
                "无需收钱"
            }
        }
}

class TripRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

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
                settlementMethod = pay?.str("settlement_method"),
                payAmount = pay?.dbl("customer_pay_amount"),
                payCurrency = pay?.str("customer_pay_currency"),
            )
        }
    }

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
)

/** numeric 字段 */
private fun JsonObject.dbl(key: String): Double? {
    val element = this[key] ?: return null
    return (element as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()
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
