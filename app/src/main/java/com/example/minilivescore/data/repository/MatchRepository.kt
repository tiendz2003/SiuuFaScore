package com.example.minilivescore.data.repository

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.minilivescore.data.model.LeagueMatches
import com.example.minilivescore.data.model.LeaguesStanding
import com.example.minilivescore.utils.Resource
import com.example.minilivescore.data.model.Match
import com.example.minilivescore.data.model.MatchLive
import com.example.minilivescore.data.networking.LiveScoreService
import com.example.minilivescore.ui.matches.MatchesViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MatchRepository(
    private val apiService: LiveScoreService,

    //Nên khai báo luồng chỉ định ở đây không hardcode chúng với withContext
    private val ioDispatcher:CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getLeagueMatches(leagueCode: String, round: Int):Resource<LeagueMatches>{
        return withContext(ioDispatcher){
            try {
                Resource.Success(apiService.getLeagueMatches(leagueCode,round))
            }catch (e:Exception){
                Resource.Error(e.message?:"Đã xảy ra lỗi")
            }
        }
    }
    suspend fun getStandingLeagues(id:String):Response<LeaguesStanding>{
        return withContext(ioDispatcher){
            apiService.getStandingLeagues(id)
        }
    }
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Thêm KotlinJsonAdapterFactory
        .build()
    suspend fun getPlayBackUrl(id:Int):Resource<MatchLive>{
        return withContext(ioDispatcher){
          try {
              val backendApi = Retrofit.Builder()
                  .baseUrl("http://192.168.0.107:8080/")
                  .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                  .build()
                  .create(LiveScoreService::class.java)
              val response = backendApi.getPlayBackUrl(id)
              Log.d("API Response", response.toString())
              Resource.Success(response)
          }catch (e:Exception){
              Resource.Error(e.message?:"Đã xảy ra lỗi")
          }
        }
    }


class MatchesViewModelFactory(
    private val matchRepository: MatchRepository,
    owner:SavedStateRegistryOwner,
    defaultArgs:Bundle? = null
) : AbstractSavedStateViewModelFactory(owner,defaultArgs) {
    /* override fun <T : ViewModel> create(modelClass: Class<T>): T {
         if (modelClass.isAssignableFrom(MatchesViewModel::class.java)) {
             @Suppress("UNCHECKED_CAST")
             return MatchesViewModel(matchRepository) as T
         }
         throw IllegalArgumentException("Unknown ViewModel class")
     }
 */
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(MatchesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchesViewModel(matchRepository, handle) as T
        }
        throw IllegalArgumentException("UnknownViewModelClass")
    }
}
}