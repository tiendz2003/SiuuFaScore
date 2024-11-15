package com.example.minilivescore.data.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.minilivescore.data.database.LiveScoreDao
import com.example.minilivescore.data.model.CoachEntity
import com.example.minilivescore.data.model.PlayerEntity
import com.example.minilivescore.data.model.TeamEntity
import com.example.minilivescore.data.model.TeamWithDetails
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
) {
    private lateinit var coach: CoachEntity
    private lateinit var player: List<PlayerEntity>
    private val leagues = listOf("PL", "SA", "PD")

    //xóa cache sau 24h
    private val CACHE_DURATION_CLEAN = TimeUnit.HOURS.toMillis(24)

    //Tìm kiếm theo tên đội bóng
    suspend fun searchTeam(query: String): Resource<List<TeamEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                updateCache()

                val teams = livescoreDao.searchTeam(query)
                Resource.Success(teams)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Đã xảy ra lỗi")
            }
        }
    }

    //Thông tin cho tiết đội bóng
    suspend fun getTeamDetails(teamId: Int): Resource<TeamWithDetails> {
        return withContext(Dispatchers.IO) {
            try {

              /*  val team = livescoreDao.searchTeam(teamId.toString()).firstOrNull()
                val player = livescoreDao.getTeamPlayers(teamId)
                val coach = livescoreDao.getTeamCoach(teamId)*/
                val teamDetails = livescoreDao.getTeamWithDetails(teamId)
                Log.d("Repository", "Team: $teamDetails")
                Log.d("Repository", "Số cầu thủ: ${teamDetails?.players?.size}")
                Log.d("Repository", "Coach: ${teamDetails?.coach}")
                if(teamDetails != null){
                   // Resource.Success(TeamWithDetails(team,player,coach))
                    Resource.Success(teamDetails)
                }else{
                    Resource.Error("Không tìm thấy thông tin đội bóng")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Đã xảy ra lỗi")
            }
        }
    }
    //Kiểm tra xem trong database có data và reset lại database nếu quá thời gian
    private suspend fun updateCache() {
        val allTeams = livescoreDao.getAllTeams()
        val lastUpdate = livescoreDao.getLastUpdateTime() ?: 0
        val currentTime = System.currentTimeMillis()
        if (allTeams.isEmpty() || currentTime - lastUpdate > CACHE_DURATION_CLEAN) {
            refreshCache()
        }
    }

    private suspend fun refreshCache() = coroutineScope {
        try {
            Resource.Loading<List<TeamEntity>>()

            val allTeams = mutableListOf<TeamEntity>()
            val allPlayer = mutableListOf<PlayerEntity>()
            val allCoaches = mutableListOf<CoachEntity>()
            //tải tất cả câu lạc bộ
           leagues.map { leagueId ->
                async {
                    try {
                        val response = liveScoreApiService.searchTeams(leagueId)
                        if (response.isSuccessful) {
                            response.body()?.teams?.map { team ->
                                //thêm danh sách đội bóng
                                allTeams.add(TeamEntity(
                                    id = team.id,
                                    name = team.name,
                                    tla = team.tla,
                                    crest = team.crest,
                                    leagueId = leagueId,
                                    shortName = team.shortName,
                                    lastUpdate = System.currentTimeMillis(),
                                ))
                                //thêm danh sachs cầu thủ
                                team.squad.forEach { player ->
                                    allPlayer.add(PlayerEntity(
                                        id = player.id,
                                        name = player.name,
                                        position = player.position,
                                        teamId = team.id,
                                       dateOfBirth = player.dateOfBirth,
                                        nationality = player.nationality,
                                    ))
                                }
                                //thêm danh sách huấn luyện viên
                                team.coach.let { coach ->
                                    allCoaches.add(CoachEntity(
                                        id = coach.id,
                                        name = coach.name,
                                        teamId = team.id,
                                        nationality = coach.nationality,
                                        lastName = coach.lastName,
                                        firstName = coach.firstName
                                    ))
                                }
                            } ?: emptyList()
                        } else {
                            emptyList()
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }.awaitAll() // thêm tất cả cầu lạc bộ với nhau bằng hàm flatten
            //thêm danh sách cầu lạc bộ với database
            if(allTeams.isNotEmpty()){
                livescoreDao.deleteTeams()
                livescoreDao.deletePlayers()
                livescoreDao.deleteCoaches()

                livescoreDao.insertTeams(allTeams)
                livescoreDao.insertPlayers(allPlayer)
                allCoaches.forEach {
                    livescoreDao.insertCoach(it)
                }
            }

        } catch (e: Exception) {
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