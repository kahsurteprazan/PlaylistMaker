package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackHistoryRepository {
   fun addTrack(track: Track): Flow<Unit>
   fun getHistory(): Flow<List<Track>>
   fun clearHistory(): Flow<Unit>
   fun saveHistory(history: List<Track>): Flow<Unit>
}