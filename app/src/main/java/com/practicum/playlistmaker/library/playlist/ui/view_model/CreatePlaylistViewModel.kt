package com.practicum.playlistmaker.library.playlist.ui.view_model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.library.playlist.domain.model.Playlist
import com.practicum.playlistmaker.library.playlist.ui.CreatePlaylistState
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val application: Application,
) : ViewModel() {

    private val _createPlaylistState =
        MutableLiveData<CreatePlaylistState>(CreatePlaylistState.Default)
    val createPlaylistState: LiveData<CreatePlaylistState> get() = _createPlaylistState

    fun createPlaylist(
        name: String,
        description: String?,
        imageUri: Uri?,
    ) {
        viewModelScope.launch {
            _createPlaylistState.postValue(CreatePlaylistState.Loading)
            try {
                val imagePath = imageUri?.let { saveImageToPrivateStorage(it) } ?: ""
                val playlist = Playlist(
                    name = name,
                    description = description,
                    coverImagePath = imagePath,
                    trackIds = emptyList(),
                    trackCount = 0
                )
                playlistInteractor.createPlaylist(playlist)
                _createPlaylistState.postValue(CreatePlaylistState.Success)
            } catch (e: Exception) {
                _createPlaylistState.postValue(
                    CreatePlaylistState.Error(
                        e.localizedMessage ?: "Неизвестная ошибка"
                    )
                )
            }
        }
    }

    fun loadPlaylistForEditing(playlistId: Int) {
        viewModelScope.launch {
            try {
                val playlist = playlistInteractor.getPlaylistById(playlistId)
                if (playlist != null) {
                    _createPlaylistState.postValue(CreatePlaylistState.PlaylistLoaded(playlist))
                }
            } catch (e: Exception) {
                _createPlaylistState.postValue(
                    CreatePlaylistState.Error(
                        e.localizedMessage ?: "Не удалось загрузить плейлист"
                    )
                )
            }
        }
    }

    fun updatePlaylist(
        playlistId: Int,
        name: String,
        description: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _createPlaylistState.postValue(CreatePlaylistState.Loading)
            try {
                val currentPlaylist = playlistInteractor.getPlaylistById(playlistId)
                    ?: throw IllegalStateException("Плейлист не найден")

                val imagePath = when {
                    imageUri != null && imageUri.scheme == "content" -> {
                        saveImageToPrivateStorage(imageUri)
                    }
                    imageUri != null && imageUri.scheme == "file" -> {
                        File(imageUri.path!!).absolutePath
                    }
                    else -> {
                        currentPlaylist.coverImagePath // если обложку не меняли
                    }
                }

                val updatedPlaylist = currentPlaylist.copy(
                    name = name,
                    description = description,
                    coverImagePath = imagePath
                )

                playlistInteractor.updatePlaylist(updatedPlaylist)
                _createPlaylistState.postValue(CreatePlaylistState.Success)
            } catch (e: Exception) {
                _createPlaylistState.postValue(
                    CreatePlaylistState.Error(
                        e.localizedMessage ?: "Не удалось обновить плейлист"
                    )
                )
            }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath =
            File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "playlist_cover_${System.currentTimeMillis()}.jpg")
        application.contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }
        }
        return file.absolutePath
    }

    fun resetState() {
        _createPlaylistState.postValue(CreatePlaylistState.Default)
    }
}