package com.example.minilivescore.ui

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.minilivescore.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val repository:MatchRepository,
    private val savedStateHandle: SavedStateHandle
):ViewModel() {
    private val _currentLeague = MutableStateFlow(savedStateHandle.get<String>("currentLeague")?:"PL")
    val currentLeague :StateFlow<String> = _currentLeague.asStateFlow()

    private val _currentTab = MutableStateFlow(savedStateHandle.get<Int>("currentTab")?:0)
    val currentTab:StateFlow<Int> = _currentTab.asStateFlow()

    fun setCurrentLeague(league:String){
        _currentLeague.value = league
        savedStateHandle["currentLeague"] = league
    }
    fun setCurrentTab(tab:Int){
        _currentTab.value = tab
        savedStateHandle["currentTab"] =tab
    }
}
class MainViewModelFactory(
    private val repository: MatchRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}