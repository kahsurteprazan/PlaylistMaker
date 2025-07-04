package com.example.playlistmaker.domain.model

sealed interface Result<out T> {
    data class Success<out T>(val data: T) : Result<T>
    data class Failure(val exception: Throwable) : Result<Nothing>
}