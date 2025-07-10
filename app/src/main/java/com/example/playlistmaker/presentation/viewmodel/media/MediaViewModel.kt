package com.example.playlistmaker.presentation.viewmodel.media

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.use_case.media.LikedTracksInteract
import com.example.playlistmaker.domain.use_case.playlist.PlaylistInteract
import com.example.playlistmaker.presentation.mappers.PlaylistMapper.toUi
import com.example.playlistmaker.presentation.model.PlaylistUi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MediaViewModel(
    private val likedTracksInteract: LikedTracksInteract,
    private val playlistInteract: PlaylistInteract
) : ViewModel() {


    sealed class State {
        object Initial : State()
        object Empty : State()
        data class Content(val tracks: List<Track>) : State()
        data class Error(val message: String) : State()
    }

    private val _playlists = MutableLiveData<List<PlaylistUi>>()
    val playlists: LiveData<List<PlaylistUi>> = _playlists

    private val _state = MutableLiveData<State>().apply { value = State.Initial }
    val state: LiveData<State> = _state

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteract.getAllPlaylists()
                .collect { playlists ->
                    _playlists.value = playlists.map { it.toUi() }
                }
        }
    }

    fun loadLikedTracks() {
        viewModelScope.launch {
            _state.value = State.Initial
            Log.d("MediaDebug", "ViewModel запустила загрузку")
            likedTracksInteract.getLikedTracks()
                .catch { e ->
                    Log.e("MediaDebug", "Ошибка: ${e.message}")
                    _state.value = State.Error(e.message ?: "Error loading tracks")
                }
                .collect { tracks ->
                    Log.d("MediaDebug", "Треков загружено: ${tracks.size}")
                    _state.value = if (tracks.isEmpty()) State.Empty else State.Content(tracks)
                }
        }
    }
}