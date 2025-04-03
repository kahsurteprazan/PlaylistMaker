package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.use_case.search.AddTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.ClearHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SearchTracksUseCase

class SearchViewModelFactory(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val addTrackToHistoryUseCase: AddTrackToHistoryUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                searchTracksUseCase,
                addTrackToHistoryUseCase,
                getHistoryUseCase,
                clearHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}