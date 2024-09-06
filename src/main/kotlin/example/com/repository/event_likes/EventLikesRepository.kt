package example.com.repository.event_likes

import example.com.model.LikeParams
import example.com.model.LikeResponse
import example.com.util.Response

interface EventLikesRepository {
    suspend fun addLike(params: LikeParams): Response<LikeResponse>
    suspend fun removeLike(params: LikeParams): Response<LikeResponse>
}