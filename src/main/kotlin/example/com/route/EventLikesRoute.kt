package example.com.route

import example.com.model.LikeParams
import example.com.model.LikeResponse
import example.com.repository.event_likes.EventLikesRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.eventLikesRouting() {
    val repository by inject<EventLikesRepository>()

    authenticate {
        route(path = "event/likes") {

            post(path = "/add") {
                try {
                    val params = call.receiveNullable<LikeParams>()

                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = LikeResponse(
                                success = false,
                                message = "Could not parse like parameters."
                            )
                        )
                        return@post
                    }

                    val result = repository.addLike(params = params)
                    call.respond(status = result.code, message = result.data)
                } catch (error: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = LikeResponse(
                            success = false,
                            message = "An unexpected error has occurred in EventLikes -> Post -> add Route. Please try again."
                        )
                    )
                }
            }

            delete(path = "/remove") {
                try {
                    val params = call.receiveNullable<LikeParams>()

                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = LikeResponse(
                                success = false,
                                message = "Could not parse remove like parameters."
                            )
                        )
                        return@delete
                    }

                    val result = repository.removeLike(params = params)
                    call.respond(status = result.code, message = result.data)
                } catch (error: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = LikeResponse(
                            success = false,
                            message = "An unexpected error has occurred in EventLikes -> Delete -> remove Route. Please try again."
                        )
                    )
                }
            }
        }
    }

}