package com.example.playlistmaker.data.repository

import android.util.Log
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConverter
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.LikedTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LikedTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: TrackDbConverter
) : LikedTracksRepository {

    override suspend fun addToLiked(track: Track) {
        val entity = converter.mapToEntity(track)
        appDatabase.trackDao().addTrack(entity)
    }

    override suspend fun removeFromLiked(track: Track) {
        appDatabase.trackDao().deleteTrack(converter.mapToEntity(track))
    }

    override fun getLikedTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks()
            .map { entities ->
                val tracks = entities.map { converter.mapFromEntity(it) }
                tracks.sortedByDescending { it.addedDate }
            }
    }

    override suspend fun isTrackLiked(trackId: Int): Boolean {
        val likedIds = appDatabase.trackDao().getLikedTrackIds().first()
        val isLiked = likedIds.contains(trackId)
        return isLiked
    }
}