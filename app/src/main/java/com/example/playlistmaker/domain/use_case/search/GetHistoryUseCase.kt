package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryUseCase(private val repository: TrackHistoryRepository) {
    operator fun invoke(): Flow<List<Track>> = repository.getHistory()
}