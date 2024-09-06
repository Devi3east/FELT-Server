package example.com.route

import example.com.dao.user.UserDao
import example.com.model.FollowAndUnfollowResponse
import example.com.model.FollowsParams
import example.com.repository.follows.FollowsRepository
import example.com.util.Constants
import example.com.util.getLongParameter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.followsRouting() {
    val repository by inject<FollowsRepository>()
    val userDao by inject<UserDao>()

    authenticate {

        route(path = "/follows") {

            post("/follow") {
                val params = call.receiveNullable<FollowsParams>()

                if (params == null) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = FollowAndUnfollowResponse(
                            success = false,
                            message = "Oops, something went wrong. Try again."
                        )
                    )
                    return@post
                }

                // Extract the authenticated user's email from the token
                val principal = call.principal<JWTPrincipal>()
                val tokenEmail = principal?.payload?.getClaim(Constants.JWT_CLAIM_EMAIL)?.asString()

                // Retrieve the UserRow of the follower using userDao
                val followerUserRow = userDao.findById(params.follower)

                // Check if followerUserRow is not null and then get the email
                val followerEmail = followerUserRow?.email

                // Check if the authenticated user's email matches the follower's email
                if (tokenEmail != followerEmail) {
                    call.respond(
                        status = HttpStatusCode.Forbidden,
                        message = "You are not authorized to perform this action."
                    )
                    return@post
                }

                val result = repository.followUser(follower = params.follower, following = params.following)

                call.respond(
                    status = result.code,
                    message = result.data
                )
            }

            post("/unfollow") {
                val params = call.receiveNullable<FollowsParams>()

                if (params == null) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = FollowAndUnfollowResponse(
                            success = false,
                            message = "Oops, something went wrong. Try again."
                        )
                    )
                    return@post
                }

                // Extract the authenticated user's email from the token
                val principal = call.principal<JWTPrincipal>()
                val tokenEmail = principal?.payload?.getClaim(Constants.JWT_CLAIM_EMAIL)?.asString()

                // Retrieve the UserRow of the follower using userDao
                val followerUserRow = userDao.findById(params.follower)

                // Check if followerUserRow is not null and then get the email
                val followerEmail = followerUserRow?.email

                // Check if the authenticated user's email matches the follower's email
                if (tokenEmail != followerEmail) {
                    call.respond(
                        status = HttpStatusCode.Forbidden,
                        message = "You are not authorized to perform this action."
                    )
                    return@post
                }

                val result = repository.unfollowUser(follower = params.follower, following = params.following)

                call.respond(
                    status = result.code,
                    message = result.data
                )
            }

            get(path = "/followers") {
                try {
                    val userId = call.getLongParameter(name = "userId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getFollowers(userId = userId, pageNumber = page, pageSize = limit)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Missing required parameters."
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "An unexpected error has occurred in Follows -> Get -> followers Route. Please try again."
                    )
                }
            }

            get(path = "/following") {
                try {
                    val userId = call.getLongParameter(name = "userId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getFollowing(userId = userId, pageNumber = page, pageSize = limit)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Missing required parameters."
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "An unexpected error has occurred in Follows -> Get -> following Route. Please try again."
                    )
                }
            }

            get(path = "/suggestions") {
                try {
                    val userId = call.getLongParameter(name = "userId", isQueryParameter = true)

                    val result = repository.getFollowingSuggestions(userId = userId)
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Missing required parameters."
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "An unexpected error has occurred in Follows -> Get -> suggestions Route. Please try again."
                    )
                }
            }

        }

    }

}