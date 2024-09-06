package example.com.model

import kotlinx.serialization.Serializable

@Serializable
data class LikeParams(
    val eventId: Long,
    val userId: Long
)

@Serializable
data class LikeResponse(
    val success: Boolean,
    val message: String? = null
)