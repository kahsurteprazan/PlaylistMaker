package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl : SearchRepository {
    override suspend fun search(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = RetrofitClient.api.search(query)
            if (response.isSuccessful) {
                val searchResponse = response.body()
                if (searchResponse != null && searchResponse.results.isNotEmpty()) {
                    emit(Result.success(searchResponse.results))
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