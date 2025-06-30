package com.example.playlistmaker.domain.use_case.media

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface LikedTracksInteract {
    suspend fun likeTrack(track: Track)
    suspend fun unlikeTrack(track: Track)
    fun getLikedTracks(): Flow<List<Track>>
    suspend fun isTrackLiked(trackId: Int): Boolean
}