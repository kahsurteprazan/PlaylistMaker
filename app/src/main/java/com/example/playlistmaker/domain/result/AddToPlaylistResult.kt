package com.example.playlistmaker.domain.result

sealed class AddToPlaylistResult {
    object Success : AddToPlaylistResult()
    object AlreadyExists : AddToPlaylistResult()
    data class Error(val message: String) : AddToPlaylistResult()
}