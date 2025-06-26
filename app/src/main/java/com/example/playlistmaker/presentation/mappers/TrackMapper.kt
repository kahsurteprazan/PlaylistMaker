package com.example.playlistmaker.presentation.mappers

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.model.TrackUi

object TrackMapper {
    fun mapToUi(domainTrack: Track): TrackUi = TrackUi(domainTrack)

    fun mapToDomain(uiTrack: TrackUi): Track = Track(
        trackName = uiTrack.trackName,
        artistName = uiTrack.artistName,
        trackTimeMillis = uiTrack.trackTimeMillis,
        artworkUrl100 = uiTrack.artworkUrl100,
        trackId= uiTrack.trackId,
        collectionName= uiTrack.collectionName,
        releaseDate= uiTrack.releaseDate,
        primaryGenreName= uiTrack.primaryGenreName,
        country= uiTrack.country,
        previewUrl = uiTrack.previewUrl,
        isFavorite = uiTrack.isFavorite,
        addedDate = uiTrack.addedDate
    )
}