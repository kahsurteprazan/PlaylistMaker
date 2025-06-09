package com.example.playlistmaker.presentation.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.use_case.search.AddTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.ClearHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SearchTracksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val addTrackToHistoryUseCase: AddTrackToHistoryUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState: StateFlow<SearchState> = _searchState

    private val _historyState = MutableStateFlow<List<Track>>(emptyList())
    val historyState: StateFlow<List<Track>> = _historyState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var searchJob: Job? = null

    init {
        observeSearchQuery()
        observeHistory()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(SEARCH_DELAY)
                .filter { it.isNotEmpty() }
                .collect { query ->
                    if (query.isEmpty()) {
                        _searchState.value = SearchState.Empty
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchState.value = SearchState.Loading
            searchTracksUseCase(query)
                .collect { result ->
                    result.onSuccess { tracks ->
                        _searchState.value = if (tracks.isNotEmpty()) {
                            SearchState.Content(tracks)
                        } else {
                            SearchState.Empty
                        }
                    }.onFailure { e ->
                        _searchState.value = SearchState.Error(e)
                    }
                }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            getHistoryUseCase()
                .distinctUntilChanged()
                .collect { history ->
                    _historyState.value = history
                }
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            addTrackToHistoryUseCase(track).collect {
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearHistoryUseCase().collect {}
        }
    }

    companion object {
        const val SEARCH_DELAY = 2000L
    }
}



