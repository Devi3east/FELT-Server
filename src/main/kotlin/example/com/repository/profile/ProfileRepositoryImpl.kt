package example.com.repository.profile

import example.com.dao.follows.FollowsDao
import example.com.dao.user.UserDao
import example.com.dao.user.UserRow
import example.com.model.Profile
import example.com.model.ProfileResponse
import example.com.model.UpdateUserParams
import example.com.util.Response
import io.ktor.http.*

class ProfileRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao
): ProfileRepository {

    override suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse> {
        val userRow = userDao.findById(userId = userId)

        return if (userRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "Could not find user with id: $userId."
                )
            )
        } else {
            val isFollowing = followsDao.isAlreadyFollowing(
                follower = currentUserId,
                following = userId
            )
            val isOwnProfile = userId == currentUserId

            Response.Success(
                data = ProfileResponse(
                    success = true,
                    profile = toProfile(userRow, isFollowing, isOwnProfile)
                )
            )
        }
    }

    override suspend fun updateUser(updateUserParams: UpdateUserParams): Response<ProfileResponse> {
        val userExists = userDao.findById(userId = updateUserParams.userId) != null

        if (userExists) {
            val userUpdated = userDao.updateUser(
                userId = updateUserParams.userId,
                name = updateUserParams.name,
                description = updateUserParams.description,
                address = updateUserParams.address ?: "",
                profileImageUrl = updateUserParams.profileImageUrl ?: "",
                organizationName = updateUserParams.organizationName ?: ""
            )

            return if (userUpdated) {
                Response.Success(
                    data = ProfileResponse(success = true)
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = ProfileResponse(
                        success = false,
                        message = "Could not update user: ${updateUserParams.userId}."
                    )
                )
            }
        } else {
            return Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "Could not find user: ${updateUserParams.userId}."
                )
            )
        }
    }

    private fun toProfile(userRow: UserRow, isFollowing: Boolean, isOwnProfile: Boolean): Profile {
        return Profile(
            userId = userRow.userId,
            name = userRow.name,
            description = userRow.description,
            address = userRow.address,
            profileImageUrl = userRow.profileImageUrl,
            organizationName = userRow.organizationName,
            followersCount = userRow.followersCount,
            followingCount = userRow.followingCount,
            isFollowing = isFollowing,
            isOwnProfile = isOwnProfile
        )
    }

}