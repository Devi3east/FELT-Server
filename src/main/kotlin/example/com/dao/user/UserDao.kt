package example.com.dao.user

import example.com.model.SignUpParams

interface UserDao {
    suspend fun insert(params: SignUpParams): UserRow?
    suspend fun findByEmail(email: String): UserRow?
    suspend fun findById(userId: Long): UserRow?
    suspend fun updateUser(userId: Long, name: String, description: String, address: String, profileImageUrl: String, organizationName: String): Boolean
    suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean
    suspend fun getUsers(userIds: List<Long>): List<UserRow>
    suspend fun getOrganizations(userIds: List<Long>): List<UserRow>
    suspend fun getPopularUsers(limit: Int): List<UserRow>
}