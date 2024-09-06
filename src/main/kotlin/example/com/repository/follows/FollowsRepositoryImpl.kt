package example.com.repository.follows

import example.com.dao.follows.FollowsDao
import example.com.dao.user.UserDao
import example.com.dao.user.UserRow
import example.com.model.FollowAndUnfollowResponse
import example.com.model.FollowUserData
import example.com.model.GetFollowsResponse
import example.com.util.Constants
import example.com.util.Response
import io.ktor.http.*

class FollowsRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao
): FollowsRepository {

    override suspend fun followUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse> {
        return if (followsDao.isAlreadyFollowing(follower, following)) {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = FollowAndUnfollowResponse(
                    success = false,
                    message = "You are already following this user."
                )
            )
        } else {
            val success = followsDao.followsUser(follower, following)

            if (success) {
                userDao.updateFollowsCount(follower, following, isFollowing = true)
                Response.Success(
                    data = FollowAndUnfollowResponse(success = true)
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = FollowAndUnfollowResponse(
                        success = false,
                        message = "Oops, something went wrong, please try again."
                    )
                )
            }
        }
    }

    override suspend fun unfollowUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse> {
        val success = followsDao.unfollowUser(follower, following)

        return if (success) {
            userDao.updateFollowsCount(follower, following, isFollowing = false)
            Response.Success(
                data = FollowAndUnfollowResponse(success = true)
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = FollowAndUnfollowResponse(
                    success = false,
                    message = "Oops, something went wrong, please try again."
                )
            )
        }
    }

    override suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse> {
        val followersIds = followsDao.getFollowerIds(userId, pageNumber, pageSize)
        val followersRows = userDao.getUsers(userIds = followersIds)
        val followers = followersRows.map { followerRow ->
            val isFollowing = followsDao.isAlreadyFollowing(follower = userId, following = followerRow.userId)
            toFollowUserData(userRow = followerRow, isFollowing = isFollowing)
        }
        return Response.Success(
            data = GetFollowsResponse(success = true, follows = followers)
        )
    }

    override suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse> {
        val followingIds = followsDao.getFollowingIds(userId, pageNumber, pageSize)
        val followingRows = userDao.getUsers(userIds = followingIds)
        val following = followingRows.map { followingRow ->
            toFollowUserData(userRow = followingRow, isFollowing = true)
        }
        return Response.Success(
            data = GetFollowsResponse(success = true, follows = following)
        )
    }

    override suspend fun getFollowingSuggestions(userId: Long): Response<GetFollowsResponse> {
        val hasFollowing = followsDao.getFollowingIds(userId = userId, pageNumber = 0, pageSize = 1).isNotEmpty()
        val suggestedFollowingRows = userDao.getPopularUsers(limit = Constants.SUGGESTED_FOLLOWING_LIMIT)

        val suggestedFollowing = suggestedFollowingRows
            .filterNot { it.userId == userId }
            .map {
            val followingStatus = followsDao.isAlreadyFollowing(userId, it.userId)

            toFollowUserData(userRow = it, isFollowing = followingStatus)
        }
        return Response.Success(
            data = GetFollowsResponse(success = true, follows = suggestedFollowing)
        )
    }

    private fun toFollowUserData(userRow: UserRow, isFollowing: Boolean): FollowUserData {
        return FollowUserData(
            userId = userRow.userId,
            name = userRow.name,
            organizationName = userRow.organizationName,
            description = userRow.description,
            profileImageUrl = userRow.profileImageUrl,
            isFollowing = isFollowing,
        )
    }

}