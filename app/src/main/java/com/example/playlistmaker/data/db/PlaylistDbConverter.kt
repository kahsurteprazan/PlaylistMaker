package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

class PlaylistDbConverter {

    fun mapToEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIdsJson = Gson().toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
    }

    fun mapFromEntity(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverImagePath = entity.coverImagePath,
            trackIds = entity.getTrackIds()
        )
    }
}