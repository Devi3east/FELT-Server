package example.com.repository.event

import example.com.dao.event.EventDao
import example.com.dao.event.EventRow
import example.com.dao.event_likes.EventLikesDao
import example.com.dao.follows.FollowsDao
import example.com.dao.user.UserDao
import example.com.model.Event
import example.com.model.EventResponse
import example.com.model.EventTextParams
import example.com.model.EventsResponse
import example.com.util.Response
import io.ktor.http.*

class EventRepositoryImpl(
    private val eventDao: EventDao,
    private val followsDao: FollowsDao,
    private val eventLikesDao: EventLikesDao
): EventRepository {
    override suspend fun createEvent(eventImagePrimary: String, eventTextParams: EventTextParams): Response<EventResponse> {
        val eventIsCreated = eventDao.createEvent(
            eventTitle = eventTextParams.eventTitle,
            description = eventTextParams.description,
            isScheduled = eventTextParams.isScheduled,
            dateTime = eventTextParams.dateTime,
            eventImagePrimary = eventImagePrimary,
            userId = eventTextParams.userId,
            organizationName = eventTextParams.organizationName
        )

        return if (eventIsCreated) {
            Response.Success(
                data = EventResponse(success = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = EventResponse(
                    success = false,
                    message = "Event could not be inserted in the database."
                )
            )
        }
    }

    override suspend fun getFeedEvents(userId: Long, pageNumber: Int, pageSize: Int): Response<EventsResponse> {
        val followingUsers = followsDao.getAllFollowing(userId = userId).toMutableList()
        followingUsers.add(userId)

        val eventsRows = eventDao.getFeedsEvent(
            userId = userId,
            follows = followingUsers,
            pageNumber = pageNumber,
            pageSize = pageSize
        )

        val events = eventsRows.map {
            toEvent(
                eventRow = it,
                isEventLiked = eventLikesDao.isEventLikedByUser(eventId = it.eventId, userId = userId),
                isOwnEvent = it.userId == userId
            )
        }

        return Response.Success(
            data = EventsResponse(
                success = true,
                events = events
            )
        )
    }

    override suspend fun getEventByUser(
        eventsOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int
    ): Response<EventsResponse> {
        val eventsRows = eventDao.getEventByUser(
            userId = eventsOwnerId,
            pageNumber = pageNumber,
            pageSize = pageSize
        )

        val events = eventsRows.map {
            toEvent(
                eventRow = it,
                isEventLiked = eventLikesDao.isEventLikedByUser(eventId = it.eventId, userId = currentUserId),
                isOwnEvent = it.userId == currentUserId
            )
        }

        return Response.Success(
            data = EventsResponse(
                success = true,
                events = events
            )
        )
    }

    override suspend fun getEvent(eventId: Long, currentUserId: Long): Response<EventResponse> {
        val event = eventDao.getEvent(eventId = eventId)

        return if (event == null) {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = EventResponse(
                    success = false,
                    message = "Could not find event in the database."
                )
            )
        } else {
            val isEventLiked = eventLikesDao.isEventLikedByUser(eventId, currentUserId)
            val isOwnEvent = event.userId == currentUserId
            Response.Success(
                data = EventResponse(success = true, toEvent(event, isEventLiked = isEventLiked, isOwnEvent = isOwnEvent))
            )
        }
    }

    override suspend fun deleteEvent(eventId: Long): Response<EventResponse> {
        val eventIsDeleted = eventDao.deleteEvent(
            eventId = eventId
        )

        return if (eventIsDeleted) {
            Response.Success(
                data = EventResponse(success = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = EventResponse(
                    success = false,
                    message = "Event could not be deleted from the database."
                )
            )
        }
    }

    private fun toEvent(eventRow: EventRow, isEventLiked: Boolean, isOwnEvent: Boolean): Event {
        return Event(
            eventId = eventRow.eventId,
            eventTitle = eventRow.eventTitle,
            description = eventRow.description,
            isScheduled = eventRow.isScheduled,
            dateTime = eventRow.dateTime,
            eventImagePrimary = eventRow.eventImagePrimary,
            createdAt = eventRow.createdAt,
            likesCount = eventRow.likesCount,
            commentsCount = eventRow.commentsCount,
            viewsCount = eventRow.viewsCount,
            userId = eventRow.userId,
            organizationName = eventRow.organizationName,
            organizationProfileImageUrl = eventRow.organizationProfileImageUrl,
            isLiked = isEventLiked,
            isOwnEvent = isOwnEvent
        )
    }

}