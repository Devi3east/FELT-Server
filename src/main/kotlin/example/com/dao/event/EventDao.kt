package example.com.dao.event

interface EventDao {
    suspend fun createEvent(eventTitle: String, description: String, isScheduled: Boolean, dateTime: String?, eventImagePrimary: String, userId: Long, organizationName: String): Boolean
    suspend fun getFeedsEvent(userId: Long, follows: List<Long>, pageNumber: Int, pageSize: Int): List<EventRow>
    suspend fun getEventByUser(userId: Long, pageNumber: Int, pageSize: Int): List<EventRow>
    suspend fun getEvent(eventId: Long): EventRow?
    suspend fun updateLikesCount(eventId: Long, decrement: Boolean = false): Boolean
    suspend fun updateCommentsCount(eventId: Long, decrement: Boolean = false): Boolean
    suspend fun deleteEvent(eventId: Long): Boolean
    suspend fun getPopularEvents(limit: Int): List<EventRow>
}