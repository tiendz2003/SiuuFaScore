package com.example.minilivescore.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.minilivescore.data.model.TeamEntity

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
    @Query("SELECT MAX(lastUpdate) FROM teams")
    suspend fun getLastUpdateTime(): Long?
}