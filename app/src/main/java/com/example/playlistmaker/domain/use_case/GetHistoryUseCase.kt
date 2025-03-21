package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackHistoryRepository

class GetHistoryUseCase(private val trackHistoryRepository: TrackHistoryRepository) {

    operator fun invoke() : List<Track> {
       return trackHistoryRepository.getHistory()
    }
}