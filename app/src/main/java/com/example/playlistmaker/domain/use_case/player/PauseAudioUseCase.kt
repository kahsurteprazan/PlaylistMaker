package com.example.playlistmaker.domain.use_case.player

import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class PauseAudioUseCase(private val audioPlayer: AudioPlayerRepository) {
    fun execute() {
        audioPlayer.pause()
    }
}