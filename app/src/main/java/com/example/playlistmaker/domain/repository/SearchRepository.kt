package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface SearchRepository {
    suspend fun search(query: String): Result<List<Track>>
}