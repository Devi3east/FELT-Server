package example.com.route

import example.com.model.ProfileResponse
import example.com.model.UpdateUserParams
import example.com.repository.profile.ProfileRepository
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

fun Routing.profileRouting() {
    val profileRepository by inject<ProfileRepository>()

    authenticate {

        route(path = "/profile") {

            get(path = "/{userId}") {
                try {
                    val profileOwnerId = call.getLongParameter(name = "userId")
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)

                    val result = profileRepository.getUserById(userId = profileOwnerId, currentUserId = currentUserId)
                    call.respond(status = result.code, message = result.data)
                } catch (badRequestError: BadRequestException) {
                    return@get
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = ProfileResponse(
                            success = false,
                            message = "An unexpected error has occurred in Profile -> Get -> userId Route. Please try again."
                        )
                    )
                }
            }

            post(path = "/update") {
                var fileName = ""
                var updateUserParams: UpdateUserParams? = null
                val multiPartData = call.receiveMultipart()

                try {
                    multiPartData.forEachPart { partData ->
                        when (partData) {
                            is PartData.FileItem -> {
                                fileName = partData.saveFile(folderPath = Constants.PROFILE_IMAGES_FOLDER_PATH)
                            }
                            is PartData.FormItem -> {
                                if (partData.name == "profile_data") {
                                    updateUserParams = Json.decodeFromString(partData.value)
                                }
                            }
                            else -> {}
                        }
                        partData.dispose()
                    }

                    val imageUrl = "${Constants.BASE_URL}${Constants.PROFILE_IMAGES_FOLDER}$fileName"

                    val result = profileRepository.updateUser(
                        updateUserParams = updateUserParams!!.copy(
                            profileImageUrl = if (fileName.isNotEmpty()) imageUrl else updateUserParams!!.profileImageUrl
                        )
                    )
                    call.respond(status = result.code, message = result.data)
                } catch (anyError: Throwable) {
                    if (fileName.isNotEmpty()) {
                        File("${Constants.PROFILE_IMAGES_FOLDER_PATH}/$fileName").delete()
                    }
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = ProfileResponse(
                            success = false,
                            message = "An unexpected error has occurred in Profile -> Post -> update Route. Please try again."
                        )
                    )
                }
            }

        }

    }

}