package com.example.minilivescore.data.model


import android.os.Parcelable
import com.squareup.moshi.Json
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Keep
data class LeagueMatches(
    @Json(name = "competition")
    val competition: Competition,
    @Json(name = "filters")
    val filters: Filters,
    @Json(name = "matches")
    val matches: List<Matche>,
    @Json(name = "resultSet")
    val resultSet: ResultSet
):Parcelable {
    @Parcelize
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
    ):Parcelable
    @Parcelize
    @Keep
    data class Filters(
        @Json(name = "matchday")
        val matchday: String,
        @Json(name = "season")
        val season: String
    ):Parcelable
    @Parcelize
    @Keep
    data class Matche(
        @Json(name = "area")
        val area: Area,
        @Json(name = "awayTeam")
        val awayTeam: AwayTeam,
        @Json(name = "competition")
        val competition: Competition,
        @Json(name = "group")
        val group: @RawValue Any? ,
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
        val referees: List<Referee>,
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
    ) :Parcelable{
        @Parcelize
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
        ):Parcelable
        @Parcelize
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
        ):Parcelable
        @Parcelize
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
        ):Parcelable
        @Parcelize
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
        ):Parcelable
        @Parcelize
        @Keep
        data class Odds(
            @Json(name = "msg")
            val msg: String
        ):Parcelable
        @Parcelize
        @Keep
        data class Referee(
            @Json(name = "id")
            val id: Int,
            @Json(name = "name")
            val name: String,
            @Json(name = "nationality")
            val nationality: String,
            @Json(name = "type")
            val type: String
        ):Parcelable
        @Parcelize
        @Keep
        data class Score(
            @Json(name = "duration")
            val duration: String?,
            @Json(name = "fullTime")
            val fullTime: FullTime?,
            @Json(name = "halfTime")
            val halfTime: HalfTime?,
            @Json(name = "winner")
            val winner: String?
        ):Parcelable {
            @Parcelize
            @Keep
            data class FullTime(
                @Json(name = "away")
                val away: Int?,
                @Json(name = "home")
                val home: Int?
            ):Parcelable

            @Parcelize
            @Keep
            data class HalfTime(
                @Json(name = "away")
                val away: Int?,
                @Json(name = "home")
                val home: Int?
            ):Parcelable
        }
        @Parcelize
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
            val winner: @RawValue Any?
        ):Parcelable
    }
    @Parcelize
    @Keep
    data class ResultSet(
        @Json(name = "count")
        val count: Int,
        @Json(name = "first")
        val first: String,
        @Json(name = "last")
        val last: String,
        @Json(name = "played")
        val played: Int
    ):Parcelable
}