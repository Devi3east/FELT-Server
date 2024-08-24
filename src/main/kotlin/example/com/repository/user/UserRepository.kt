package example.com.repository.user

import example.com.model.AuthResponse
import example.com.model.SignInParams
import example.com.model.SignUpParams
import example.com.util.Response

interface UserRepository {
    suspend fun signUp(params: SignUpParams): Response<AuthResponse>
    suspend fun signIn(params: SignInParams): Response<AuthResponse>
}