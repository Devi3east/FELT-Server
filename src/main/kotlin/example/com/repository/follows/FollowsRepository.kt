package example.com.repository.follows

import example.com.model.FollowAndUnfollowResponse
import example.com.util.Response

interface FollowsRepository {
    suspend fun followUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>
    suspend fun unfollowUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>
}