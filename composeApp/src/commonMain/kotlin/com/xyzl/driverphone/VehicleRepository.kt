package com.xyzl.driverphone

import com.xyzl.driverphone.screens.currentTimeParts
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

private const val BUCKET_VEHICLE_PHOTOS = "se_vehicle-photos"
private const val BUCKET_VEHICLE_DOCS = "se_vehicle-documents"

/** 下拉项（品牌 / 型号 / 车款） */
data class PickerOption(val id: String, val label: String)

/** 一台车（列表显示用） */
data class VehicleCard(
    val id: String,
    val brand: String,
    val model: String,
    val trim: String,
    val plate: String,
    val verifyAt: String?,
    val verifiedAt: String?,
    val status: String?,
)

/** 增加车辆页收集到的资料 */
data class NewVehicleInput(
    val brandId: String,
    val modelId: String,
    val trimId: String?,
    val plate: String,
    val isOwner: Boolean,
    val frontPhoto: String?,
    val backPhoto: String?,
    val frontSeatPhoto: String?,
    val backSeatPhoto: String?,
    val roadTaxFile: String?,
    val registrationCardFile: String?,
    val insuranceFile: String?,
    val ownerIcFile: String?,
    val ownerIcUnavailable: Boolean,
)

/**
 * 车辆信息（《车辆信息页面接线表》）
 *
 * - 品牌 se_vehicle_brands（is_active=true）→ 型号 se_vehicle_models（brand_id）→ 车款 se_vehicle_trims（model_id）
 * - 我的车辆 / 待通过验证：se_vehicle_profiles where owner_id = 我的 account_id
 * - 提交：se_vehicle_profiles INSERT 1 条 + se_vehicle_documents INSERT 3~4 条
 */
class VehicleRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    /** 我的账户 id */
    suspend fun accountId(userId: String): String? = safe {
        supabase.postgrest.from("se_account_members")
            .select(Columns.list("account_id")) { filter { eq("user_id", userId) }; limit(1) }
            .decodeList<JsonObject>()
            .firstOrNull()
            ?.s("account_id")
    }

    /** 品牌下拉：se_vehicle_brands where is_active = true */
    suspend fun brands(): List<PickerOption> = safe {
        supabase.postgrest.from("se_vehicle_brands")
            .select(Columns.list("id", "brand_name")) {
                filter { eq("is_active", true) }
                order("sort_order", Order.ASCENDING)
            }
            .decodeList<JsonObject>()
            .mapNotNull { r ->
                val id = r.s("id") ?: return@mapNotNull null
                PickerOption(id, r.s("brand_name") ?: "未命名")
            }
    } ?: emptyList()

    /** 型号下拉：where brand_id = 选中的品牌 */
    suspend fun models(brandId: String): List<PickerOption> = safe {
        supabase.postgrest.from("se_vehicle_models")
            .select(Columns.list("id", "model_name")) {
                filter {
                    eq("brand_id", brandId)
                    eq("is_active", true)
                }
            }
            .decodeList<JsonObject>()
            .mapNotNull { r ->
                val id = r.s("id") ?: return@mapNotNull null
                PickerOption(id, r.s("model_name") ?: "未命名")
            }
    } ?: emptyList()

    /** 车款下拉：where model_id = 选中的型号 */
    suspend fun trims(modelId: String): List<PickerOption> = safe {
        supabase.postgrest.from("se_vehicle_trims")
            .select(Columns.list("id", "trim_name")) {
                filter {
                    eq("model_id", modelId)
                    eq("is_active", true)
                }
            }
            .decodeList<JsonObject>()
            .mapNotNull { r ->
                val id = r.s("id") ?: return@mapNotNull null
                PickerOption(id, r.s("trim_name") ?: "未命名")
            }
    } ?: emptyList()

    /** 车辆列表：verification_status = approved / pending */
    suspend fun vehicles(accountId: String, status: String): List<VehicleCard> {
        val rows = safe {
            supabase.postgrest.from("se_vehicle_profiles")
                .select(
                    Columns.list(
                        "id", "plate_no", "brand_id", "model_id", "trim_id",
                        "verification_status", "verification_submitted_at", "verified_at",
                    ),
                ) {
                    filter {
                        eq("owner_id", accountId)
                        eq("verification_status", status)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<JsonObject>()
        } ?: emptyList()
        if (rows.isEmpty()) return emptyList()

        val brandNames = nameMap("se_vehicle_brands", "brand_name")
        val modelNames = nameMap("se_vehicle_models", "model_name")
        val trimNames = nameMap("se_vehicle_trims", "trim_name")

        return rows.map { r ->
            val brandId = r.s("brand_id")
            val modelId = r.s("model_id")
            val trimId = r.s("trim_id")
            VehicleCard(
                id = r.s("id").orEmpty(),
                brand = brandId?.let { brandNames[it] } ?: "-",
                model = modelId?.let { modelNames[it] } ?: "-",
                trim = trimId?.let { trimNames[it] } ?: "-",
                plate = r.s("plate_no") ?: "-",
                verifyAt = r.s("verification_submitted_at"),
                verifiedAt = r.s("verified_at"),
                status = r.s("verification_status"),
            )
        }
    }

    private suspend fun nameMap(table: String, nameColumn: String): Map<String, String> = safe {
        supabase.postgrest.from(table)
            .select(Columns.list("id", nameColumn))
            .decodeList<JsonObject>()
            .mapNotNull { r ->
                val id = r.s("id") ?: return@mapNotNull null
                id to (r.s(nameColumn) ?: "-")
            }
            .toMap()
    } ?: emptyMap()

    /** 提交新增车辆：INSERT 1 条车 + 3~4 条证件 */
    suspend fun submitVehicle(
        userId: String,
        accountId: String,
        input: NewVehicleInput,
    ): String? {
        val photoPaths = mutableListOf<String>()

        suspend fun uploadPhoto(uri: String?, name: String): String? {
            if (uri.isNullOrBlank()) return null
            val path = uploadAsset(uri, BUCKET_VEHICLE_PHOTOS, userId, name)
            if (path == null) return null
            photoPaths += path
            return path
        }

        val front = uploadPhoto(input.frontPhoto, "front")
        val back = uploadPhoto(input.backPhoto, "back")
        val frontSeat = uploadPhoto(input.frontSeatPhoto, "front-seat")
        val backSeat = uploadPhoto(input.backSeatPhoto, "back-seat")

        val roadTax = uploadAsset(input.roadTaxFile, BUCKET_VEHICLE_DOCS, userId, "road-tax")
        val regCard = uploadAsset(input.registrationCardFile, BUCKET_VEHICLE_DOCS, userId, "registration-card")
        val insurance = uploadAsset(input.insuranceFile, BUCKET_VEHICLE_DOCS, userId, "insurance")
        val ownerIc = if (input.isOwner) null else uploadAsset(input.ownerIcFile, BUCKET_VEHICLE_DOCS, userId, "owner-ic")

        val vehicleId = try {
            supabase.postgrest.from("se_vehicle_profiles").insert(
                buildJsonObject {
                    put("account_id", accountId)
                    put("owner_type", "personal")
                    put("owner_id", accountId)
                    put("plate_no", input.plate)
                    put("brand_id", input.brandId)
                    put("model_id", input.modelId)
                    if (!input.trimId.isNullOrBlank()) put("trim_id", input.trimId)
                    put("is_owner", input.isOwner)
                    put("verification_status", "pending")
                    put("verification_submitted_at", "now")
                    if (front != null) put("front_photo_url", front)
                    if (back != null) put("back_photo_url", back)
                    if (frontSeat != null) put("front_seat_photo_url", frontSeat)
                    if (backSeat != null) put("back_seat_photo_url", backSeat)
                    put("owner_ic_unavailable", input.ownerIcUnavailable)
                },
            ) {
                select()
            }.decodeList<JsonObject>().firstOrNull()?.s("id")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return friendly(e.message)
        } ?: return "车辆登记失败：没拿到车辆记录号"

        val docs = buildList {
            if (roadTax != null) add("road_tax" to roadTax)
            if (regCard != null) add("registration_card" to regCard)
            if (insurance != null) add("insurance" to insurance)
            if (ownerIc != null) add("owner_ic" to ownerIc)
        }

        return try {
            docs.forEach { (type, path) ->
                supabase.postgrest.from("se_vehicle_documents").insert(
                    buildJsonObject {
                        put("account_id", accountId)
                        put("vehicle_id", vehicleId)
                        put("document_type", type)
                        put("file_url", path)
                        put("status", "pending")
                        put("uploaded_by_user_id", userId)
                    },
                )
            }
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            "车辆已登记，但证件登记失败：" + (e.message ?: "未知错误").take(100)
        }
    }

    /** 上传到 Storage，返回公开 URL；失败 null */
    private suspend fun uploadAsset(uri: String?, bucket: String, userId: String, name: String): String? {
        if (uri.isNullOrBlank()) return null
        return try {
            val bytes = readUriBytes(uri)
            if (bytes == null || bytes.isEmpty()) return null
            val ext = when {
                uri.contains(".png", true) -> "png"
                uri.contains(".pdf", true) -> "pdf"
                else -> "jpg"
            }
            val (date, time) = currentTimeParts()
            val stamp = date.replace("-", "") + time.replace(":", "")
            val path = userId + "/" + name + "-" + stamp + "." + ext
            supabase.storage.from(bucket).upload(path, bytes) { upsert = true }
            path
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    private inline fun <T> safe(block: () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private fun friendly(message: String?): String {
        val m = message.orEmpty()
        return when {
            m.contains("duplicate key", true) -> "这台车（同一个车牌）已经登记过了"
            m.contains("violates not-null", true) -> "有必填字段没填"
            m.contains("row-level security", true) || m.contains("permission denied", true) -> "没有权限提交，先看下数据库策略"
            m.isBlank() -> "提交失败，稍后重试"
            else -> "提交失败：" + m.take(120)
        }
    }
}

private fun JsonObject.s(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() && it != "null" }
