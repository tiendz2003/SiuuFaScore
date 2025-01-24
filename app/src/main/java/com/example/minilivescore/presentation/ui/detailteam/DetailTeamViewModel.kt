package com.example.minilivescore.presentation.ui.detailteam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.model.football.TeamWithDetails
import com.example.minilivescore.data.repository.SearchRepositoryImpl
import com.example.minilivescore.domain.repository.SearchRepository
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTeamViewModel @Inject constructor (
    private val searchRepository: SearchRepository
):ViewModel() {
    private val _teamDetails = MutableStateFlow<Resource<TeamWithDetails>>(Resource.Loading())
    val teamDetails = _teamDetails.asStateFlow()

    fun getTeamDetails(teamId: Int){
        viewModelScope.launch {
            try{
                _teamDetails.value = Resource.Loading()
                val result = searchRepository.getTeamDetails(teamId)

                _teamDetails.value = result
            }catch (e:Exception){
                _teamDetails.value = Resource.Error(e.message ?: "Đã xảy ra lỗi")
            }
        }
    }

}
