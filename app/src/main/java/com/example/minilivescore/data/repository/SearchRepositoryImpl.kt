package com.example.minilivescore.data.repository

import android.util.Log
import com.example.minilivescore.data.database.LiveScoreDao
import com.example.minilivescore.data.model.football.CoachEntity
import com.example.minilivescore.data.model.football.PlayerEntity
import com.example.minilivescore.data.model.football.TeamEntity
import com.example.minilivescore.data.model.football.TeamWithDetails
import com.example.minilivescore.data.networking.LiveScoreService
import com.example.minilivescore.domain.repository.SearchRepository
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val liveScoreApiService: LiveScoreService,
    private val livescoreDao:LiveScoreDao
): SearchRepository {
    private val leagues = listOf("PL", "SA", "PD")

    //xóa cache sau 24h
    private val CACHE_DURATION_CLEAN = TimeUnit.HOURS.toMillis(24)

    //Tìm kiếm theo tên đội bóng
    override suspend fun searchTeam(query: String): Resource<List<TeamEntity>> {
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
    override suspend fun getTeamDetails(teamId: Int): Resource<TeamWithDetails> {
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

  /*  private suspend fun refreshCache() = coroutineScope {
        try {
            //Lưu như này hơi tốn kém gây ra tràn ram khi dữ liệu lớn.Nên thay trực tiếp vào cơ sở dữ liệu(dùng batch insert)(fixed)
            val allTeams = mutableListOf<TeamEntity>()
            val allPlayer = mutableListOf<PlayerEntity>()
            val allCoaches = mutableListOf<CoachEntity>()
            //tải tất cả câu lạc bộ
           leagues.map { leagueId ->
                async {
                        val response = liveScoreApiService.searchTeams(leagueId)
                        if (response.isSuccessful) {
                            response.body()?.teams?.forEach { team ->

                                //thêm danh sách đội bóng
                                allTeams.add(
                                    TeamEntity(
                                    id = team.id,
                                    name = team.name,
                                    tla = team.tla,
                                    crest = team.crest,
                                    leagueId = leagueId,
                                    shortName = team.shortName,
                                    lastUpdate = System.currentTimeMillis(),
                                )
                                )
                                //thêm danh sachs cầu thủ
                                team.squad.forEach { player ->
                                    allPlayer.add(
                                        PlayerEntity(
                                        id = player.id,
                                        name = player.name,
                                        position = player.position,
                                        teamId = team.id,
                                       dateOfBirth = player.dateOfBirth,
                                        nationality = player.nationality,
                                    )
                                    )
                                }
                                //thêm danh sách huấn luyện viên
                                team.coach.let { coach ->
                                    allCoaches.add(
                                        CoachEntity(
                                        id = coach.id,
                                        name = coach.name,
                                        teamId = team.id,
                                        nationality = coach.nationality,
                                        lastName = coach.lastName,
                                        firstName = coach.firstName
                                    )
                                    )
                                }
                            }
                        }
                }
            }.awaitAll() // thêm tất cả cầu lạc bộ với nhau bằng hàm flatten
            //thêm danh sách cầu lạc bộ với database
            if(allTeams.isNotEmpty()){
               livescoreDao.apply {
                   deleteTeams()
                   deletePlayers()
                   deleteCoaches()
                   insertTeams(allTeams)
                   insertPlayers(allPlayer)
                   allCoaches.forEach { insertCoach(it)
                   }
               }
            }
        } catch (e: Exception) {
            throw Exception("Error:{${e.localizedMessage}}")
        }

    }*/
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


