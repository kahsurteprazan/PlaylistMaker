package com.example.playlistmaker.domain.repository

import android.content.Context
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track

interface ShareAppRepository {
    fun shareApp()
    fun sharePlaylist(playlist: Playlist, tracks: List<Track>, context: Context)
}