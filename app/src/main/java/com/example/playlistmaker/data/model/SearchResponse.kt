package com.example.playlistmaker.data.model

import com.example.playlistmaker.domain.model.Track

data class SearchResponse(val resultCount: Int, val results: List<Track>)
