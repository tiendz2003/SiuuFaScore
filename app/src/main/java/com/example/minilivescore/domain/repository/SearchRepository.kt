package com.example.minilivescore.domain.repository

import com.example.minilivescore.data.model.football.TeamEntity
import com.example.minilivescore.data.model.football.TeamWithDetails
import com.example.minilivescore.utils.Resource

interface SearchRepository {
    suspend fun searchTeam(query: String): Resource<List<TeamEntity>>
    suspend fun getTeamDetails(teamId: Int): Resource<TeamWithDetails>
}