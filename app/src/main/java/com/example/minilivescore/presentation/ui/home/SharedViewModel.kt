package com.example.minilivescore.presentation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.model.football.LeagueMatches
import com.example.minilivescore.data.model.football.LeaguesStanding
import com.example.minilivescore.data.repository.MatchRepositoryImpl
import com.example.minilivescore.domain.repository.MatchRepository
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor (
    private val repository: MatchRepository,
    private val savedStateHandle: SavedStateHandle
):ViewModel(){
    companion object{
        private const val MAX_MATCHDAY = 38
        private const val MIN_MATCHDAY = 1
        private const val DEFAULT_LEAGUE = "PL"
        private const val KEY_CURRENT_LEAGUE = "currentLeague"
        private const val KEY_SELECTED_MATCHDAY = "selectedMatchday"
    }
    private val _matches = MutableLiveData<Resource<LeagueMatches>>()
    val matches :LiveData<Resource<LeagueMatches>> = _matches

    private var _currentLeague =  savedStateHandle.getStateFlow("currentLeague", "PL")
    val currentLeague :StateFlow<String> = _currentLeague

    fun setCurrentLeague(league:String){
        if(_currentLeague.value != league){
            savedStateHandle["currentLeague"] = league
            Log.d("currentLeague","$league")
            viewModelScope.launch {
                fetchMatches(league)
            }
        }

    }

    private val _currentMatchday = MutableStateFlow<Int?>(null)
    val currentMatchday: MutableStateFlow<Int?> = _currentMatchday

    private val _matchday = MutableStateFlow<Int?>(null)
    val matchday: StateFlow<Int?> = _matchday


    private val _matchError = MutableStateFlow<String?>(null)
    val matchError: StateFlow<String?> = _matchError
    private val _selectedMatchday = savedStateHandle.getStateFlow("selectedMatchday", 0)
    val selectedMatchday: StateFlow<Int> = _selectedMatchday

    init {
        viewModelScope.launch {
            // Khởi tạo giá trị ban đầu cho selectedMatchday nếu chưa có
            if (_selectedMatchday.value == 0) {
                currentMatchday
                    .collect { currentMatchday ->
                    currentMatchday?.let {
                        savedStateHandle["selectedMatchday"] = it
                    }
                }
            }
        }


    }
    private fun selectedMatchday(matchday:Int){
        savedStateHandle["selectedMatchday"] = matchday
        fetchMatches(_currentLeague.value,matchday)
    }
    fun fetchMatches(leagueCode: String, count: Int? = null) {

        viewModelScope.launch {
            _matches.value = Resource.Loading()
            try {
                // Bây giờ mới fetch matches với matchday đã có
                val result = repository.getLeagueMatches(
                    leagueCode,
                    count ?: _matchday.value ?: _currentMatchday.value ?: 1
                )
                _matches.value = result

                if (result is Resource.Success) {
                    result.data?.let { leagueMatches ->
                        _currentMatchday.value = leagueMatches.matches.firstOrNull()?.season?.currentMatchday
                        if (count != null) {
                            _matchday.value = count
                        }
                    }
                } else if (result is Resource.Error) {
                    _matchError.value = result.message
                }
            } catch (e: Exception) {
                _matchError.value = e.message ?: "Đã xảy ra lỗi không xác định"
                _matches.value = Resource.Error(e.message ?: "Đã xảy ra lỗi không xác định")
            }
        }
    }
    //xử lý khi vòng đấu sau
    fun incrementMatchday() {
        _matchday.value?.let { currentMatchday ->
            when{
                currentMatchday <MAX_MATCHDAY -> selectedMatchday(currentMatchday + 1)
                else -> _matchError.value = "Đã đến vòng đấu cuối"
            }
        }
    }
    fun decrementMatchday() {
        _matchday.value?.let { currentMatchday ->
            when{
                currentMatchday > MIN_MATCHDAY -> selectedMatchday(currentMatchday - 1)
                else -> _matchError.value = "Đã đến vòng đấu đầu tiên"
            }
        }
    }
    fun clearError() {
        _matchError.value = null
    }

    private val _standingLeagues = MutableStateFlow<Resource<LeaguesStanding?>>(Resource.Loading())
    val standingLeague:StateFlow<Resource<LeaguesStanding?>> = _standingLeagues.asStateFlow()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getStandingLeagues(id:String){
        viewModelScope.launch {
            try {
                val result = repository.getStandingLeagues(id)
                if(result is Resource.Success){
                    _standingLeagues.value = Resource.Success(result.data)
                }else{
                    _error.value = "Error : ${result.message}"
                }
            }catch (e:Exception){
                _standingLeagues.value = Resource.Error(e.message?:"Đã xảy ra lỗi")
            }
        }
    }
}
