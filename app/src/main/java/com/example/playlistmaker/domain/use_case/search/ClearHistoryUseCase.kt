package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.repository.TrackHistoryRepository

class ClearHistoryUseCase(private val trackHistoryRepository: TrackHistoryRepository) {

    operator fun invoke() {
        trackHistoryRepository.clearHistory()
    }
}