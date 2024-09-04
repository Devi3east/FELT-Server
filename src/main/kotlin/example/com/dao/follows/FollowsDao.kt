package example.com.dao.follows

interface FollowsDao {
    suspend fun followsUser(follower: Long, following: Long): Boolean
    suspend fun unfollowUser(follower: Long, following: Long): Boolean
    suspend fun getFollowerIds(userId: Long, pageNumber: Int, pageSize: Int): List<Long>
    suspend fun getFollowingIds(userId: Long, pageNumber: Int, pageSize: Int): List<Long>
    suspend fun getAllFollowing(userId: Long): List<Long>
    suspend fun isAlreadyFollowing(follower: Long, following: Long): Boolean
}