package com.example.minilivescore.data.model

import com.squareup.moshi.Json

data class SearchListResponse(
    val items: List<SearchResult>
)

data class SearchResult(
    val id: VideoId,
    val snippet: Snippet
)

data class VideoId(
    val videoId: String
)

data class Snippet(
    val title: String,
    val description: String,
    val publishedAt: String
)