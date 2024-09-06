package example.com.dao.event_comments

interface EventCommentsDao {
    suspend fun addComment(eventId: Long, userId: Long, content: String): EventCommentRow?
    suspend fun removeComment(commentId: Long, eventId: Long): Boolean
    suspend fun findComment(commentId: Long, eventId: Long): EventCommentRow?
    suspend fun getComments(eventId: Long, pageNumber: Int, pageSize: Int): List<EventCommentRow>
}