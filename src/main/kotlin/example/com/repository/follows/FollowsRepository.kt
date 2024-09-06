package example.com.repository.follows

import example.com.model.FollowAndUnfollowResponse
import example.com.model.GetFollowsResponse
import example.com.util.Response

interface FollowsRepository {
    suspend fun followUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>
    suspend fun unfollowUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>
    suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse>
    suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse>
    suspend fun getFollowingSuggestions(userId: Long): Response<GetFollowsResponse>
}