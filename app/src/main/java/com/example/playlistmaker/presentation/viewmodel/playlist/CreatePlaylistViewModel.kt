package com.example.playlistmaker.presentation.viewmodel.playlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.use_case.playlist.PlaylistInteract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class CreatePlaylistViewModel(
    private val interact: PlaylistInteract,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _playlistForEditing = MutableLiveData<Playlist?>()
    val playlistForEditing: LiveData<Playlist?> = _playlistForEditing

    private val _playlistCreated = MutableLiveData<Long>()
    val playlistCreated: LiveData<Long> = _playlistCreated

    private val _showExitDialog = MutableLiveData<Boolean>()
    val showExitDialog: LiveData<Boolean> = _showExitDialog

    private val _coverImagePath = MutableLiveData<String?>()
    val coverImagePath: LiveData<String?> = _coverImagePath

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    var playlistName: String
        get() = state["playlistName"] ?: ""
        set(value) { state["playlistName"] = value }

    var playlistDescription: String?
        get() = state["playlistDescription"]
        set(value) { state["playlistDescription"] = value }

    init {
        _coverImagePath.value = state["coverImagePath"]
    }
    fun updateCoverImagePath(path: String?) {
        _coverImagePath.value = path
    }

    fun loadPlaylistForEditing(playlistId: Long) {
        viewModelScope.launch {
            _playlistForEditing.value = interact.getPlaylistById(playlistId)
        }
    }

    fun updatePlaylist(playlistId: Long, name: String, description: String?, coverImagePath: String?) {
        viewModelScope.launch {
            try {
                val currentPlaylist = interact.getPlaylistById(playlistId) ?: run {
                    _errorMessage.postValue("Плейлист не найден")
                    return@launch
                }

                val updatedPlaylist = currentPlaylist.copy(
                    name = name,
                    description = description,
                    coverImagePath = coverImagePath
                )

                interact.updatePlaylist(updatedPlaylist)
                _playlistCreated.postValue(playlistId)
            } catch (e: Exception) {
                _errorMessage.postValue("Ошибка при обновлении плейлиста: ${e.message}")
            }
        }
    }

    fun hasUnsavedChanges(): Boolean {
        return playlistName.isNotBlank() ||
                !playlistDescription.isNullOrBlank() ||
                !coverImagePath.value.isNullOrBlank()
    }

    fun onBackPressed() {
        if (hasUnsavedChanges()) {
            _showExitDialog.value = true
        }
    }

    fun resetExitDialog() {
        _showExitDialog.value = false
    }

    fun createPlaylist() {
        if (playlistName.isBlank()) {
            _errorMessage.value = "Введите название плейлиста"
            return
        }

        viewModelScope.launch {
            try {
                val id = interact.createPlaylist(
                    name = playlistName,
                    description = playlistDescription,
                    coverImageUri = _coverImagePath.value
                )
                _playlistCreated.postValue(id)
                clearFields()
            } catch (e: Exception) {
                _errorMessage.postValue("Ошибка при создании плейлиста: ${e.message}")
            }
        }
    }

    suspend fun saveCoverImage(context: Context, uri: Uri) {
        try {
            val imagePath = saveImageToPrivateStorage(context, uri)
            _coverImagePath.postValue(imagePath)
            state["coverImagePath"] = imagePath
        } catch (e: Exception) {
            _errorMessage.postValue("Ошибка сохранения изображения")
        }
    }

    private suspend fun saveImageToPrivateStorage(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val storageDir = File(context.filesDir, "playlist_covers").apply {
                    if (!exists()) mkdirs()
                }
                val outputFile = File(storageDir, "cover_${System.currentTimeMillis()}.jpg")

                context.contentResolver.openInputStream(uri)?.use { input ->
                    outputFile.outputStream().use { output ->
                        BitmapFactory.decodeStream(input)?.let { bitmap ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                            bitmap.recycle()
                        }
                    }
                }
                outputFile.takeIf { it.exists() && it.length() > 0 }?.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }

    fun errorShown() {
        _errorMessage.value = null
    }

    private fun clearFields() {
        playlistName = ""
        playlistDescription = null
        _coverImagePath.value = null
        state["coverImagePath"] = null
    }
}