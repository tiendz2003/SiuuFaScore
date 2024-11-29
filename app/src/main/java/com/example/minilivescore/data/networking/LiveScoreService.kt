package com.example.minilivescore.data.networking

import com.example.minilivescore.data.model.football.LeagueMatches
import com.example.minilivescore.data.model.football.LeaguesStanding
import com.example.minilivescore.data.model.football.Match
import com.example.minilivescore.data.model.football.MatchLive
import com.example.minilivescore.data.model.football.SearchTeamsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LiveScoreService {
    @GET("matches")
    suspend fun getMatches(
        @Query("dateFrom") dateFrom:String,
        @Query("dateTo") dateTo:String,
    ): Match
    @GET("competitions/{id}/matches")
    suspend fun getLeagueMatches(
        @Path ("id") id:String,
        @Query("matchday") matchDay:Int? = null
    ): LeagueMatches
    @GET("competitions/{id}/standings")
    suspend fun getStandingLeagues(
        @Path ("id") id: String
    ):Response<LeaguesStanding>

    @GET("competitions/{id}/teams")
    suspend fun searchTeams(
        @Path ("id") id: String
    ):Response<SearchTeamsResponse>

    @GET("api/matches/{id}/stream")
    suspend fun getPlayBackUrl(@Path ("id") id: Int): MatchLive

    companion object{
        operator fun invoke(retrofit: Retrofit):LiveScoreService = retrofit.create()
    }
}