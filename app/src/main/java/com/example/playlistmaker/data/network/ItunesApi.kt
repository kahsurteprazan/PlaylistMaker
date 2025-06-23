package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): Response<SearchResponse>
}