package com.example.minilivescore.domain.repository

import com.example.minilivescore.data.model.football.LeagueMatches
import com.example.minilivescore.data.model.football.LeaguesStanding
import com.example.minilivescore.data.model.football.MatchLive
import com.example.minilivescore.presentation.ui.detailmatch.comment.LiveComment
import com.example.minilivescore.utils.Resource
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    // Lấy danh sách trận đấu trong một giải đấu cụ thể
    suspend fun getLeagueMatches(leagueCode: String, round: Int? = null): Resource<LeagueMatches>

    // Lấy bảng xếp hạng của một giải đấu
    suspend fun getStandingLeagues(id: String): Resource<LeaguesStanding>

    // Lấy URL phát lại trận đấu
    suspend fun getPlayBackUrl(id: Int): Resource<MatchLive>

    // Lấy danh sách các bình luận trực tiếp của một trận đấu
    fun getComments(matchId: String): Flow<List<LiveComment>>

    // Đăng một bình luận trực tiếp
    fun postComment(matchId: String, cmtLive: LiveComment)
}