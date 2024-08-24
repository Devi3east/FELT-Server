package example.com.dao.user

import example.com.model.SignUpParams
import example.com.model.User

interface UserDao {
    suspend fun insert(params: SignUpParams): User?
    suspend fun findByEmail(email: String): User?
}