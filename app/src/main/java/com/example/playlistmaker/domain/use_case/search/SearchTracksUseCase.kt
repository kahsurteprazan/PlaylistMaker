package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchTracksUseCase(private val searchRepository: SearchRepository) {

    fun search(query: String, callback: (Result<List<Track>>) -> Unit) {
        searchRepository.search(query, callback)
    }
}