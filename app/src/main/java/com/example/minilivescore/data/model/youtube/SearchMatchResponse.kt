package com.example.minilivescore.data.model.youtube


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SearchMatchResponse(
    @Json(name = "etag")
    val etag: String,
    @Json(name = "items")
    val items: List<Item>,
    @Json(name = "kind")
    val kind: String,
    @Json(name = "nextPageToken")
    val nextPageToken: String,
    @Json(name = "pageInfo")
    val pageInfo: PageInfo
) {
    @Keep
    data class Item(
        @Json(name = "etag")
        val etag: String,
        @Json(name = "id")
        val id: String,
        @Json(name = "kind")
        val kind: String,
        @Json(name = "snippet")
        val snippet: Snippet
    ) {
        @Keep
        data class Snippet(
            @Json(name = "channelId")
            val channelId: String,
            @Json(name = "channelTitle")
            val channelTitle: String,
            @Json(name = "description")
            val description: String,
            @Json(name = "playlistId")
            val playlistId: String,
            @Json(name = "position")
            val position: Int,
            @Json(name = "publishedAt")
            val publishedAt: String,
            @Json(name = "resourceId")
            val resourceId: ResourceId,
            @Json(name = "thumbnails")
            val thumbnails: Thumbnails,
            @Json(name = "title")
            val title: String,
            @Json(name = "videoOwnerChannelId")
            val videoOwnerChannelId: String,
            @Json(name = "videoOwnerChannelTitle")
            val videoOwnerChannelTitle: String
        ) {
            @Keep
            data class ResourceId(
                @Json(name = "kind")
                val kind: String,
                @Json(name = "videoId")
                val videoId: String
            )

            @Keep
            data class Thumbnails(
                @Json(name = "default")
                val default: Default,
                @Json(name = "high")
                val high: High,
                @Json(name = "maxres")
                val maxres: Maxres,
                @Json(name = "medium")
                val medium: Medium,
                @Json(name = "standard")
                val standard: Standard
            ) {
                @Keep
                data class Default(
                    @Json(name = "height")
                    val height: Int,
                    @Json(name = "url")
                    val url: String,
                    @Json(name = "width")
                    val width: Int
                )

                @Keep
                data class High(
                    @Json(name = "height")
                    val height: Int,
                    @Json(name = "url")
                    val url: String,
                    @Json(name = "width")
                    val width: Int
                )

                @Keep
                data class Maxres(
                    @Json(name = "height")
                    val height: Int,
                    @Json(name = "url")
                    val url: String,
                    @Json(name = "width")
                    val width: Int
                )

                @Keep
                data class Medium(
                    @Json(name = "height")
                    val height: Int,
                    @Json(name = "url")
                    val url: String,
                    @Json(name = "width")
                    val width: Int
                )

                @Keep
                data class Standard(
                    @Json(name = "height")
                    val height: Int,
                    @Json(name = "url")
                    val url: String,
                    @Json(name = "width")
                    val width: Int
                )
            }
        }
    }

    @Keep
    data class PageInfo(
        @Json(name = "resultsPerPage")
        val resultsPerPage: Int,
        @Json(name = "totalResults")
        val totalResults: Int
    )
}