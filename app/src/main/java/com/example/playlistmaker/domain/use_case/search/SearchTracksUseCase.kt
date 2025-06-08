package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchTracksUseCase(private val searchRepository: SearchRepository) {
    suspend fun search(query: String): Result<List<Track>> {
        return searchRepository.search(query)
    }
}