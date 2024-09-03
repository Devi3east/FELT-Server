package example.com.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpParams(
    val name: String,
    val email: String,
    val password: String,
    val isOrganization: Boolean,
    val organizationName: String?,
    val isAgreementChecked: Boolean
)

@Serializable
data class SignInParams(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val data: AuthResponseData? = null,
    val error: String? = null
)

@Serializable
data class AuthResponseData(
    val userId: Long,
    val name: String,
    val email: String,
    val userImage: String? = null,
    val isOrganization: Boolean = false,
    val organizationName: String? = null,
    val isAgreementChecked: Boolean? = false,
    val followersCount: Int? = 0,
    val followingCount: Int? = 0,
    val isPremium: Boolean = false,
    val token: String,
)