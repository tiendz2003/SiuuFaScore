package com.example.minilivescore.data.networking

import com.example.minilivescore.data.model.football.LeagueMatches
import com.example.minilivescore.data.model.football.LeaguesStanding
import com.example.minilivescore.data.model.football.Match
import com.example.minilivescore.data.model.football.MatchLive
import com.example.minilivescore.data.model.football.SearchTeamsResponse
import com.example.minilivescore.data.model.football.TeamMatches
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LiveScoreService {
    //Tải tất cả các trận đấu
    @GET("competitions/{id}/matches")
    suspend fun getLeagueMatches(
        @Path ("id") id:String,
        @Query("matchday") matchDay:Int? = null
    ): LeagueMatches
    //bxh của giải đấu
    @GET("competitions/{id}/standings")
    suspend fun getStandingLeagues(
        @Path ("id") id: String
    ):Response<LeaguesStanding>

    //Tìm đội bóng theo Id
    @GET("competitions/{id}/teams")
    suspend fun searchTeams(
        @Path ("id") id: String
    ):Response<SearchTeamsResponse>
    //Lấy danh sách trận đấu của team cụ thể
    @GET("teams/{id}/matches")
    suspend fun getMatchesTeams(
        @Path ("id") id: String
    ):Response<TeamMatches>

    @GET("api/matches/{id}/stream")
    suspend fun getPlayBackUrl(@Path ("id") id: Int): MatchLive

    companion object{
        operator fun invoke(retrofit: Retrofit):LiveScoreService = retrofit.create()
    }
}