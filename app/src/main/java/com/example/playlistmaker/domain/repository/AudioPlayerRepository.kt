package com.example.playlistmaker.domain.repository

interface AudioPlayerRepository {
    fun setDataSource(url: String)
    fun prepareAsync()
    fun start()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
}