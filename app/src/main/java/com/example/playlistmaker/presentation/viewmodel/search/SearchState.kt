package com.example.playlistmaker.presentation.viewmodel.search

import com.example.playlistmaker.domain.model.Track

sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    data class Error(val throwable: Throwable) : SearchState()
}