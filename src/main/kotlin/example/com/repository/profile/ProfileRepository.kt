package example.com.repository.profile

import example.com.model.ProfileResponse
import example.com.model.UpdateUserParams
import example.com.util.Response

interface ProfileRepository {
    suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse>
    suspend fun updateUser(updateUserParams: UpdateUserParams): Response<ProfileResponse>
}