package com.example.ai_ad_feed_flow.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FeedApi {
    @GET("feed")
    suspend fun getFeed(
        @Query("channel") channel: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): FeedResponseDto

    @POST("search")
    suspend fun search(
        @Body request: SearchRequestDto
    ): SearchResponseDto
}
