package com.example.minilivescore.data.networking

import com.example.minilivescore.data.model.LeagueMatches
import com.example.minilivescore.data.model.LeaguesStanding
import com.example.minilivescore.data.model.Match
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
    ):Match
    @GET("competitions/{id}/matches")
    suspend fun getLeagueMatches(
        @Path ("id") id:String,
        @Query("matchday") matchDay:Int? = null
    ):LeagueMatches
    @GET("competitions/{id}/standings")
    suspend fun getStandingLeagues(
        @Path ("id") id: String
    ):Response<LeaguesStanding>
    companion object{
        operator fun invoke(retrofit: Retrofit):LiveScoreService = retrofit.create()
    }
}