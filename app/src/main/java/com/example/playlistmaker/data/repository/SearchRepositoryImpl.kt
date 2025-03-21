package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.model.SearchResponse
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchRepositoryImpl : SearchRepository {

    override fun search(query: String, callback: (Result<List<Track>>) -> Unit) {
        RetrofitClient.api.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null && searchResponse.results.isNotEmpty()) {
                        callback(Result.success(searchResponse.results))
                    } else {
                        callback(Result.success(emptyList()))
                    }
                } else {
                    callback(Result.failure(Exception("Failed to fetch data")))
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}