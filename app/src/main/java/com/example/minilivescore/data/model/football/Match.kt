package com.example.minilivescore.data.model.football


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class Match(
    @Json(name = "filters")
    val filters: Filters,
    @Json(name = "matches")
    val matches: List<Matches>,
    @Json(name = "resultSet")
    val resultSet: ResultSet?
) {
    @Keep
    data class Filters(
        @Json(name = "dateFrom")
        val dateFrom: String,
        @Json(name = "dateTo")
        val dateTo: String,
        @Json(name = "permission")
        val permission: String
    )

    @Keep
    data class Matches(
        @Json(name = "area")
        val area: Area,
        @Json(name = "awayTeam")
        val awayTeam: AwayTeam,
        @Json(name = "competition")
        val competition: Competition,
        @Json(name = "group")
        val group: Any?,
        @Json(name = "homeTeam")
        val homeTeam: HomeTeam,
        @Json(name = "id")
        val id: Int,
        @Json(name = "lastUpdated")
        val lastUpdated: String,
        @Json(name = "matchday")
        val matchday: Int,
        @Json(name = "odds")
        val odds: Odds,
        @Json(name = "referees")
        val referees: List<Any>,
        @Json(name = "score")
        val score: Score,
        @Json(name = "season")
        val season: Season,
        @Json(name = "stage")
        val stage: String,
        @Json(name = "status")
        val status: String,
        @Json(name = "utcDate")
        val utcDate: String
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
        data class AwayTeam(
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
        data class HomeTeam(
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

        @Keep
        data class Odds(
            @Json(name = "msg")
            val msg: String
        )

        @Keep
        data class Score(
            @Json(name = "duration")
            val duration: String,
            @Json(name = "fullTime")
            val fullTime: FullTime,
            @Json(name = "halfTime")
            val halfTime: HalfTime,
            @Json(name = "winner")
            val winner: String?
        ) {
            @Keep
            data class FullTime(
                @Json(name = "away")
                val away: Int?,
                @Json(name = "home")
                val home: Int?
            )

            @Keep
            data class HalfTime(
                @Json(name = "away")
                val away: Int?,
                @Json(name = "home")
                val home: Int?
            )
        }

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
    }

    @Keep
    data class ResultSet(
        @Json(name = "competitions")
        val competitions: String?,
        @Json(name = "count")
        val count: Int?,
        @Json(name = "first")
        val first: String?,
        @Json(name = "last")
        val last: String?,
        @Json(name = "played")
        val played: Int?
    )
}
data class MatchLive(
    @Json(name = "matchId") // Đảm bảo trường này phải khớp với JSON
    val id: Int?, // Có thể để null nếu không bắt buộc
    @Json(name = "awayTeam")
    val awayTeam: String?,
    @Json(name = "homeTeam")
    val homeTeam: String?,
    @Json(name = "matchDate")
    val date: String?,
    @Json(name = "ivsPlaybackUrl") // Đổi lại cho đúng tên trường
    val playBackUrl: String?
)