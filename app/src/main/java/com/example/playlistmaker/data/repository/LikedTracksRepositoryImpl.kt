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
        Log.d("DB_DEBUG", "Adding track ${track.trackId} to liked")
        val entity = converter.mapToEntity(track)
        Log.d("DB_DEBUG", "Entity: $entity")
        appDatabase.trackDao().addTrack(entity)
        Log.d("DB_DEBUG", "Track added successfully")
    }

    override suspend fun removeFromLiked(track: Track) {
        Log.d("DB_DEBUG", "Removing track ${track.trackId} from liked")
        appDatabase.trackDao().deleteTrack(converter.mapToEntity(track))
        Log.d("DB_DEBUG", "Track removed successfully")
    }

    override fun getLikedTracks(): Flow<List<Track>> {
        Log.d("DB_DEBUG", "Fetching liked tracks flow")
        return appDatabase.trackDao().getTracks()
            .map { entities ->
                Log.d("DB_DEBUG", "Raw entities count: ${entities.size}")
                val tracks = entities.map { converter.mapFromEntity(it) }
                Log.d("DB_DEBUG", "Converted tracks count: ${tracks.size}")
                tracks.sortedByDescending { it.addedDate }
            }
    }

    override suspend fun isTrackLiked(trackId: Int): Boolean {
        Log.d("DB_DEBUG", "Checking if track $trackId is liked")
        val likedIds = appDatabase.trackDao().getLikedTrackIds().first()
        val isLiked = likedIds.contains(trackId)
        Log.d("DB_DEBUG", "Track $trackId is liked: $isLiked")
        return isLiked
    }
}