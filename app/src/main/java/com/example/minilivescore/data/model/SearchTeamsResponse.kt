package com.example.minilivescore.data.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SearchTeamsResponse(
    @Json(name = "competition")
    val competition: Competition,
    @Json(name = "count")
    val count: Int,
    @Json(name = "filters")
    val filters: Filters,
    @Json(name = "season")
    val season: Season,
    @Json(name = "teams")
    val teams: List<Team>
) {
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
    data class Team(
        @Json(name = "address")
        val address: String,
        @Json(name = "area")
        val area: Area,
        @Json(name = "clubColors")
        val clubColors: String,
        @Json(name = "coach")
        val coach: Coach,
        @Json(name = "crest")
        val crest: String,
        @Json(name = "founded")
        val founded: Int,
        @Json(name = "id")
        val id: Int,
        @Json(name = "lastUpdated")
        val lastUpdated: String,
        @Json(name = "name")
        val name: String,
        @Json(name = "runningCompetitions")
        val runningCompetitions: List<RunningCompetition>,
        @Json(name = "shortName")
        val shortName: String,
        @Json(name = "squad")
        val squad: List<Squad>,
        @Json(name = "staff")
        val staff: List<Any?>,
        @Json(name = "tla")
        val tla: String,
        @Json(name = "venue")
        val venue: String,
        @Json(name = "website")
        val website: String
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
        data class Coach(
            @Json(name = "contract")
            val contract: Contract,
            @Json(name = "dateOfBirth")
            val dateOfBirth: String,
            @Json(name = "firstName")
            val firstName: String,
            @Json(name = "id")
            val id: Int,
            @Json(name = "lastName")
            val lastName: String,
            @Json(name = "name")
            val name: String,
            @Json(name = "nationality")
            val nationality: String
        ) {
            @Keep
            data class Contract(
                @Json(name = "start")
                val start: String,
                @Json(name = "until")
                val until: String
            )
        }

        @Keep
        data class RunningCompetition(
            @Json(name = "code")
            val code: String,
            @Json(name = "emblem")
            val emblem: String?,
            @Json(name = "id")
            val id: Int,
            @Json(name = "name")
            val name: String,
            @Json(name = "type")
            val type: String
        )

        @Keep
        data class Squad(
            @Json(name = "dateOfBirth")
            val dateOfBirth: String?,
            @Json(name = "id")
            val id: Int,
            @Json(name = "name")
            val name: String,
            @Json(name = "nationality")
            val nationality: String,
            @Json(name = "position")
            val position: String
        )
    }
}