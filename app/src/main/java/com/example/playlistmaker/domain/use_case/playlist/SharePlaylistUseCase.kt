package com.example.playlistmaker.domain.use_case.playlist

import android.content.Context
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.ShareAppRepository

class SharePlaylistUseCase(
    private val shareRepository: ShareAppRepository
) {
    operator fun invoke(playlist: Playlist, tracks: List<Track>, context: Context) {
        shareRepository.sharePlaylist(playlist, tracks, context)
    }
}