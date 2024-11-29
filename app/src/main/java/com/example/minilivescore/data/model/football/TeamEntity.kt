package com.example.minilivescore.data.model.football

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey
    val id:Int,
    val name:String,
    val shortName:String,
    val tla:String,
    val crest:String,
    val leagueId:String,
    val lastUpdate:Long =System.currentTimeMillis(),
    )


@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id:Int,
    val name:String,
    val teamId: Int,
    val position:String,
    val dateOfBirth:String?,
    val nationality :String
    )


@Entity(tableName = "coaches")
data class CoachEntity(
    @PrimaryKey
    val id:Int,
    val name:String,
    val teamId: Int,
    val firstName: String,
    val lastName: String,
    val nationality: String,

)
