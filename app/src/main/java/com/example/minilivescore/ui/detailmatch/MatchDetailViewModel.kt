package com.example.minilivescore.ui.detailmatch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.data.model.SearchListResponse
import com.example.minilivescore.data.repository.HighlightRepository
import com.example.minilivescore.data.repository.MatchRepository
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MatchDetailViewModel(
    val highlightRepository: HighlightRepository
) :ViewModel(){
     private val _highlightState = MutableLiveData<Resource<String?>>()
    val highlightState: LiveData<Resource<String?>> = _highlightState
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    /*init {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatDate: Date? = date.parse("2024-09-30")
        viewModelScope.launch {
            Log.d("API Call", "Starting API call with date: $formatDate")
            val result = kotlin.runCatching {
                if (formatDate != null) {
                    highlightRepository.findHighlightVideo(homeTeam = "ManchesterUnited", awayTeam = "TottenHam", formatDate)
                } else {
                    throw IllegalArgumentException("Invalid date format")
                }
            }.onSuccess {
                Log.d("Result", "Success: $it")
            }.onFailure {
                Log.e("Result", "Error: ${it.message}", it)
            }

        }
    }*/
    fun loadHighlight(homeTeam:String,awayTeam:String,matchEndTime:Date){
        viewModelScope.launch {
            _highlightState.value = Resource.Loading()
            val result = highlightRepository.findHighlightVideo(homeTeam,awayTeam,matchEndTime)
            _highlightState.value = result
        }
    }
}