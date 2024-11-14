package com.example.minilivescore.ui.searchteam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.model.TeamEntity
import com.example.minilivescore.data.repository.SearchRepository
import com.example.minilivescore.utils.LiveScoreMiniServiceLocator
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchTeamViewModel(
    private val searchRepository: SearchRepository
):ViewModel() {
    private val _searchResult = MutableStateFlow<Resource<List<TeamEntity>>>(Resource.Success(
        emptyList()))

    val searchResult = _searchResult.asStateFlow()
    init {
       viewModelScope.launch {
           val result = kotlin.runCatching {
               LiveScoreMiniServiceLocator.liveScoreApiService.searchTeams("PL")
           }
           println("result= $result")
       }
    }
    fun searchTeam(query:String){
        viewModelScope.launch {
            _searchResult.value = Resource.Loading()

            _searchResult.value = searchRepository.searchTeam(query)

        }
    }
}