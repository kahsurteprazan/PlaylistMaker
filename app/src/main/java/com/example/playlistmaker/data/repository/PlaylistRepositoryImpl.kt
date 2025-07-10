package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.PlaylistDbConverter
import com.example.playlistmaker.data.db.TrackDbConverter
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.example.playlistmaker.domain.result.AddToPlaylistResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: PlaylistDbConverter,
    private val converterForTrack: TrackDbConverter
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val entity = PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIdsJson = "[]",
            trackCount = 0
        )
        return appDatabase.playlistDao().insert(entity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao()
            .getAllPlaylists()
            .map { entities -> entities.map { converter.mapFromEntity(it) } }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): AddToPlaylistResult {

        val trackEntity = converterForTrack.mapToEntity(track)

        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
            ?: return AddToPlaylistResult.Error("Плейлист не найден")

        if (playlistEntity.getTrackIds().contains(track.trackId)) {
            return AddToPlaylistResult.AlreadyExists
        }

        try {
            appDatabase.playlistTrackDao().insert(trackEntity)
        } catch (e: Exception) {
            return AddToPlaylistResult.Error("Ошибка добавления трека: ${e.message}")
        }

        val updatedPlaylist = playlistEntity.addTrackId(track.trackId)
        appDatabase.playlistDao().update(updatedPlaylist)

        return AddToPlaylistResult.Success
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(id: Int): Long {
        TODO("Not yet implemented")
    }

}