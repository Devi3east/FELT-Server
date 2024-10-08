package example.com.model

import kotlinx.serialization.Serializable

@Serializable
data class FollowAndUnfollowResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class FollowsParams(
    val follower: Long,
    val following: Long
)

@Serializable
data class FollowUserData(
    val userId: Long,
    val name: String,
    val organizationName: String? = null,
    val description: String,
    val profileImageUrl: String? = null,
    val isFollowing: Boolean
)

@Serializable
data class GetFollowsResponse(
    val success: Boolean,
    val follows: List<FollowUserData> = listOf(),
    val message: String? = null
)