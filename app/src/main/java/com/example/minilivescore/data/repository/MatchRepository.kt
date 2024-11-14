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
import com.example.minilivescore.data.networking.LiveScoreService
import com.example.minilivescore.ui.matches.MatchesViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
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
    suspend fun getMatches(): Resource<Match> {
        return withContext(ioDispatcher){
            try {
                val dataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar =Calendar.getInstance()
                //Lấy ngày hôm nay
                val today = calendar.time
                val dateFrom = dataFormat.format(today)
                //Lấy ngày hôm
                calendar.add(Calendar.DAY_OF_YEAR,1)
                val tomorrow = calendar.time
                val dateTo = dataFormat.format(tomorrow)
                Log.d("dateFrom",dateFrom)
                Log.d("dateTo",dateTo)
                Resource.Success(apiService.getMatches(dateFrom = "2024-10-05","2024-10-06"))

            }catch (e:Exception){
                Resource.Error(e.message?:"Đã xảy ra lỗi")
            }
        }
    }
}

class MatchesViewModelFactory(
    private val matchRepository: MatchRepository,
    owner:SavedStateRegistryOwner,
    defaultArgs:Bundle? = null
) : AbstractSavedStateViewModelFactory(owner,defaultArgs){
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
        if(modelClass.isAssignableFrom(MatchesViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MatchesViewModel(matchRepository,handle) as T
        }
        throw IllegalArgumentException("UnknownViewModelClass")
    }
}