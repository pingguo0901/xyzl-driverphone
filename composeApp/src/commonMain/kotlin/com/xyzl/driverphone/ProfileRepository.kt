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
 * 个人详情（《对方/自己的个人详情页接线表》）
 *
 * 头像 / 名字 / ID：se_users.avatar_url / full_name / display_id
 * 角色标签：se_account_members.role　·　所属公司：se_accounts.display_name（经 se_account_members.account_id）
 * 备用 ID：se_public_profiles.display_id
 * 评分 / 单量：se_driver_profiles.rating / total_trips
 * 最近评价：se_reviews where reviewee_user_id = 我 order by created_at desc limit 3
 */
data class PersonProfile(
    val userId: String,
    val name: String,
    val avatarUrl: String?,
    val roleLabel: String?,
    val displayId: String?,
    val companyName: String?,
    val rating: Double?,
    val totalTrips: Int?,
)

data class ReviewLine(
    val reviewId: String,
    val createdAt: String?,
    val reviewerName: String,
    val rating: Double?,
    val comment: String?,
)

class ProfileRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    /** 对方详情：按名字找（聊天窗点开某人） */
    suspend fun byName(name: String): PersonProfile? {
        val row = try {
            supabase.postgrest.from("se_users")
                .select(Columns.list("id", "full_name", "avatar_url", "display_id")) {
                    filter { eq("full_name", name) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        val userId = row?.str("id") ?: return null
        return build(userId, row)
    }

    suspend fun byUserId(userId: String): PersonProfile? {
        val row = try {
            supabase.postgrest.from("se_users")
                .select(Columns.list("id", "full_name", "avatar_url", "display_id")) {
                    filter { eq("id", userId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        } ?: return null
        return build(userId, row)
    }

    private suspend fun build(userId: String, row: JsonObject): PersonProfile {
        val member = try {
            supabase.postgrest.from("se_account_members")
                .select(Columns.list("role", "account_id")) {
                    filter { eq("user_id", userId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

        val accountId = member?.str("account_id")
        val company = if (accountId == null) {
            null
        } else {
            try {
                supabase.postgrest.from("se_accounts")
                    .select(Columns.list("display_name")) {
                        filter { eq("id", accountId) }
                        limit(1)
                    }
                    .decodeList<JsonObject>()
                    .firstOrNull()
                    ?.str("display_name")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
        }

        val publicProfile = try {
            supabase.postgrest.from("se_public_profiles")
                .select(Columns.list("display_id")) {
                    filter { eq("user_id", userId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

        val driver = try {
            supabase.postgrest.from("se_driver_profiles")
                .select(Columns.list("rating", "total_trips")) {
                    filter { eq("user_id", userId) }
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

        return PersonProfile(
            userId = userId,
            name = row.str("full_name") ?: "",
            avatarUrl = row.str("avatar_url"),
            roleLabel = member?.str("role"),
            displayId = row.str("display_id") ?: publicProfile?.str("display_id"),
            companyName = company,
            rating = driver?.dbl("rating"),
            totalTrips = driver?.intOf("total_trips"),
        )
    }

    /** 最近评价（默认 3 条）：日期 + 评价人 + 星级 + 评论 */
    suspend fun reviews(revieweeUserId: String, limit: Int = 3): List<ReviewLine> {
        val rows = try {
            supabase.postgrest.from("se_reviews")
                .select(Columns.list("id", "reviewer_user_id", "rating", "comment", "created_at")) {
                    filter { eq("reviewee_user_id", revieweeUserId) }
                    order("created_at", Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<JsonObject>()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
        if (rows.isEmpty()) return emptyList()

        val reviewerIds = rows.mapNotNull { it.str("reviewer_user_id") }.distinct()
        val names: Map<String, String> = if (reviewerIds.isEmpty()) {
            emptyMap()
        } else {
            try {
                supabase.postgrest.from("se_users")
                    .select(Columns.list("id", "full_name")) {
                        filter { isIn("id", reviewerIds) }
                    }
                    .decodeList<JsonObject>()
                    .mapNotNull { r ->
                        val id = r.str("id") ?: return@mapNotNull null
                        id to (r.str("full_name") ?: "用户")
                    }
                    .toMap()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyMap()
            }
        }

        return rows.mapNotNull { r ->
            val id = r.str("id") ?: return@mapNotNull null
            ReviewLine(
                reviewId = id,
                createdAt = r.str("created_at"),
                reviewerName = names[r.str("reviewer_user_id").orEmpty()] ?: "用户",
                rating = r.dbl("rating"),
                comment = r.str("comment"),
            )
        }
    }
}

private fun JsonObject.dbl(key: String): Double? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()

private fun JsonObject.intOf(key: String): Int? =
    (this[key] as? JsonPrimitive)?.contentOrNull?.toDoubleOrNull()?.toInt()

/** 1.0 → "1.0"；空 → "-" */
fun formatRating(value: Double?): String {
    if (value == null) return "-"
    val tenths = (value * 10 + 0.5).toLong()
    return (tenths / 10).toString() + "." + (tenths % 10).toString()
}

/** 评价显示：日期 + 评价人 + 星级 + 评论 */
fun formatReview(line: ReviewLine): String {
    val date = formatDate(line.createdAt)
    val stars = line.rating?.let { r ->
        val full = (r + 0.5).toInt().coerceIn(0, 5)
        "★".repeat(full) + "☆".repeat(5 - full)
    } ?: ""
    val comment = line.comment?.takeIf { it.isNotBlank() }?.let { " " + it }.orEmpty()
    return date + " · " + line.reviewerName + " " + stars + comment
}
