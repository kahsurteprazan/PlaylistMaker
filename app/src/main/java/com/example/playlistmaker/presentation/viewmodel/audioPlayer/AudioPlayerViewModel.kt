package com.example.playlistmaker.presentation.viewmodel.audioPlayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.use_case.player.PauseAudioUseCase
import com.example.playlistmaker.domain.use_case.player.PlayAudioInteract
import com.example.playlistmaker.domain.use_case.player.StartAudioUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val playAudioUseCase: PlayAudioInteract,
    private val startAudioUseCase: StartAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val audioPlayer: AudioPlayerRepository
) : ViewModel() {

    private var timerJob: Job? = null

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> = _playerState

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun playbackControl() {
        when (val currentState = _playerState.value) {
            is PlayerState.Playing -> pause()
            is PlayerState.Prepared, is PlayerState.Paused -> play()
            else -> {}
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
                _playerState.value = PlayerState.Prepared()
            },
            onCompletion = {
                _playerState.value = PlayerState.Prepared()
                timerJob?.cancel()
            }
        )
    }

    private fun play() {
        startAudioUseCase.execute()
        _playerState.value = PlayerState.Playing(getCurrentProgress())
        startTimer()
    }

    fun pause() {
        pauseAudioUseCase.execute()
        timerJob?.cancel()
        _playerState.value = PlayerState.Paused(getCurrentProgress())
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(300L)
                if (_playerState.value is PlayerState.Playing) {
                    _playerState.value = PlayerState.Playing(getCurrentProgress())
                } else {
                    break
                }
            }
        }
    }

    private fun getCurrentProgress(): String {
        val currentPosition = audioPlayer.getCurrentPosition()
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        audioPlayer.release()
    }
}
