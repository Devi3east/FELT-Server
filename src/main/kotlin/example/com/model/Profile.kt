package example.com.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserParams(
    val userId: Long,
    val name: String,
    val description: String,
    val address: String? = null,
    val profileImageUrl: String? = null,
    val organizationName: String? = null
)

@Serializable
data class Profile(
    val userId: Long,
    val name: String,
    val description: String,
    val address: String? = null,
    val profileImageUrl: String? = null,
    val organizationName: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean,
    val isOwnProfile: Boolean
)

@Serializable
data class ProfileResponse(
    val success: Boolean,
    val profile: Profile? = null,
    val message: String? = null
)