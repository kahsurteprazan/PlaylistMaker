package com.example.playlistmaker.data.repository

import android.util.Log
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.PlaylistDbConverter
import com.example.playlistmaker.data.db.PlaylistTrackDbConverter
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
    private val converterForTrack: PlaylistTrackDbConverter
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
            Log.d("DEBUG", "Inserted track ID ${trackEntity.trackId} to playlist_tracks")
        } catch (e: Exception) {
            return AddToPlaylistResult.Error("Ошибка добавления трека: ${e.message}")
        }

        val updatedPlaylist = playlistEntity.addTrackId(track.trackId)
        appDatabase.playlistDao().update(updatedPlaylist)

        return AddToPlaylistResult.Success
    }


    override suspend fun getPlaylistById(id: Long): Playlist? {
        return appDatabase.playlistDao().getPlaylistById(id)?.let { converter.mapFromEntity(it) }
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
            ?: throw IllegalArgumentException("Playlist not found")

        val trackIds = playlistEntity.getTrackIds()
        Log.d("REPO_DEBUG", "Track IDs: $trackIds")

        val allTracks = appDatabase.playlistTrackDao().getAllTracks()
        Log.d("REPO_DEBUG", "All tracks in DB: ${allTracks.map { it.trackId }}")

        val playlistTracks = allTracks.filter { trackIds.contains(it.trackId) }
            .map { converterForTrack.mapFromEntity(it) }

        Log.d("REPO_DEBUG", "Loaded ${playlistTracks.size} tracks for playlist")
        return playlistTracks
    }

    override suspend fun getTotalDuration(playlistId: Long): Long {
        val tracks = getTracksForPlaylist(playlistId)
        return tracks.sumOf { it.trackTimeMillis }
    }

    override suspend fun deletePlaylist(id: Long): Boolean {
        return try {
            // 1. Получаем плейлист перед удалением
            val playlist = appDatabase.playlistDao().getPlaylistById(id)
                ?: return false

            // 2. Получаем ID треков этого плейлиста
            val trackIds = playlist.getTrackIds()

            // 3. Удаляем сам плейлист
            appDatabase.playlistDao().deletePlaylist(id)

            // 4. Удаляем неиспользуемые треки
            deleteOrphanedTracks(trackIds)

            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun deleteOrphanedTracks(trackIds: List<Int>) {
        val orphanedTracks = trackIds.filter { trackId ->
            !appDatabase.playlistDao().containsTrackInAnyPlaylist(trackId)
        }

        orphanedTracks.forEach { trackId ->
            appDatabase.playlistTrackDao().deleteById(trackId)
        }

        if (orphanedTracks.isNotEmpty()) {
            Log.d("DEBUG", "Deleted ${orphanedTracks.size} orphaned tracks")
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = converter.mapToEntity(playlist)
        appDatabase.playlistDao().update(entity)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Int) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
            ?: throw IllegalArgumentException("Плейлист не найден")

        val updatedEntity = playlistEntity.removeTrackId(trackId)
        appDatabase.playlistDao().update(updatedEntity)

        if (!isTrackUsedInOtherPlaylists(trackId)) {
            appDatabase.playlistTrackDao().deleteById(trackId)
        }
    }

    private suspend fun isTrackUsedInOtherPlaylists(trackId: Int): Boolean {
        return appDatabase.playlistDao().containsTrackInAnyPlaylist(trackId)
    }

}