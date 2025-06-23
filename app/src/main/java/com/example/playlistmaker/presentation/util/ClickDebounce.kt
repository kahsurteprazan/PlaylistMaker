package com.example.playlistmaker.presentation.util

import kotlinx.coroutines.delay

class ClickDebounce(private val delay: Long) {
    private var isClickAllowed = true

    suspend fun clickDebounce(): Boolean {
        return if (isClickAllowed) {
            isClickAllowed = false
            delay(delay)
            isClickAllowed = true
            true
        } else {
            false
        }
    }
}