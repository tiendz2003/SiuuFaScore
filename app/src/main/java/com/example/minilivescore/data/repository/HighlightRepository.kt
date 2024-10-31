package com.example.minilivescore.data.repository

import com.example.minilivescore.data.networking.YouTubeApiService
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class HighlightRepository(private val youTubeApiService: YouTubeApiService){
    suspend fun findHighlightVideo(homeTeam:String,awayTeam:String,matchEndTime: Date):Resource<String?> =
        withContext(Dispatchers.IO){
            try {
                val query = "$homeTeam-$awayTeam Kplus Sport 24/25 "
                val channelId = "UC9xeuekJd88ku9LDcmGdUOA"
                val publishAfter = matchEndTime.toInstant().plusSeconds(3*60*60).toString()

                val response = youTubeApiService.searchVideos(
                    channelId = channelId,
                    query = query,
                    publishedAfter = publishAfter
                )
                if(response.isSuccessful){
                    val videoId =response.body()?.items?.firstOrNull()?.id?.videoId
                    Resource.Success(videoId)
                }else{
                    Resource.Error("${response.code()}")
                }

            }catch (e: CancellationException){
                throw e
            }
            catch (e:Exception){
                Resource.Error(e.message ?:"Lỗi không xác định")
            }
        }

}

