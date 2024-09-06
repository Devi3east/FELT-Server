package example.com.dao.event_likes

interface EventLikesDao {
    suspend fun addLike(eventId: Long, userId: Long): Boolean
    suspend fun removeLike(eventId: Long, userId: Long): Boolean
    suspend fun isEventLikedByUser(eventId: Long, userId: Long): Boolean
}