package com.example.minilivescore.presentation.ui.searchteam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.model.football.TeamEntity
import com.example.minilivescore.domain.repository.SearchRepository
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchTeamViewModel @Inject constructor (
    private val searchRepository: SearchRepository
):ViewModel() {
    private val _searchResult = MutableStateFlow<Resource<List<TeamEntity>>>(Resource.Success(
        emptyList()))

    val searchResult = _searchResult.asStateFlow()

    fun searchTeam(query:String){
        viewModelScope.launch {
            _searchResult.value = Resource.Loading()

            _searchResult.value = searchRepository.searchTeam(query)

        }
    }
}