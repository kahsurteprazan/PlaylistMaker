package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackHistoryRepository

class SaveHistoryUseCase(private val trackHistoryRepository: TrackHistoryRepository) {

    operator fun invoke(history: List<Track>) {
        trackHistoryRepository.saveHistory(history)
    }
}