package com.example.minilivescore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("teams")
data class TeamEntity(
    @PrimaryKey
    val id:Int,
    val name:String,
    val shortName:String,
    val tla:String,
    val crest:String,
    val leagueId:String,
    val lastUpdate:Long =System.currentTimeMillis()
    )