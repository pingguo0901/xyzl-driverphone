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
    val user_id: String? = null,
    val order_no: String? = null,
    val status: String? = null,
    val whatsapp: String? = null,
    val wechat: String? = null,
    val adult: Int? = null,
    val child: Int? = null,
    val luggage: Int? = null,
    val trips_date: String? = null,
    val vehicle_count: Int? = null,
    val vehicle_type: String? = null,
    val departure_state: String? = null,
    val departure_address: String? = null,
    val destination_state: String? = null,
    val destination_address: String? = null,
    val notes: String? = null,
    val created_at: String? = null,
    val departure_address_2: String? = null,
    val departure_address_3: String? = null,
    val departure_address_4: String? = null,
    val departure_address_5: String? = null,
    val departure_address_6: String? = null,
    val departure_address_7: String? = null,
    val departure_address_8: String? = null,
    val departure_address_9: String? = null,
    val departure_address_10: String? = null,
    val destination_address_2: String? = null,
    val destination_address_3: String? = null,
    val destination_address_4: String? = null,
    val destination_address_5: String? = null,
    val destination_address_6: String? = null,
    val destination_address_7: String? = null,
    val destination_address_8: String? = null,
    val destination_address_9: String? = null,
    val destination_address_10: String? = null
)

// 订单金额（order_amounts）
@Serializable
data class OrderAmountRow(
    val order_no: String? = null,
    val base_price: Double? = null,
    val car_upgrade_fee: Double? = null,
    val car_reduce_fee: Double? = null,
    val discount: Double? = null,
    val final_amount: Double? = null,
    val status: String? = null
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

// 乘客资料（user_profile）
@Serializable
data class UserProfileRow(
    val user_id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val whatsapp: String? = null,
    val wechat: String? = null,
    val avatar_url: String? = null
)

// 司机薪资（salary_records）
@Serializable
data class SalaryRecordRow(
    val order_no: String? = null,
    val driver_id: String? = null,
    val post_type: String? = null,
    val settle_month: String? = null,
    val base_salary: Double? = null,
    val bonus: Double? = null,
    val deduction: Double? = null,
    val allowance: Double? = null,
    val total_payable: Double? = null,
    val actual_paid: Double? = null,
    val pay_status: String? = null
)

// 车辆信息（vehicle_profile / vehicle_private_data）
@Serializable
data class VehicleRow(
    val vehicle_id: String? = null,
    val vehicle_brand: String? = null,
    val vehicle_model: String? = null,
    val vehicle_type: String? = null,
    val vehicle_plate: String? = null,
    val vehicle_color: String? = null
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

// 行程记录列表项
data class DriverTripSummary(
    val order_no: String = "",
    val departure_address: String = "",
    val destination_address: String = "",
    val status: String = "",
    val final_amount: Double = 0.0,
    val trips_date: String = "",
    val pickup_time: String = "",
    val dropoff_time: String = ""
)

// 司机薪资明细
data class DriverSalary(
    val base_salary: Double = 0.0,
    val bonus: Double = 0.0,
    val deduction: Double = 0.0,
    val allowance: Double = 0.0,
    val total_payable: Double = 0.0,
    val actual_paid: Double = 0.0,
    val pay_status: String = ""
)

// 行程详情页
data class TripDetail(
    val order_no: String = "",
    val status: String = "",
    val trips_date: String = "",
    val whatsapp: String = "",
    val wechat: String = "",
    val adult: Int = 0,
    val child: Int = 0,
    val luggage: Int = 0,
    val vehicle_type: String = "",
    val vehicle_count: Int = 1,
    val departure_addresses: List<String> = emptyList(),
    val destination_addresses: List<String> = emptyList(),
    val notes: String = "",
    val base_price: Double = 0.0,
    val car_upgrade_fee: Double = 0.0,
    val car_reduce_fee: Double = 0.0,
    val discount: Double = 0.0,
    val final_amount: Double = 0.0,
    val pickup_time: String = "",
    val dropoff_time: String = "",
    val customer_name: String = "",
    val customer_email: String = "",
    val salary: DriverSalary = DriverSalary()
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
        val dutyResp = httpRequest(
            "$BASE/rest/v1/duty_records?driver_id=eq.$driverId&order_no=not.is.null&order=created_at.desc&limit=1",
            "GET", headers()
        )
        if (dutyResp.status !in 200..299) return null
        val orderNo = runCatching {
            json.decodeFromString<List<DutyRecordRow>>(dutyResp.body).firstOrNull()?.order_no
        }.getOrNull() ?: return null

        val trip = fetchTrip(orderNo) ?: return null
        val finalAmount = fetchFinalAmount(orderNo)

        return DriverOrder(
            order_no = trip.order_no ?: orderNo,
            departure_address = trip.departure_address ?: "",
            destination_address = trip.destination_address ?: "",
            status = trip.status ?: "",
            final_amount = finalAmount,
            whatsapp = trip.whatsapp ?: "",
            wechat = trip.wechat ?: ""
        )
    }

    /** 查询司机历史行程（duty_records -> order_trips + order_amounts） */
    suspend fun getDriverTrips(driverId: String): List<DriverTripSummary> {
        val dutyResp = httpRequest(
            "$BASE/rest/v1/duty_records?driver_id=eq.$driverId&order_no=not.is.null&select=order_no,start_date,end_date&order=created_at.desc&limit=100",
            "GET", headers()
        )
        if (dutyResp.status !in 200..299) return emptyList()
        val duties = runCatching { json.decodeFromString<List<DutyRecordRow>>(dutyResp.body) }.getOrElse { emptyList() }
        val orderNos = duties.mapNotNull { it.order_no }.distinct()
        if (orderNos.isEmpty()) return emptyList()

        val dutyMap = duties.filter { it.order_no != null }.associateBy { it.order_no }
        val inFilter = orderNos.joinToString(",")

        val tripResp = httpRequest(
            "$BASE/rest/v1/order_trips?order_no=in.($inFilter)&select=order_no,status,departure_address,destination_address,trips_date",
            "GET", headers()
        )
        val trips = if (tripResp.status in 200..299) {
            runCatching { json.decodeFromString<List<OrderTripRow>>(tripResp.body) }.getOrElse { emptyList() }
        } else emptyList()

        val amountResp = httpRequest(
            "$BASE/rest/v1/order_amounts?order_no=in.($inFilter)&select=order_no,final_amount",
            "GET", headers()
        )
        val amountMap = if (amountResp.status in 200..299) {
            runCatching { json.decodeFromString<List<OrderAmountRow>>(amountResp.body) }.getOrElse { emptyList() }
                .filter { it.order_no != null }.associateBy { it.order_no }
        } else emptyMap()

        return trips.map { t ->
            val no = t.order_no ?: ""
            DriverTripSummary(
                order_no = no,
                departure_address = t.departure_address ?: "",
                destination_address = t.destination_address ?: "",
                status = t.status ?: "",
                final_amount = amountMap[no]?.final_amount ?: 0.0,
                trips_date = t.trips_date ?: "",
                pickup_time = dutyMap[no]?.start_date ?: "",
                dropoff_time = dutyMap[no]?.end_date ?: ""
            )
        }
    }

    /** 查询行程详情（order_trips + order_amounts + duty_records + user_profile + salary_records） */
    suspend fun getTripDetail(orderNo: String, driverId: String): TripDetail? {
        val trip = fetchTrip(orderNo) ?: return null
        val amount = fetchAmount(orderNo)

        // duty_records（接客/送达时间）
        val dutyResp = httpRequest(
            "$BASE/rest/v1/duty_records?driver_id=eq.$driverId&order_no=eq.$orderNo&select=start_date,end_date",
            "GET", headers()
        )
        val duty = if (dutyResp.status in 200..299) {
            runCatching { json.decodeFromString<List<DutyRecordRow>>(dutyResp.body).firstOrNull() }.getOrNull()
        } else null

        // user_profile（乘客）
        val userId = trip.user_id ?: ""
        val user = if (userId.isNotBlank()) {
            val userResp = httpRequest("$BASE/rest/v1/user_profile?user_id=eq.$userId", "GET", headers())
            if (userResp.status in 200..299) {
                runCatching { json.decodeFromString<List<UserProfileRow>>(userResp.body).firstOrNull() }.getOrNull()
            } else null
        } else null

        // salary_records（司机该单薪资）
        val salary = fetchSalary(orderNo, driverId)

        val deps = listOfNotNull(
            trip.departure_address,
            trip.departure_address_2, trip.departure_address_3, trip.departure_address_4,
            trip.departure_address_5, trip.departure_address_6, trip.departure_address_7,
            trip.departure_address_8, trip.departure_address_9, trip.departure_address_10
        ).filter { it.isNotBlank() }
        val dests = listOfNotNull(
            trip.destination_address,
            trip.destination_address_2, trip.destination_address_3, trip.destination_address_4,
            trip.destination_address_5, trip.destination_address_6, trip.destination_address_7,
            trip.destination_address_8, trip.destination_address_9, trip.destination_address_10
        ).filter { it.isNotBlank() }

        return TripDetail(
            order_no = trip.order_no ?: orderNo,
            status = trip.status ?: "",
            trips_date = trip.trips_date ?: "",
            whatsapp = trip.whatsapp ?: "",
            wechat = trip.wechat ?: "",
            adult = trip.adult ?: 0,
            child = trip.child ?: 0,
            luggage = trip.luggage ?: 0,
            vehicle_type = trip.vehicle_type ?: "",
            vehicle_count = trip.vehicle_count ?: 1,
            departure_addresses = deps,
            destination_addresses = dests,
            notes = trip.notes ?: "",
            base_price = amount?.base_price ?: 0.0,
            car_upgrade_fee = amount?.car_upgrade_fee ?: 0.0,
            car_reduce_fee = amount?.car_reduce_fee ?: 0.0,
            discount = amount?.discount ?: 0.0,
            final_amount = amount?.final_amount ?: 0.0,
            pickup_time = duty?.start_date ?: "",
            dropoff_time = duty?.end_date ?: "",
            customer_name = user?.username ?: "",
            customer_email = user?.email ?: "",
            salary = salary
        )
    }

    // ===== 内部辅助 =====

    private suspend fun fetchTrip(orderNo: String): OrderTripRow? {
        val resp = httpRequest("$BASE/rest/v1/order_trips?order_no=eq.$orderNo", "GET", headers())
        return if (resp.status in 200..299) {
            runCatching { json.decodeFromString<List<OrderTripRow>>(resp.body).firstOrNull() }.getOrNull()
        } else null
    }

    private suspend fun fetchFinalAmount(orderNo: String): Double {
        return fetchAmount(orderNo)?.final_amount ?: 0.0
    }

    private suspend fun fetchAmount(orderNo: String): OrderAmountRow? {
        val resp = httpRequest("$BASE/rest/v1/order_amounts?order_no=eq.$orderNo", "GET", headers())
        return if (resp.status in 200..299) {
            runCatching { json.decodeFromString<List<OrderAmountRow>>(resp.body).firstOrNull() }.getOrNull()
        } else null
    }

    private suspend fun fetchSalary(orderNo: String, driverId: String): DriverSalary {
        val resp = httpRequest(
            "$BASE/rest/v1/salary_records?driver_id=eq.$driverId&order_no=eq.$orderNo",
            "GET", headers()
        )
        val row = if (resp.status in 200..299) {
            runCatching { json.decodeFromString<List<SalaryRecordRow>>(resp.body).firstOrNull() }.getOrNull()
        } else null
        return DriverSalary(
            base_salary = row?.base_salary ?: 0.0,
            bonus = row?.bonus ?: 0.0,
            deduction = row?.deduction ?: 0.0,
            allowance = row?.allowance ?: 0.0,
            total_payable = row?.total_payable ?: 0.0,
            actual_paid = row?.actual_paid ?: 0.0,
            pay_status = row?.pay_status ?: ""
        )
    }

    /** 查询司机已有权限车辆（按所属公司） */
    suspend fun getDriverVehicles(driverId: String): List<VehicleRow> {
        val companyId = getDriverCompanyId(driverId)
        if (companyId.isNullOrBlank()) return emptyList()
        val resp = httpRequest(
            "$BASE/rest/v1/vehicle_private_data?company_id=eq.$companyId&select=vehicle_id,vehicle_brand,vehicle_model,vehicle_type,vehicle_plate,vehicle_color",
            "GET", headers()
        )
        return if (resp.status in 200..299) {
            runCatching { json.decodeFromString<List<VehicleRow>>(resp.body) }.getOrElse { emptyList() }
        } else emptyList()
    }

    /** 直接添加车辆到公司（vehicle_private_data） */
    suspend fun addVehicle(vehicleId: String, companyId: String): Boolean {
        val payload = """{"vehicle_id":"$vehicleId","company_id":"$companyId"}"""
        val resp = httpRequest("$BASE/rest/v1/vehicle_private_data", "POST", headers(), payload)
        return resp.status in 200..299
    }

    private suspend fun getDriverCompanyId(driverId: String): String? {
        val resp = httpRequest("$BASE/rest/v1/driver_profile?driver_id=eq.$driverId&select=company_id", "GET", headers())
        return if (resp.status in 200..299) {
            runCatching { json.decodeFromString<List<DriverProfileRow>>(resp.body).firstOrNull()?.company_id }.getOrNull()
        } else null
    }
}
