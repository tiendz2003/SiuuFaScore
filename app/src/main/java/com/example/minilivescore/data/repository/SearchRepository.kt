package com.example.minilivescore.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.minilivescore.data.database.LiveScoreDao
import com.example.minilivescore.data.model.SearchTeamsResponse
import com.example.minilivescore.data.model.TeamEntity
import com.example.minilivescore.data.networking.LiveScoreService
import com.example.minilivescore.ui.searchteam.SearchTeamViewModel
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SearchRepository(
    private val liveScoreApiService: LiveScoreService,
    private val livescoreDao:LiveScoreDao
){

    private val leagues = listOf("PL","SA","PD")
    //xóa cache sau 24h
    private val CACHE_DURATION_CLEAN =TimeUnit.HOURS.toMillis(24)
    //Tìm kiếm theo tên đội bóng
    suspend fun searchTeam(query:String):Resource<List<TeamEntity>>{
        return withContext(Dispatchers.IO){
           try {
               updateCache()

               val teams= livescoreDao.searchTeam(query)
               Resource.Success(teams)
           }catch (e:Exception){
               Resource.Error(e.message?:"Đã xảy ra lỗi")
           }
        }
    }
//Kiểm tra xem trong database có data và reset lại database nếu quá thời gian
    private suspend fun updateCache(){
        val allTeams = livescoreDao.getAllTeams()
        val lastUpdate = livescoreDao.getLastUpdateTime()?:0
        val currentTime = System.currentTimeMillis()
        if(allTeams.isEmpty()|| currentTime - lastUpdate >CACHE_DURATION_CLEAN){
            refreshCache()
        }
    }
    private suspend fun refreshCache() = coroutineScope{
        try {
           Resource.Loading<List<TeamEntity>>()
        //tải tất cả câu lạc bộ
            val allTeams = leagues.map { leagueId->
                async {
                    try {
                        val response =liveScoreApiService.searchTeams(leagueId)
                        if(response.isSuccessful){
                            response.body()?.teams?.map { team ->
                                TeamEntity(
                                    id = team.id,
                                    name = team.name,
                                    tla = team.tla,
                                    crest = team.crest,
                                    leagueId = leagueId,
                                    shortName = team.shortName,
                                    lastUpdate = System.currentTimeMillis(),
                                )

                            }?: emptyList()
                        } else {
                            emptyList()
                        }
                    }catch (e:Exception){
                        emptyList()
                    }
                }
            }.awaitAll().flatten() // thêm tất cả cầu lạc bộ với nhau bằng hàm flatten
            //thêm danh sách cầu lạc bộ với database
            if(allTeams.isNotEmpty()){
                livescoreDao.deleteTeams()
                livescoreDao.insertTeams(allTeams)
            }

        }catch (e:Exception){
            throw Exception("Error:{${e.localizedMessage}}")
        }

    }
}


class TeamViewModelFactory(
    private val repository: SearchRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchTeamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchTeamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}