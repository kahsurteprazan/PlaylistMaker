package com.example.playlistmaker.domain.use_case.search

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import kotlinx.coroutines.flow.Flow

class AddTrackToHistoryUseCase(private val repository: TrackHistoryRepository) {
    operator fun invoke(track: Track): Flow<Unit> = repository.addTrack(track)
}