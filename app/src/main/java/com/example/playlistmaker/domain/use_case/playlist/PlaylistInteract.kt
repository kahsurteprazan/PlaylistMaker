package com.example.playlistmaker.domain.use_case.playlist

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.result.AddToPlaylistResult
import kotlinx.coroutines.flow.Flow

interface PlaylistInteract {

    suspend fun createPlaylist(name: String, description: String?, coverImageUri: String?): Long

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): AddToPlaylistResult

    suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track)

    suspend fun deletePlaylist(id: Int): Long
}