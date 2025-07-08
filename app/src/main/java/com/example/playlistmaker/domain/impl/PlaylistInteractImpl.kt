package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.example.playlistmaker.domain.result.AddToPlaylistResult
import com.example.playlistmaker.domain.use_case.playlist.PlaylistInteract
import kotlinx.coroutines.flow.Flow

class PlaylistInteractImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteract {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImageUri: String?
    ): Long {
        val playlist = Playlist(
            name = name,
            description = description,
            coverImagePath = coverImageUri
        )
        return playlistRepository.createPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
      return  playlistRepository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): AddToPlaylistResult {
        return playlistRepository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        playlistRepository.deleteTrackFromPlaylist(playlist, track)
    }

    override suspend fun deletePlaylist(id: Int): Long {
        return playlistRepository.deletePlaylist(id)
    }

}