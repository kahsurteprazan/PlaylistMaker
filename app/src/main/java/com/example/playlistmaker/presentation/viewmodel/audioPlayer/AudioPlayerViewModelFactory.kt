package com.example.playlistmaker.presentation.viewmodel.audioPlayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.use_case.player.PauseAudioUseCase
import com.example.playlistmaker.domain.use_case.player.PlayAudioInteract
import com.example.playlistmaker.domain.use_case.player.StartAudioUseCase
import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerViewModelFactory(
    private val playAudioUseCase: PlayAudioInteract,
    private val startAudioUseCase: StartAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val audioPlayer: AudioPlayerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
            return AudioPlayerViewModel(
                playAudioUseCase,
                startAudioUseCase,
                pauseAudioUseCase,
                audioPlayer
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}