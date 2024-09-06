package example.com.repository.event_comments

import example.com.model.CommentResponse
import example.com.model.GetCommentsResponse
import example.com.model.NewCommentParams
import example.com.model.RemoveCommentParams
import example.com.util.Response

interface EventCommentsRepository {
    suspend fun addComment(params: NewCommentParams): Response<CommentResponse>
    suspend fun removeComment(params: RemoveCommentParams): Response<CommentResponse>
    suspend fun getEventComments(eventId: Long, pageNumber: Int, pageSize: Int): Response<GetCommentsResponse>
}