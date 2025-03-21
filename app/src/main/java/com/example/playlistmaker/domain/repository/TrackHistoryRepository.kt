package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TrackHistoryRepository {
    fun addTrack(track: Track)
    fun getHistory() : List<Track>
    fun clearHistory()
    fun saveHistory(history: List<Track>)
}