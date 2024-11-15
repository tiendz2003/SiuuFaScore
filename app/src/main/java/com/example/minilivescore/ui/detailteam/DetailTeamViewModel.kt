package com.example.minilivescore.ui.detailteam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.database.AppDatabase
import com.example.minilivescore.data.database.LiveScoreDao
import com.example.minilivescore.data.model.SearchTeamsResponse
import com.example.minilivescore.data.model.TeamWithDetails
import com.example.minilivescore.data.repository.SearchRepository
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailTeamViewModel(val searchRepository: SearchRepository):ViewModel() {
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
class DetailTeamViewModelFactory(
    private val repository: SearchRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailTeamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailTeamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}