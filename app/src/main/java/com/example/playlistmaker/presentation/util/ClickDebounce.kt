package com.example.playlistmaker.presentation.util

import android.os.Handler
import android.os.Looper

class ClickDebounce(private val delay: Long) {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, delay)
        }
        return current
    }
}