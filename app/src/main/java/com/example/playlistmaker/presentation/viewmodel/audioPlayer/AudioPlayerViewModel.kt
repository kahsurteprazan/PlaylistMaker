package com.example.playlistmaker.presentation.viewmodel.audioPlayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.result.AddToPlaylistResult
import com.example.playlistmaker.domain.use_case.media.LikedTracksInteract
import com.example.playlistmaker.domain.use_case.player.PauseAudioUseCase
import com.example.playlistmaker.domain.use_case.player.PlayAudioInteract
import com.example.playlistmaker.domain.use_case.player.StartAudioUseCase
import com.example.playlistmaker.domain.use_case.playlist.PlaylistInteract
import com.example.playlistmaker.presentation.mappers.PlaylistMapper.toUi
import com.example.playlistmaker.presentation.model.PlaylistUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val likedTracksInteract: LikedTracksInteract,
    private val playAudioUseCase: PlayAudioInteract,
    private val startAudioUseCase: StartAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val audioPlayer: AudioPlayerRepository,
    private val playlistInteract: PlaylistInteract
) : ViewModel() {

    private val _addToPlaylistResult = MutableSharedFlow<AddToPlaylistResult>()
    val addToPlaylistResult: SharedFlow<AddToPlaylistResult> = _addToPlaylistResult

    private val _playlists = MutableLiveData<List<PlaylistUi>>()
    val playlists: LiveData<List<PlaylistUi>> = _playlists

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    private var timerJob: Job? = null

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> = _playerState

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteract.getAllPlaylists()
                .collect { playlists ->
                    _playlists.value = playlists.map { it.toUi() }
                }
        }
    }

    fun addTrackToPlaylist(playlistId: Long) {
        val track = _currentTrack.value ?: run {
            return
        }

        viewModelScope.launch {
            try {
                val result = playlistInteract.addTrackToPlaylist(track, playlistId)
                _addToPlaylistResult.emit(result)
            } catch (e: Exception) {
                _addToPlaylistResult.emit(AddToPlaylistResult.Error("Ошибка: ${e.message}"))
            }
        }
    }

    fun setTrack(track: Track) {
        viewModelScope.launch {
            val isLiked = likedTracksInteract.isTrackLiked(track.trackId)
            _currentTrack.value = track.copy(isFavorite = isLiked)
        }
    }

    fun onFavoriteClicked() {
        val track = _currentTrack.value ?: run {
            return
        }

        viewModelScope.launch {
            try {

                if (track.isFavorite) {

                    likedTracksInteract.unlikeTrack(track)
                } else {

                    likedTracksInteract.likeTrack(track)
                }


                _currentTrack.value = track.copy(isFavorite = !track.isFavorite)

            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message}"
            }
        }
    }

    fun playbackControl() {
        when (_playerState.value) {
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
