package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface SearchRepository {
    fun search(query: String, callback: (Result<List<Track>>) -> Unit)
}