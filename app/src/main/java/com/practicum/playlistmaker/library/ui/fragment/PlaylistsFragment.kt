package com.practicum.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.utils.BindingFragment

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.phEmptyFavoriteTracksErrorMessage.setText(R.string.library_empty_playlists_placeholder)
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}