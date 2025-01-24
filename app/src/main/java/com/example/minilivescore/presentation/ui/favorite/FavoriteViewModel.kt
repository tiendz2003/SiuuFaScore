package com.example.minilivescore.presentation.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.model.football.FavoriteTeam
import com.example.minilivescore.data.model.football.TeamMatches
import com.example.minilivescore.data.repository.FavoriteTeamRepositoryImpl
import com.example.minilivescore.domain.repository.FavoriteRepository
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    val repository: FavoriteRepository
):ViewModel() {
    private val _favoriteTeams = MutableStateFlow<Resource<List<FavoriteTeam>>>(Resource.Loading())
    val favoriteTeams  get()  = _favoriteTeams.asStateFlow()

    init {
        getFavoriteTeam()
       /* viewModelScope.launch {
            repository.getFavoriteTeam().collect {
                _favoriteTeams.value = it
            }
        }*/
        /*viewModelScope.launch {
            val favEnableTeam =repository.getEnableNotificationTeam()
                .first()
                .map {team->
                    team.id
                }
                .toSet()
            Log.d("repository", "$favEnableTeam")
            val listUpComingMatch = coroutineScope {
                favEnableTeam.map {teamId ->
                    async {
                        repository.getMatchesByID(teamId)
                    }
                }.awaitAll()
            }
            Log.d("repository","$listUpComingMatch")
            val upComingMatch:List<TeamMatches.Matche> = listUpComingMatch.flatMap {
                it.data?.matches ?: emptyList<TeamMatches.Matche>()
            }.filter {
                it.homeTeam.id.toString() in favEnableTeam || it.awayTeam.id.toString() in favEnableTeam
                        && it.status == "SCHEDULED"//Lọc đội bóng yêu thích và các trận chưa đá
            }
            Log.d("repository","$upComingMatch")
    }*/
    }
     fun getFavoriteTeam(){
        viewModelScope.launch {
            _favoriteTeams.value = Resource.Loading()
            repository.getFavoriteTeam().collect {teams->
                val result = Resource.Success(teams)
                _favoriteTeams.value = result
            }
        }
    }
     fun toggleNotification(teamId: String, isEnable: Boolean){
        viewModelScope.launch {
            repository.updateNotificationPref(teamId, isEnable)
            //Xử lý đặt lịch worker chỉ khi có đội bóng yêu thích trong firebase
        }
    }
    private val _deleteStatus = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val deleteStatus get() = _deleteStatus.asStateFlow()
    fun deleteFavoriteTeam(teamId:String) {
        viewModelScope.launch {
            try {
                repository.removeFavoriteTeam(teamId)
                _deleteStatus.value = Resource.Success(Unit)
                getFavoriteTeam() //tải lại
            } catch (e: Exception) {
                _deleteStatus.value = Resource.Error("Lỗi khi xóa: ${e.message}")
            }
        }
    }

    private val _matches = MutableStateFlow<Resource<TeamMatches>>(Resource.Loading())
    val matches get() = _matches.asStateFlow()
    fun getMatchesByID(teamId: String){
        _matches.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getMatchesByID(teamId)
            if(result is Resource.Success){
                _matches.value = Resource.Success(result.data!!)
            }else{
                _matches.value = Resource.Error(result.message!!)
            }
        }
    }

}

