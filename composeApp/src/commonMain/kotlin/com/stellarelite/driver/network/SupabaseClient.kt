package com.stellarelite.driver.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ===================== 数据模型 =====================

@Serializable
data class DriverProfileRow(
    val driver_id: String? = null,
    val status: String? = null,
    val company_id: String? = null,
    val username: String? = null
)

// 订单详情（order_trips）
@Serializable
data class OrderTripRow(
    val order_no: String? = null,
    val status: String? = null,
    val whatsapp: String? = null,
    val wechat: String? = null,
    val departure_address: String? = null,
    val destination_address: String? = null,
    val trips_date: String? = null,
    val created_at: String? = null
)

// 订单金额（order_amounts）
@Serializable
data class OrderAmountRow(
    val order_no: String? = null,
    val final_amount: Double? = null
)

// 司机值班/任务记录（duty_records）
@Serializable
data class DutyRecordRow(
    val uuid: String? = null,
    val driver_id: String? = null,
    val order_no: String? = null,
    val record_type: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val created_at: String? = null
)

// 组合后的「当前任务」展示模型
data class DriverOrder(
    val order_no: String = "",
    val departure_address: String = "",
    val destination_address: String = "",
    val status: String = "",
    val final_amount: Double = 0.0,
    val whatsapp: String = "",
    val wechat: String = ""
)

// ===================== 客户端 =====================

object SupabaseClient {
    private val json = Json { ignoreUnknownKeys = true }
    private const val BASE = SupabaseConfig.BASE_URL

    private fun headers() = mapOf(
        "apikey" to SupabaseConfig.ANON_KEY,
        "Authorization" to "Bearer ${SupabaseConfig.ANON_KEY}",
        "Content-Type" to "application/json"
    )

    private fun patchHeaders() = headers() + ("Prefer" to "return=minimal")

    /** 查询司机值班状态（driver_profile.status） */
    suspend fun getDriverStatus(driverId: String): String? {
        val resp = httpRequest("$BASE/rest/v1/driver_profile?driver_id=eq.$driverId&select=status", "GET", headers())
        return if (resp.status in 200..299) {
            runCatching { json.decodeFromString<List<DriverProfileRow>>(resp.body).firstOrNull()?.status }.getOrNull()
        } else null
    }

    /** 更新司机值班状态（driver_profile.status） */
    suspend fun updateDriverStatus(driverId: String, status: String): Boolean {
        val payload = """{"status":"$status"}"""
        val resp = httpRequest("$BASE/rest/v1/driver_profile?driver_id=eq.$driverId", "PATCH", patchHeaders(), payload)
        return resp.status in 200..299
    }

    /** 查询司机当前任务（duty_records -> order_trips + order_amounts） */
    suspend fun getDriverCurrentOrder(driverId: String): DriverOrder? {
        // 1. 找司机最新的带订单号的值班/任务记录
        val dutyResp = httpRequest(
            "$BASE/rest/v1/duty_records?driver_id=eq.$driverId&order_no=not.is.null&order=created_at.desc&limit=1",
            "GET", headers()
        )
        if (dutyResp.status !in 200..299) return null
        val orderNo = runCatching {
            json.decodeFromString<List<DutyRecordRow>>(dutyResp.body).firstOrNull()?.order_no
        }.getOrNull() ?: return null

        // 2. 订单详情
        val tripResp = httpRequest("$BASE/rest/v1/order_trips?order_no=eq.$orderNo", "GET", headers())
        val trip = if (tripResp.status in 200..299) {
            runCatching { json.decodeFromString<List<OrderTripRow>>(tripResp.body).firstOrNull() }.getOrNull()
        } else null

        // 3. 订单金额
        val amountResp = httpRequest("$BASE/rest/v1/order_amounts?order_no=eq.$orderNo", "GET", headers())
        val finalAmount = if (amountResp.status in 200..299) {
            runCatching { json.decodeFromString<List<OrderAmountRow>>(amountResp.body).firstOrNull()?.final_amount }.getOrNull() ?: 0.0
        } else 0.0

        return DriverOrder(
            order_no = trip?.order_no ?: orderNo,
            departure_address = trip?.departure_address ?: "",
            destination_address = trip?.destination_address ?: "",
            status = trip?.status ?: "",
            final_amount = finalAmount,
            whatsapp = trip?.whatsapp ?: "",
            wechat = trip?.wechat ?: ""
        )
    }
}
