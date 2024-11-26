package com.example.minilivescore.data.repository

import android.util.Log
import com.example.minilivescore.data.networking.YouTubeApiService
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class HighlightRepository(private val youTubeApiService: YouTubeApiService){

    //Learn and train.this is nice code
    suspend fun findVideoInList(videoTitle: String): Resource<String?> = withContext(Dispatchers.IO) {
        try {
            var nextPageToken: String? = null
            val playListId = "PLiaWrX4zmrTmVTbQGnQv2FQQ3h09Kk5_K"

            do {
                // Gửi yêu cầu lấy video từ danh sách phát
                val response = youTubeApiService.getPlaylistItems(
                    playlistId = playListId,
                    pageToken = nextPageToken
                )
                // Kiểm tra phản hồi
                if (response.isSuccessful) {
                    Log.d("videoTitle", "videoTitle: $videoTitle")
                    // Tìm video phù hợp trong danh sách video
                    val matchingVideo = response.body()!!.items.firstOrNull {video->
                        video.snippet.title.contains(videoTitle, ignoreCase = true)
                    }

                    // Nếu tìm thấy video, trả về videoId
                    if (matchingVideo != null) {
                        return@withContext Resource.Success(matchingVideo.snippet.resourceId.videoId)
                    }

                    // Cập nhật nextPageToken để lấy trang tiếp theo
                    nextPageToken = response.body()!!.nextPageToken
                } else {
                    // Xử lý trường hợp không thành công
                    return@withContext Resource.Error("Không tìm thấy video hoặc xảy ra lỗi.")
                }

            } while (nextPageToken != null)

            // Nếu không tìm thấy video sau khi kiểm tra tất cả các trang
            Resource.Error("Không tìm thấy video nào với tiêu đề: $videoTitle")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi không xác định")
        }
    }

}

