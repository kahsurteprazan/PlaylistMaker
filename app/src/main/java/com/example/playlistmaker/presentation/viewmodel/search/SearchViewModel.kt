package com.example.playlistmaker.presentation.viewmodel.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.use_case.search.AddTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.ClearHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SearchTracksUseCase

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val addTrackToHistoryUseCase: AddTrackToHistoryUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<List<Track>>()
    val historyState: LiveData<List<Track>> = _historyState

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchState() {
        _searchQuery.value = ""
    }

    fun searchDebounced(query: String) {
        _searchState.value = SearchState.Loading
        searchTracksUseCase.search(query) { result ->
            result.onSuccess { tracks ->
                _searchState.value = if (tracks.isNotEmpty()) {
                    SearchState.Content(tracks)
                } else {
                    SearchState.Empty
                }
            }.onFailure {
                _searchState.value = SearchState.Error(it)
            }
        }
    }

    fun loadHistory() {
        _historyState.value = getHistoryUseCase()
    }

    fun addToHistory(track: Track) {
        addTrackToHistoryUseCase(track)
        loadHistory()
    }

    fun clearHistory() {
        clearHistoryUseCase()
        loadHistory()
    }

    companion object {
        const val SEARCH_DELAY = 2000L
    }
}


