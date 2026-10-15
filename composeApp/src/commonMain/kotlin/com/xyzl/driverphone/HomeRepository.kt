package com.xyzl.driverphone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

/**
 * 首页数据（接线表：首页 / 更换值班车辆弹窗）
 *
 * 铃铛未读  se_notifications.count(user_id = 我, is_read = false)
 * 接单开关  se_driver_profiles.is_online
 * 值班车辆  se_driver_profiles.current_vehicle_id → se_vehicle_profiles(plate_no / make / model)
 * 当前任务  se_order_parents(accepted_at / 客户信息 / execution_status) + se_bookings(pickup / dropoff)
 * 滑块      se_order_parents.execution_status + execution_status_updated_at
 */

/** 值班车辆 */
data class HomeVehicle(
    val id: String,
    val plateNo: String,
    val make: String,
    val model: String,
) {
    /** 卡片主文案：车牌 · 品牌 型号 */
    val title: String
        get() = listOf(plateNo, (make + " " + model))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(" · ")
}

/** 当前任务 */
data class HomeTask(
    val orderId: String,
    val acceptedAt: String?,
    val pickup: String?,
    val dropoff: String?,
    val customerName: String?,
    val customerPhone: String?,
    val customerWechat: String?,
    val customerWhatsapp: String?,
    /** 待完成 / 进行中 / 已完成 / 迟到 / 超时 */
    val executionStatus: String?,
)

/** 首页一屏数据 */
data class HomeSnapshot(
    val driverProfileId: String? = null,
    val accountId: String? = null,
    val isOnline: Boolean = false,
    val unreadCount: Int = 0,
    val vehicle: HomeVehicle? = null,
    val task: HomeTask? = null,
    val vehicles: List<HomeVehicle> = emptyList(),
)

/** 执行状态常量（se_order_parents.execution_status） */
object ExecutionStatus {
    const val PENDING = "待完成"
    const val ONGOING = "进行中"
    const val DONE = "已完成"
    const val LATE = "迟到"
    const val OVERDUE = "超时"
}

class HomeRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    // ------------------------------------------------------------ 读：整屏
    suspend fun load(userId: String): HomeSnapshot {
        val driver = driverRow(userId)
        val accountId = driver?.str("account_id") ?: userId
        val driverProfileId = driver?.str("id")
        val currentVehicleId = driver?.str("current_vehicle_id")

        val vehicles = vehicleList(accountId)
        val current = vehicles.firstOrNull { it.id == currentVehicleId }
            ?: currentVehicleId?.let { fetchVehicle(it) }

        return HomeSnapshot(
            driverProfileId = driverProfileId,
            accountId = accountId,
            isOnline = driver?.bool("is_online") ?: false,
            unreadCount = unreadCount(userId),
            vehicle = current,
            task = currentTask(userId, driverProfileId),
            vehicles = vehicles,
        )
    }

    // ------------------------------------------------------------ 写：接单开关
    suspend fun setOnline(driverProfileId: String, online: Boolean): Boolean = updateOne(
        table = "se_driver_profiles",
        id = driverProfileId,
        values = buildJsonObject { put("is_online", online) },
    )

    // ------------------------------------------------------------ 写：更换值班车辆
    suspend fun setCurrentVehicle(driverProfileId: String, vehicleId: String): Boolean = updateOne(
        table = "se_driver_profiles",
        id = driverProfileId,
        values = buildJsonObject { put("current_vehicle_id", vehicleId) },
    )

    // ------------------------------------------------------------ 写：滑块推进执行状态
    suspend fun setExecutionStatus(orderId: String, status: String): Boolean = updateOne(
        table = "se_order_parents",
        id = orderId,
        values = buildJsonObject {
            put("execution_status", status)
            // 'now' 是 PostgreSQL 认的 timestamptz 字面量，让数据库自己盖时间戳
            put("execution_status_updated_at", "now")
        },
    )

    // ------------------------------------------------------------ 内部查询
    private suspend fun driverRow(userId: String): JsonObject? = runQuery {
        supabase.postgrest.from("se_driver_profiles")
            .select(Columns.list("id", "account_id", "is_online", "current_vehicle_id")) {
                filter { eq("user_id", userId) }
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()
    }

    private suspend fun unreadCount(userId: String): Int = runQuery {
        supabase.postgrest.from("se_notifications")
            .select(Columns.list("id")) {
                filter {
                    eq("user_id", userId)
                    eq("is_read", false)
                }
            }
            .decodeList<JsonObject>()
            .size
    } ?: 0

    /** 车辆列表：owner_id = 我的 account_id（另外把 account_id 也算上，避免两套归属口径不一致） */
    private suspend fun vehicleList(accountId: String): List<HomeVehicle> {
        val out = LinkedHashMap<String, HomeVehicle>()
        for (column in listOf("owner_id", "account_id")) {
            val rows = runQuery {
                supabase.postgrest.from("se_vehicle_profiles")
                    .select(Columns.list("id", "plate_no", "make", "model")) {
                        filter { eq(column, accountId) }
                        order("plate_no", Order.ASCENDING)
                    }
                    .decodeList<JsonObject>()
            } ?: continue
            for (row in rows) {
                val v = toVehicle(row) ?: continue
                out[v.id] = v
            }
        }
        return out.values.toList()
    }

    private suspend fun fetchVehicle(vehicleId: String): HomeVehicle? = runQuery {
        supabase.postgrest.from("se_vehicle_profiles")
            .select(Columns.list("id", "plate_no", "make", "model")) {
                filter { eq("id", vehicleId) }
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()
    }?.let { toVehicle(it) }

    /** 当前任务：我最新的接单（没有就用派给我的单） */
    private suspend fun currentTask(userId: String, driverProfileId: String?): HomeTask? {
        var order = latestOrder("accepted_by_user_id", userId)
        if (order == null && driverProfileId != null) {
            order = latestOrder("assigned_driver_id", driverProfileId)
        }
        val row = order ?: return null
        val orderId = row.str("id") ?: return null
        val booking = firstBooking(orderId)
        return HomeTask(
            orderId = orderId,
            acceptedAt = row.str("accepted_at"),
            pickup = booking?.str("pickup_address"),
            dropoff = booking?.str("dropoff_address"),
            customerName = row.str("customer_name"),
            customerPhone = row.str("customer_phone"),
            customerWechat = row.str("customer_wechat_id"),
            customerWhatsapp = row.str("customer_whatsapp"),
            executionStatus = row.str("execution_status"),
        )
    }

    private suspend fun latestOrder(column: String, value: String): JsonObject? = runQuery {
        supabase.postgrest.from("se_order_parents")
            .select(ORDER_COLUMNS) {
                filter { eq(column, value) }
                order("accepted_at", Order.DESCENDING)
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()
    }

    private suspend fun firstBooking(orderId: String): JsonObject? = runQuery {
        supabase.postgrest.from("se_bookings")
            .select(Columns.list("pickup_address", "dropoff_address")) {
                filter { eq("parent_id", orderId) }
                limit(1)
            }
            .decodeList<JsonObject>()
            .firstOrNull()
    }

    // ------------------------------------------------------------ 统一异常包装
    private suspend fun <T> runQuery(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private suspend fun updateOne(table: String, id: String, values: JsonObject): Boolean = try {
        supabase.postgrest.from(table).update(values) { filter { eq("id", id) } }
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    private fun toVehicle(row: JsonObject): HomeVehicle? {
        val id = row.str("id") ?: return null
        return HomeVehicle(
            id = id,
            plateNo = row.str("plate_no").orEmpty(),
            make = row.str("make").orEmpty(),
            model = row.str("model").orEmpty(),
        )
    }
}

private val ORDER_COLUMNS = Columns.list(
    "id", "accepted_at", "customer_name", "customer_phone",
    "customer_wechat_id", "customer_whatsapp", "execution_status",
)

/** 取字符串字段：null / JsonNull / 空串都当没有 */
internal fun JsonObject.str(key: String): String? {
    val element = this[key] ?: return null
    if (element is JsonNull) return null
    val content = (element as? JsonPrimitive)?.contentOrNull ?: return null
    return content.takeIf { it.isNotBlank() && it != "null" }
}

/** 取布尔字段 */
internal fun JsonObject.bool(key: String): Boolean =
    (this[key] as? JsonPrimitive)?.booleanOrNull ?: false

/**
 * 时间戳显示：Supabase 回的是 UTC（2026-10-15T14:30:00+00:00），
 * 司机在马来西亚，统一按 UTC+8 显示成 15/10/2026 22:30。
 */
fun formatHomeTimestamp(iso: String?): String? {
    if (iso == null || iso.length < 16) return null
    val year = iso.substring(0, 4).toIntOrNull() ?: return null
    val month = iso.substring(5, 7).toIntOrNull() ?: return null
    val day = iso.substring(8, 10).toIntOrNull() ?: return null
    val hour = iso.substring(11, 13).toIntOrNull() ?: return null
    val minute = iso.substring(14, 16).toIntOrNull() ?: return null

    val shifted = hour * 60 + minute + 8 * 60
    val carryDays = shifted / (24 * 60)
    val localMinutes = shifted % (24 * 60)

    var y = year
    var m = month
    var d = day + carryDays
    val dim = daysInMonth(y, m)
    if (d > dim) {
        d -= dim
        m += 1
        if (m > 12) {
            m = 1
            y += 1
        }
    }
    return pad2(d) + "/" + pad2(m) + "/" + y + " " + pad2(localMinutes / 60) + ":" + pad2(localMinutes % 60)
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    else -> 31
}

private fun pad2(value: Int): String = if (value < 10) "0" + value else value.toString()

/** 电话 / WhatsApp 号码 → wa.me 用的纯数字（本地 0 开头自动补 60） */
fun waDigits(raw: String?): String? {
    val digits = raw?.filter { it.isDigit() }?.trim().orEmpty()
    if (digits.isEmpty()) return null
    return if (digits.startsWith("0")) "60" + digits.drop(1) else digits
}
