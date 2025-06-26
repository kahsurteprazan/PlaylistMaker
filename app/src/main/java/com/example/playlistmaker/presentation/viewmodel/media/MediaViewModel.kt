package com.example.playlistmaker.presentation.viewmodel.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.use_case.media.LikedTracksInteract
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MediaViewModel(
    private val likedTracksInteract: LikedTracksInteract
) : ViewModel() {

    sealed class State {
        object Initial : State()
        object Empty : State()
        data class Content(val tracks: List<Track>) : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>().apply { value = State.Initial }
    val state: LiveData<State> = _state

    init {
        viewModelScope.launch {
            likedTracksInteract.getLikedTracks()
                .catch { e ->
                    _state.value = State.Error(e.message ?: "Unknown error")
                }
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _state.value = State.Empty
                    } else {
                        _state.value = State.Content(tracks)
                    }
                }
        }
    }
}