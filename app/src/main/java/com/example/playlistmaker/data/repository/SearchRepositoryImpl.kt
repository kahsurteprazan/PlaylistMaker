package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchRepositoryImpl : SearchRepository {
    override suspend fun search(query: String): Result<List<Track>> {
        return try {
            val response = RetrofitClient.api.search(query)
            if (response.isSuccessful) {
                val searchResponse = response.body()
                if (searchResponse != null && searchResponse.results.isNotEmpty()) {
                    Result.success(searchResponse.results)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Failed to fetch data"))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}