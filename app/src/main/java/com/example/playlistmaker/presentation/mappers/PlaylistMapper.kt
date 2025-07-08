package com.example.playlistmaker.presentation.mappers

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.presentation.model.PlaylistUi

object PlaylistMapper {
    fun Playlist.toUi(): PlaylistUi {
        return PlaylistUi(
            id = this.id,
            name = this.name,
            imgPath = this.coverImagePath,
            trackCount = this.trackCount,
            description = this.description
        )
    }
}