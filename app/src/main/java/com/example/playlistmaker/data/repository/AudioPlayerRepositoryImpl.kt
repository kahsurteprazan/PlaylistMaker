package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : AudioPlayerRepository {

    override fun setDataSource(url: String) {
        try {
            mediaPlayer.release()
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(url)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error setting data source", e)
            throw e
        }
    }

    override fun prepareAsync() {
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        mediaPlayer.setOnPreparedListener { listener() }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }
}
