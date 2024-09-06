package example.com.model

import kotlinx.serialization.Serializable

@Serializable
data class NewCommentParams(
    val content: String,
    val eventId: Long,
    val userId: Long
)

@Serializable
data class RemoveCommentParams(
    val eventId: Long,
    val commentId: Long,
    val userId: Long
)

@Serializable
data class EventComment(
    val commentId: Long,
    val content: String,
    val eventId: Long,
    val userId: Long,
    val name: String,
    val organizationName: String?,
    val profileImageUrl: String?,
    val createdAt: String
)

@Serializable
data class CommentResponse(
    val success: Boolean,
    val comment: EventComment? = null,
    val message: String? = null
)

@Serializable
data class GetCommentsResponse(
    val success: Boolean,
    val comments: List<EventComment> = listOf(),
    val message: String? = null
)