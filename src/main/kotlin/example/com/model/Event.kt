package example.com.model

import kotlinx.serialization.Serializable

@Serializable
data class EventTextParams(
    val eventTitle: String,
    val description: String,
    val isScheduled: Boolean,
    val dateTime: String?,
    val userId: Long,
    val organizationName: String
)

@Serializable
data class Event(
    val eventId: Long,
    val eventTitle: String,
    val description: String,
    val isScheduled: Boolean,
    val dateTime: String?,
    val eventImagePrimary: String,
    val createdAt: String,
    val likesCount: Int,
    val commentsCount: Int,
    val viewsCount: Int,
    val userId: Long,
    val organizationName: String,
    val organizationProfileImageUrl: String?,
    val isLiked: Boolean,
    val isOwnEvent: Boolean
)

@Serializable
data class EventResponse(
    val success: Boolean,
    val event: Event? = null,
    val message: String? = null
)

@Serializable
data class EventsResponse(
    val success: Boolean,
    val events: List<Event> = listOf(),
    val message: String? = null
)