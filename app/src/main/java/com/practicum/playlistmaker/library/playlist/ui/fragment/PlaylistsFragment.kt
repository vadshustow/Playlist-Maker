package com.practicum.playlistmaker.library.playlist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.playlist.ui.PlaylistState
import com.practicum.playlistmaker.library.playlist.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment() : BindingFragment<FragmentPlaylistsBinding>() {

    private lateinit var adapter: PlaylistAdapter

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.createPlaylistFragment)
        }

        binding.rvPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
        viewModel.getPlaylists()

        viewModel.playlistState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Empty -> {
                    showEmpty()
                }
                is PlaylistState.Content -> {
                    adapter = PlaylistAdapter(state.playlists)
                    binding.rvPlaylist.adapter = adapter
                    adapter.notifyDataSetChanged()
                    showContent()
                }
            }
        }
    }

    private fun showContent() {
        binding.rvPlaylist.isVisible = true
        binding.phEmptyFavoriteTracksImage.isVisible = false
        binding.phEmptyFavoriteTracksErrorMessage.isVisible = false
    }

    private fun showEmpty() {
        binding.rvPlaylist.isVisible = false
        binding.phEmptyFavoriteTracksImage.isVisible = true
        binding.phEmptyFavoriteTracksErrorMessage.isVisible = true
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}