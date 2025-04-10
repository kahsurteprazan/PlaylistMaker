package com.example.playlistmaker.presentation.model

import android.os.Parcelable
import com.example.playlistmaker.domain.model.Track
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackUi(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?
) : Parcelable {
    constructor(domainModel: Track) : this(
        trackName = domainModel.trackName,
        artistName = domainModel.artistName,
        trackTimeMillis = domainModel.trackTimeMillis,
        artworkUrl100 = domainModel.artworkUrl100,
        trackId= domainModel.trackId,
        collectionName= domainModel.collectionName,
        releaseDate= domainModel.releaseDate,
        primaryGenreName= domainModel.primaryGenreName,
        country= domainModel.country,
        previewUrl = domainModel.previewUrl
    )
}