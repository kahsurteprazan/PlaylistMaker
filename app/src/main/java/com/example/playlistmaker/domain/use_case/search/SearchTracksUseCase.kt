package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(query: String): Flow<Result<List<Track>>> = repository.search(query)
}