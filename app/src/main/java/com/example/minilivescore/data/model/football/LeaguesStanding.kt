package com.example.minilivescore.data.model.football


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class LeaguesStanding(
    @Json(name = "area")
    val area: Area,
    @Json(name = "competition")
    val competition: Competition,
    @Json(name = "filters")
    val filters: Filters,
    @Json(name = "season")
    val season: Season,
    @Json(name = "standings")
    val standings: List<Standing>
) {
    @Keep
    data class Area(
        @Json(name = "code")
        val code: String,
        @Json(name = "flag")
        val flag: String,
        @Json(name = "id")
        val id: Int,
        @Json(name = "name")
        val name: String
    )

    @Keep
    data class Competition(
        @Json(name = "code")
        val code: String,
        @Json(name = "emblem")
        val emblem: String,
        @Json(name = "id")
        val id: Int,
        @Json(name = "name")
        val name: String,
        @Json(name = "type")
        val type: String
    )

    @Keep
    data class Filters(
        @Json(name = "season")
        val season: String
    )

    @Keep
    data class Season(
        @Json(name = "currentMatchday")
        val currentMatchday: Int,
        @Json(name = "endDate")
        val endDate: String,
        @Json(name = "id")
        val id: Int,
        @Json(name = "startDate")
        val startDate: String,
        @Json(name = "winner")
        val winner: Any?
    )

    @Keep
    data class Standing(
        @Json(name = "group")
        val group: Any?,
        @Json(name = "stage")
        val stage: String,
        @Json(name = "table")
        val table: List<Table>,
        @Json(name = "type")
        val type: String
    ) {
        @Keep
        data class Table(
            @Json(name = "draw")
            val draw: Int,
            @Json(name = "form")
            val form: Any?,
            @Json(name = "goalDifference")
            val goalDifference: Int,
            @Json(name = "goalsAgainst")
            val goalsAgainst: Int,
            @Json(name = "goalsFor")
            val goalsFor: Int,
            @Json(name = "lost")
            val lost: Int,
            @Json(name = "playedGames")
            val playedGames: Int,
            @Json(name = "points")
            val points: Int,
            @Json(name = "position")
            val position: Int,
            @Json(name = "team")
            val team: Team,
            @Json(name = "won")
            val won: Int
        ) {
            @Keep
            data class Team(
                @Json(name = "crest")
                val crest: String,
                @Json(name = "id")
                val id: Int,
                @Json(name = "name")
                val name: String,
                @Json(name = "shortName")
                val shortName: String,
                @Json(name = "tla")
                val tla: String
            )
        }
    }
}