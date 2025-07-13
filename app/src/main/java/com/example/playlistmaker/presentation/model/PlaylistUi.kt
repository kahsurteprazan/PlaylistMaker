package com.example.playlistmaker.presentation.model


data class PlaylistUi(
    val id: Long,
    val name: String,
    val imgPath: String?,
    val trackCount: Int,
    val description: String?
)