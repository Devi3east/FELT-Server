package example.com.repository.event_comments

import example.com.dao.event.EventDao
import example.com.dao.event_comments.EventCommentRow
import example.com.dao.event_comments.EventCommentsDao
import example.com.model.*
import example.com.util.Response
import io.ktor.http.*

class EventCommentsRepositoryImpl(
    private val eventCommentsDao: EventCommentsDao,
    private val eventDao: EventDao
): EventCommentsRepository {

    override suspend fun addComment(params: NewCommentParams): Response<CommentResponse> {
        val eventCommentRow = eventCommentsDao.addComment(
            eventId = params.eventId,
            userId = params.userId,
            content = params.content
        )

        return if (eventCommentRow == null) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = CommentResponse(success = false, message = "Could not insert comment into the database.")
            )
        } else {
            eventDao.updateCommentsCount(eventId = params.eventId)
            Response.Success(
                data = CommentResponse(success = true, comment = toEventComment(eventCommentRow))
            )
        }
    }

    override suspend fun removeComment(params: RemoveCommentParams): Response<CommentResponse> {
        val commentRow = eventCommentsDao.findComment(commentId = params.commentId, eventId = params.eventId)

        return if (commentRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = CommentResponse(success = false, message = "Comment ${params.commentId} was not found.")
            )
        } else {
            val eventOwnerId = eventDao.getEvent(eventId = params.eventId)!!.userId

            if (params.userId != commentRow.userId && params.userId != eventOwnerId) {
                Response.Error(
                    code = HttpStatusCode.Forbidden,
                    data = CommentResponse(
                        success = false,
                        message = "User ${params.userId} cannot delete comment ${params.commentId}."
                    )
                )
            } else {
                val commentWasRemoved = eventCommentsDao.removeComment(commentId = params.commentId, eventId = params.eventId)

                if (commentWasRemoved) {
                    eventDao.updateCommentsCount(eventId = params.eventId, decrement = true)
                    Response.Success(
                        data = CommentResponse(success = true)
                    )
                } else {
                    Response.Error(
                        code = HttpStatusCode.Conflict,
                        data = CommentResponse(
                            success = false,
                            message = "Comment ${params.commentId} could not be removed."
                        )
                    )
                }
            }
        }
    }

    override suspend fun getEventComments(eventId: Long, pageNumber: Int, pageSize: Int): Response<GetCommentsResponse> {
        val commentRows = eventCommentsDao.getComments(eventId = eventId, pageNumber = pageNumber, pageSize = pageSize)
        val comments = commentRows.map {
            toEventComment(it)
        }

        return Response.Success(
            data = GetCommentsResponse(success = true, comments = comments)
        )
    }

    private fun toEventComment(commentRow: EventCommentRow): EventComment {
        return EventComment(
            commentId = commentRow.commentId,
            content = commentRow.content,
            eventId = commentRow.eventId,
            userId = commentRow.userId,
            name = commentRow.name,
            organizationName = commentRow.organizationName,
            profileImageUrl = commentRow.profileImageUrl,
            createdAt = commentRow.createdAt
        )
    }

}