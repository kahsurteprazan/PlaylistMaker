package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import kotlinx.coroutines.flow.Flow

class SaveHistoryUseCase(private val repository: TrackHistoryRepository) {
    operator fun invoke(history: List<Track>): Flow<Unit> = repository.saveHistory(history)
}