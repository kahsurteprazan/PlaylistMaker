package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface LikedTracksRepository {
    suspend fun addToLiked(track: Track)
    suspend fun removeFromLiked(track: Track)
    fun getLikedTracks(): Flow<List<Track>>
    suspend fun isTrackLiked(trackId: Int): Boolean
}