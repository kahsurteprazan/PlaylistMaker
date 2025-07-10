package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.result.AddToPlaylistResult
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist): Long

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): AddToPlaylistResult

    suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track)

    suspend fun deletePlaylist(id: Int): Long
}