package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.library.playlist.domain.model.Playlist

sealed class BottomSheetPlaylistState {

    object Empty: BottomSheetPlaylistState()

    data class Content(val playlists: List<Playlist>): BottomSheetPlaylistState()

    data class ShowToast(val message: String) : BottomSheetPlaylistState()

    object CloseBottomSheet : BottomSheetPlaylistState()
}