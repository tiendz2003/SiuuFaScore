package com.example.minilivescore.presentation.ui.detailmatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.model.football.MatchLive
import com.example.minilivescore.domain.repository.HighlightRepository
import com.example.minilivescore.domain.repository.MatchRepository
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val highlightRepository: HighlightRepository,
    private val matchRepository: MatchRepository
) :ViewModel(){
     private val _highlightState = MutableLiveData<Resource<String?>>()
    val highlightState: LiveData<Resource<String?>> = _highlightState
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val _url = MutableLiveData<Resource<String>>()
    val url :LiveData<Resource<String>> = _url
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
}