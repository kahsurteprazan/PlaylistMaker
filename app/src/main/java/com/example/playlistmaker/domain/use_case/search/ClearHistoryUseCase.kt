package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import kotlinx.coroutines.flow.Flow

class ClearHistoryUseCase(private val repository: TrackHistoryRepository) {
    operator fun invoke(): Flow<Unit> = repository.clearHistory()
}