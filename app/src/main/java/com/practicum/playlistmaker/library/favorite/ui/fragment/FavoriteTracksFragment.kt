package com.practicum.playlistmaker.library.favorite.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.library.favorite.ui.FavoriteTracksState
import com.practicum.playlistmaker.library.favorite.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.fragment.INTENT_TRACK_INFO
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import com.practicum.playlistmaker.utils.BindingFragment
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.jvm.java

class FavoriteTracksFragment : BindingFragment<FragmentFavoriteTracksBinding>() {

    private val favoriteTrackAdapter = TrackAdapter()

    private lateinit var clickDebounce: (Track) -> Unit

    private val viewModel by viewModel<FavoriteTracksViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavoriteTracks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            openAudioPlayer(track)
        }

        favoriteTrackAdapter.setItemClickListener { track ->
            clickDebounce(track)
        }

        binding.rvFavoriteTrack.adapter = favoriteTrackAdapter

        viewModel.favoriteTracksState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteTracksState.Empty -> {
                    favoriteTrackAdapter.submitTracks(emptyList())
                    showPhMessage()
                }

                is FavoriteTracksState.Content -> {
                    favoriteTrackAdapter.submitTracks(state.tracks)
                    showContent()
                }
            }
        }
    }

    private fun showContent() {
        binding.phEmptyFavoriteTracksImage.isVisible = false
        binding.phEmptyFavoriteTracksErrorMessage.isVisible = false
        binding.rvFavoriteTrack.isVisible = true
    }

    private fun showPhMessage() {
        binding.phEmptyFavoriteTracksImage.isVisible = true
        binding.phEmptyFavoriteTracksErrorMessage.isVisible = true
        binding.rvFavoriteTrack.isVisible = false
        binding.phEmptyFavoriteTracksErrorMessage.setText(R.string.library_empty_favorite_tracks_placeholder)
    }

    private fun openAudioPlayer(track: Track) {
        val playerIntent = Intent(requireContext(), AudioPlayerActivity::class.java)
        playerIntent.putExtra(INTENT_TRACK_INFO, track)
        startActivity(playerIntent)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = FavoriteTracksFragment()
    }
}