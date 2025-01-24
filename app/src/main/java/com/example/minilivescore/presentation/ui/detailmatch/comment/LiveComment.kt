package com.example.minilivescore.presentation.ui.detailmatch.comment

data class LiveComment(
    val userId:String ="",
    val userName:String="",
    val userImage:String="",
    val comment:String="",
    val timestamp:Long =System.currentTimeMillis()
)
data class MatchComment(
    val matchId :String= "",
    val listCmt:List<LiveComment> = listOf()
)