package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SearchRepositoryImpl(
    private val trackDao: TrackDao
) : SearchRepository {

    override suspend fun search(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val likedTrackIds = withContext(Dispatchers.IO) {
                trackDao.getLikedTrackIds().first()
            }
            val response = RetrofitClient.api.search(query)
            if (response.isSuccessful) {
                val searchResponse = response.body()
                if (searchResponse != null && searchResponse.results.isNotEmpty()) {
                    val tracksWithFavorites = searchResponse.results.map { track ->
                        track.copy(isFavorite = track.trackId in likedTrackIds)
                    }
                    emit(Result.success(tracksWithFavorites))
                } else {
                    emit(Result.success(emptyList()))
                }
            } else {
                emit(Result.failure(Exception("Failed to fetch data")))
            }
        } catch (t: Throwable) {
            emit(Result.failure(t))
        }
    }
}