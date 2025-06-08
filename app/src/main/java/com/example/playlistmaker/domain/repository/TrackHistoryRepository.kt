package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TrackHistoryRepository {
   suspend fun addTrack(track: Track)
   suspend fun getHistory() : List<Track>
   suspend fun clearHistory()
   suspend fun saveHistory(history: List<Track>)
}