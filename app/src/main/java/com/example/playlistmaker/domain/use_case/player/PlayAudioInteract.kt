package com.example.playlistmaker.domain.use_case.player

import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class PlayAudioInteract(private val audioPlayerRepository: AudioPlayerRepository) {
    fun execute(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        audioPlayerRepository.setDataSource(url)
        audioPlayerRepository.setOnPreparedListener { onPrepared() }
        audioPlayerRepository.setOnCompletionListener { onCompletion() }
        audioPlayerRepository.prepareAsync()
    }
}