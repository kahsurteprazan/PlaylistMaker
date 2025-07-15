package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.example.playlistmaker.domain.result.AddToPlaylistResult
import com.example.playlistmaker.domain.use_case.playlist.PlaylistInteract
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

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

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Int) {
        playlistRepository.deleteTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun deletePlaylist(id: Long): Boolean {
        return playlistRepository.deletePlaylist(id)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        return playlistRepository.getTracksForPlaylist(playlistId)
    }

    override suspend fun getTotalDuration(playlistId: Long): Long {
        val tracks = playlistRepository.getTracksForPlaylist(playlistId)
        return tracks.sumOf { it.trackTimeMillis }
    }

    override suspend fun getFormattedDuration(playlistId: Long): String {
        val tracks = getTracksForPlaylist(playlistId)
        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        return formatAsMinutes(totalMillis)
    }

    private fun formatAsMinutes(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        return "$minutes мин"
    }

}