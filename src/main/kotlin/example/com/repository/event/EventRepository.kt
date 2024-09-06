package example.com.repository.event

import example.com.model.EventResponse
import example.com.model.EventTextParams
import example.com.model.EventsResponse
import example.com.util.Response

interface EventRepository {
    suspend fun createEvent(eventImagePrimary: String, eventTextParams: EventTextParams): Response<EventResponse>
    suspend fun getFeedEvents(userId: Long, pageNumber: Int, pageSize: Int): Response<EventsResponse>
    suspend fun getEventByUser(eventsOwnerId: Long, currentUserId: Long, pageNumber: Int, pageSize: Int): Response<EventsResponse>
    suspend fun getEvent(eventId: Long, currentUserId: Long): Response<EventResponse>
    suspend fun deleteEvent(eventId: Long): Response<EventResponse>
}