package com.example.minilivescore.data.networking

import com.example.minilivescore.data.model.SearchListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("q") query: String,
        @Query("channelId") channelId: String?= null,
        @Query("order") order: String = "date",
        @Query("publishedAfter") publishedAfter: String,
        @Query("maxResults") maxResults: Int = 1
    ): Response<SearchListResponse>
}