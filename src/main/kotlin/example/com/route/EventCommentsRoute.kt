package example.com.route

import example.com.model.CommentResponse
import example.com.model.GetCommentsResponse
import example.com.model.NewCommentParams
import example.com.model.RemoveCommentParams
import example.com.repository.event_comments.EventCommentsRepository
import example.com.util.Constants
import example.com.util.getLongParameter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.eventCommentsRouting() {
    val repository by inject<EventCommentsRepository>()

    authenticate {

        route(path = "/event/comments") {

            post(path = "/create") {
                try {
                    val params = call.receiveNullable<NewCommentParams>()

                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = CommentResponse(
                                success = false,
                                message = "Could not parse event comment parameters."
                            )
                        )
                        return@post
                    }

                    val result = repository.addComment(params = params)
                    call.respond(status = result.code, message = result.data)
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = CommentResponse(
                            success = false,
                            message = "An unexpected error has occurred in EventComments -> Post -> create Route. Please try again."
                        )
                    )
                }
            }

            delete(path = "/delete") {
                try {
                    val params = call.receiveNullable<RemoveCommentParams>()

                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = CommentResponse(
                                success = false,
                                message = "Could not parse delete comment parameters."
                            )
                        )
                        return@delete
                    }

                    val result = repository.removeComment(params = params)
                    call.respond(status = result.code, message = result.data)
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = CommentResponse(
                            success = false,
                            message = "An unexpected error has occurred in EventComments -> Delete -> delete Route. Please try again."
                        )
                    )
                }
            }

            get(path = "/{eventId}") {
                try {
                    val eventId = call.getLongParameter(name = "eventId")
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getEventComments(eventId = eventId, pageNumber = page, pageSize = limit)
                    call.respond(status = result.code, message = result.data)
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = GetCommentsResponse(
                            success = false,
                            message = "An unexpected error has occurred in EventComments -> Get -> eventId Route. Please try again."
                        )
                    )
                    return@get
                }
            }
        }
    }
}