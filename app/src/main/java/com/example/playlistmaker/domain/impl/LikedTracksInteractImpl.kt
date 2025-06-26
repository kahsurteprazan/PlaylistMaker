package com.example.playlistmaker.domain.impl

import android.util.Log
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.LikedTracksRepository
import com.example.playlistmaker.domain.use_case.media.LikedTracksInteract
import kotlinx.coroutines.flow.Flow

class LikedTracksInteractImpl(
    private val likedTracksRepository: LikedTracksRepository
) : LikedTracksInteract {

    override suspend fun likeTrack(track: Track) {
        Log.d("LikedTracks", "Adding track ${track.trackName} to liked")
        likedTracksRepository.addToLiked(track)
        Log.d("LikedTracks", "Liked track ID: ${track.trackId}")
    }

    override suspend fun unlikeTrack(track: Track) {
        Log.d("unLikedTracks", "unAdding track")
        likedTracksRepository.removeFromLiked(track)
        Log.d("LikedTracks", "Unliked track ID: ${track.trackId}")
    }

    override fun getLikedTracks(): Flow<List<Track>> {
        return likedTracksRepository.getLikedTracks()
    }

    override suspend fun isTrackLiked(trackId: Int): Boolean {
        return likedTracksRepository.isTrackLiked(trackId)
    }
}