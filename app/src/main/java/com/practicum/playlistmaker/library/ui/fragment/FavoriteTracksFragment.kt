package com.practicum.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.utils.BindingFragment

class FavoriteTracksFragment : BindingFragment<FragmentFavoriteTracksBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.phEmptyFavoriteTracksErrorMessage.setText(R.string.library_empty_favorite_tracks_placeholder)
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}