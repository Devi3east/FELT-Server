package example.com.route

import example.com.model.EventResponse
import example.com.model.EventTextParams
import example.com.model.EventsResponse
import example.com.repository.event.EventRepository
import example.com.util.Constants
import example.com.util.getLongParameter
import example.com.util.saveFile
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.io.File

fun Routing.eventRouting() {
    val eventRepository by inject<EventRepository>()

    authenticate {

        route(path = "/event") {

            post(path = "/create") {
                var fileName = ""
                var eventTextParams: EventTextParams? = null
                val multiPartData = call.receiveMultipart()

                multiPartData.forEachPart { partData ->
                    when (partData) {
                        is PartData.FileItem -> {
                            fileName = partData.saveFile(folderPath = Constants.EVENT_IMAGES_FOLDER_PATH)
                        }
                        is PartData.FormItem -> {
                            if (partData.name == "post_data") {
                                eventTextParams = Json.decodeFromString(partData.value)
                            }
                        }
                        else -> {}
                    }
                    partData.dispose()
                }

                val eventImagePrimary = "${Constants.BASE_URL}${Constants.EVENT_IMAGES_FOLDER}$fileName"

                if (eventTextParams == null) {
                    File("${Constants.EVENT_IMAGES_FOLDER_PATH}/$fileName").delete()

                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = EventResponse(
                            success = false,
                            message = "Could not parse event data."
                        )
                    )
                } else {
                    val result = eventRepository.createEvent(eventImagePrimary, eventTextParams!!)
                    call.respond(result.code, message = result.data)
                }
            }

            get(path = "/{eventId}") {
                try {
                    val eventId = call.getLongParameter(name = "eventId")
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)

                    val result = eventRepository.getEvent(eventId = eventId, currentUserId = currentUserId)
                    call.respond(status = result.code, message = result.data)
                } catch (badRequestError: BadRequestException) {
                    return@get
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = EventResponse(
                            success = false,
                            message = "An unexpected error has occurred in Event -> Get -> eventId Route. Please try again."
                        )
                    )
                }
            }

            delete(path = "/{eventId}") {
                try {
                    val eventId = call.getLongParameter(name = "eventId")
                    val result = eventRepository.deleteEvent(eventId = eventId)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    return@delete
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = EventResponse(
                            success = false,
                            message = "An unexpected error has occurred in Event -> Delete -> eventId Route. Please try again."
                        )
                    )
                }
            }

        }

        route(path = "/events") {

            get(path = "/feed") {
                try {
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = eventRepository.getFeedEvents(
                        userId = currentUserId,
                        pageSize = limit,
                        pageNumber = page
                    )
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    return@get
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = EventsResponse(
                            success = false,
                            message = "An unexpected error has occurred in Events -> Get -> Feed Route. Please try again."
                        )
                    )
                }
            }

            get(path = "/{userId}") {
                try {
                    val eventsOwnerId = call.getLongParameter(name = "userId")
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = eventRepository.getEventByUser(
                        eventsOwnerId = eventsOwnerId,
                        currentUserId = currentUserId,
                        pageNumber = page,
                        pageSize = limit
                    )
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    return@get
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = EventsResponse(
                            success = false,
                            message = "An unexpected error has occurred in Events -> Get -> userId Route. Please try again."
                        )
                    )
                }
            }

        }

    }
}