package com.example.playlistmaker.presentation.viewmodel.infoPlaylist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.use_case.playlist.PlaylistInteract
import com.example.playlistmaker.domain.use_case.playlist.SharePlaylistUseCase
import kotlinx.coroutines.launch

class InfoPlaylistViewModel(
    private val playlistInteract: PlaylistInteract,
    private val sharePlaylistUseCase: SharePlaylistUseCase
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _trackCount = MutableLiveData<Int>()
    val trackCount: LiveData<Int> = _trackCount

    private val _totalDuration = MutableLiveData<String>()
    val totalDuration: LiveData<String> = _totalDuration


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    private val _deleteStatus = MutableLiveData<Boolean?>()
    val deleteStatus: LiveData<Boolean?> = _deleteStatus

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>, context: Context) {
        sharePlaylistUseCase(playlist, tracks, context)
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            _deleteStatus.value = playlistInteract.deletePlaylist(playlistId)
        }
    }

    fun clearDeleteStatus() {
        _deleteStatus.value = null
    }

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            try {
                val loadedPlaylist = playlistInteract.getPlaylistById(playlistId)
                    ?: throw IllegalArgumentException("Плейлист не найден")
                _playlist.value = loadedPlaylist

                val loadedTracks = playlistInteract.getTracksForPlaylist(playlistId)
                _tracks.value = loadedTracks
                _trackCount.value = loadedTracks.size
                _totalDuration.value = playlistInteract.getFormattedDuration(playlistId)

            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки: ${e.message}"
            }
        }
    }
    fun deleteTrack(playlistId: Long, trackId: Int) {
        viewModelScope.launch {
            try {
                playlistInteract.deleteTrackFromPlaylist(playlistId, trackId)
                loadPlaylist(playlistId)
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }
}
