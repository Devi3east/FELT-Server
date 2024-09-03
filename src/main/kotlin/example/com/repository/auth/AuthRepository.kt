package example.com.repository.auth

import example.com.model.AuthResponse
import example.com.model.SignInParams
import example.com.model.SignUpParams
import example.com.util.Response

interface AuthRepository {
    suspend fun signUp(params: SignUpParams): Response<AuthResponse>
    suspend fun signIn(params: SignInParams): Response<AuthResponse>
}