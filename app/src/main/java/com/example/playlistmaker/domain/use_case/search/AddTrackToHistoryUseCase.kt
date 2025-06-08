package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackHistoryRepository

class AddTrackToHistoryUseCase(private val trackHistoryRepository: TrackHistoryRepository) {

   suspend operator fun invoke(track: Track) {
        trackHistoryRepository.addTrack(track)
    }
}