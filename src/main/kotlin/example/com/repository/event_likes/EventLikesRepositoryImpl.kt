package example.com.repository.event_likes

import example.com.dao.event.EventDao
import example.com.dao.event_likes.EventLikesDao
import example.com.model.LikeParams
import example.com.model.LikeResponse
import example.com.util.Response
import io.ktor.http.*

class EventLikesRepositoryImpl(
    private val eventLikesDao: EventLikesDao,
    private val eventDao: EventDao
): EventLikesRepository {

    override suspend fun addLike(params: LikeParams): Response<LikeResponse> {
        val likeExists = eventLikesDao.isEventLikedByUser(eventId = params.eventId, userId = params.userId)

        return if (likeExists) {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = LikeResponse(success = false, message = "Post is already liked.")
            )
        } else {
            val eventLiked = eventLikesDao.addLike(eventId = params.eventId, userId = params.userId)

            if (eventLiked) {
                eventDao.updateLikesCount(eventId = params.eventId)
                Response.Success(
                    data = LikeResponse(success = true)
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = LikeResponse(success = false, message = "Unexpected database error, try again!")
                )
            }
        }
    }

    override suspend fun removeLike(params: LikeParams): Response<LikeResponse> {
        val likeExists = eventLikesDao.isEventLikedByUser(eventId = params.eventId, userId = params.userId)

        return if (likeExists) {
            val likeRemoved = eventLikesDao.removeLike(eventId = params.eventId, userId = params.userId)

            if (likeRemoved) {
                eventDao.updateLikesCount(eventId = params.eventId, decrement = true)
                Response.Success(
                    data = LikeResponse(success = true)
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = LikeResponse(success = false, message = "Unexpected database error, try again!")
                )
            }
        } else {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = LikeResponse(success = false, message = "Like was not found, it may have been removed already.")
            )
        }
    }

}