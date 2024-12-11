package com.example.minilivescore.presentation.ui.detailmatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.model.football.FavoriteTeam
import com.example.minilivescore.data.model.football.MatchLive
import com.example.minilivescore.domain.repository.FavoriteTeamRepository
import com.example.minilivescore.domain.repository.HighlightRepository
import com.example.minilivescore.domain.repository.MatchRepository
import com.example.minilivescore.presentation.ui.detailmatch.comment.LiveComment
import com.example.minilivescore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val highlightRepository: HighlightRepository,
    private val matchRepository: MatchRepository,
    private val firebaseAuth:FirebaseAuth,
    private val favRepository:FavoriteTeamRepository
) :ViewModel(){
     private val _highlightState = MutableLiveData<Resource<String?>>()
    val highlightState: LiveData<Resource<String?>> = _highlightState
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val _url = MutableLiveData<Resource<String>>()
    val url :LiveData<Resource<String>> = _url
    fun addFavoriteTeam(team:FavoriteTeam){
        viewModelScope.launch {
            favRepository.addFavoriteTeam(team)
        }
    }
    fun getPlayBackUrl(matchId:Int):LiveData<Resource<MatchLive>> = liveData {
            emit(matchRepository.getPlayBackUrl(matchId))
    }
    fun loadHighlight(title:String){
        viewModelScope.launch {
            _highlightState.value = Resource.Loading()
            val result = highlightRepository.findVideoInList(title)
            _highlightState.value = result
        }
    }
    //Comment truc tiep
    private val _cmt = MutableStateFlow<Resource<List<LiveComment>>>(Resource.Loading())
    val cmt  get()  = _cmt.asStateFlow()
    fun postComment(matchId: String,cmtLive:String){
        val currentUser = firebaseAuth.currentUser
        val comment = LiveComment(
            userId = currentUser?.uid?:"",
            userName = currentUser?.displayName?:"Ẩn danh",
            comment = cmtLive,
            userImage = currentUser?.photoUrl?.toString() ?:"",
        )
        viewModelScope.launch {
            matchRepository.postComment(matchId,comment)
            //tải lại cmt
            getComments(matchId)
        }
    }
    fun getComments(matchId:String){
        viewModelScope.launch {
            _cmt.value = Resource.Loading()
            matchRepository.getComments(matchId).collect{newCmt ->
                _cmt.value = Resource.Success(newCmt)
            }

        }
    }
}