package com.example.playlistmaker.presentation.viewmodel.audioPlayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.use_case.player.PauseAudioUseCase
import com.example.playlistmaker.domain.use_case.player.PlayAudioInteract
import com.example.playlistmaker.domain.use_case.player.StartAudioUseCase
import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerViewModel(
    private val playAudioUseCase: PlayAudioInteract,
    private val startAudioUseCase: StartAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val audioPlayer: AudioPlayerRepository
) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    private val _playerState = MutableLiveData(STATE_DEFAULT)
    val playerState: LiveData<Int> = _playerState

    private val _playButtonResId = MutableLiveData<Int>()
    val playButtonResId: LiveData<Int> = _playButtonResId

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun playbackControl() {
        when (_playerState.value) {
            STATE_PLAYING -> pause()
            STATE_PREPARED, STATE_PAUSED -> play()
        }
    }

    fun preparePlayer(url: String) {
        if (url.isEmpty()) {
            _errorMessage.value = "Ошибка: аудиофайл недоступен"
            return
        }

        playAudioUseCase.execute(
            url,
            onPrepared = {
                _playerState.value = STATE_PREPARED
                updatePlayButton()
            },
            onCompletion = {
                _playerState.value = STATE_PAUSED
                updatePlayButton()
            }
        )
    }

    fun getCurrentPosition(): Int = audioPlayer.getCurrentPosition()

    fun pause() {
        pauseAudioUseCase.execute()
        _playerState.value = STATE_PAUSED
        updatePlayButton()
    }

    private fun play() {
        startAudioUseCase.execute()
        _playerState.value = STATE_PLAYING
        updatePlayButton()
    }

    private fun updatePlayButton() {
        _playButtonResId.value = when (_playerState.value) {
            STATE_PLAYING -> R.drawable.ic_pause
            else -> R.drawable.ic_play
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}

