package com.example.minilivescore.domain.repository

import com.example.minilivescore.data.model.football.FavoriteTeam
import com.example.minilivescore.data.model.football.TeamMatches
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteTeam(): Flow<List<   FavoriteTeam>>
    fun getEnableNotificationTeam(): Flow<List<FavoriteTeam>>
    suspend fun addFavoriteTeam(team: FavoriteTeam)
    suspend fun removeFavoriteTeam(teamId: String)
    suspend fun updateNotificationPref(teamId: String, isEnable: Boolean)
    suspend fun getMatchesByID(teamId: String): Resource<TeamMatches>
}