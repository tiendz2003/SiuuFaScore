package com.example.minilivescore.data.database

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.minilivescore.data.model.CoachEntity
import com.example.minilivescore.data.model.PlayerEntity
import com.example.minilivescore.data.model.TeamEntity
import com.example.minilivescore.data.model.TeamWithDetails

@Dao
interface LiveScoreDao {
    @Query("SELECT * FROM teams WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(shortName) LIKE '%' || LOWER(:query) || '%'")
    suspend fun searchTeam(query: String):List<TeamEntity>

    @Query("SELECT * FROM teams")
    suspend fun getAllTeams():List<TeamEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams:List<TeamEntity>)

    @Query("DELETE FROM teams")
    suspend fun deleteTeams()

    @Query("SELECT * FROM players WHERE teamId = :teamId")
    suspend fun getTeamPlayers(teamId: Int):List<PlayerEntity>

    @Query("SELECT * FROM coaches WHERE teamId = :teamId")
    suspend fun getTeamCoach(teamId: Int):CoachEntity?
    //Mỗi clb có nhiều cầu thủ(1-n)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)
    //mỗi đội chỉ có 1 hlv(1-1)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoach(coach: CoachEntity)

    @Query("DELETE FROM players")
    suspend fun deletePlayers()

    @Query("DELETE FROM coaches")
    suspend fun deleteCoaches()
    @Query("SELECT MAX(lastUpdate) FROM teams")
    suspend fun getLastUpdateTime(): Long?

    @Transaction
    @Query("SELECT * FROM teams WHERE id = :teamId")
    suspend fun getTeamWithDetails(teamId: Int): TeamWithDetails?
}