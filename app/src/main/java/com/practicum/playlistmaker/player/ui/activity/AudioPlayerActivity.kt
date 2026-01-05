package com.practicum.playlistmaker.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.library.playlist.ui.fragment.CreatePlaylistFragment
import com.practicum.playlistmaker.player.ui.AudioPlayerState
import com.practicum.playlistmaker.player.ui.BottomSheetPlaylistState
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.fragment.INTENT_TRACK_INFO
import com.practicum.playlistmaker.utils.Transform
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var adapter: BottomSheetPlaylistAdapter

    private lateinit var url: String
    private lateinit var track: Track

    private val viewModel by viewModel<AudioPlayerViewModel>() {
        parametersOf(url, track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.audioPlayerToolbar.setOnClickListener {
            finish()
        }

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(INTENT_TRACK_INFO, Track::class.java) as Track
        } else {
            intent.getSerializableExtra(INTENT_TRACK_INFO) as Track
        }

        fillData(track)

        setupObservers()

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.audioPlayerBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.ibPlayButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.ibAddToFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.ibAddToPlaylist.setOnClickListener {
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButtonBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.fragmentContainer.isVisible = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreatePlaylistFragment())
                .addToBackStack(null)
                .commit()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.fragmentContainer.isVisible = false
                viewModel.getPlaylists()
            }
        }
    }

    private fun fillData(track: Track?) = with(binding) {
        if (track != null) {
            url = track.previewUrl
            Glide.with(this@AudioPlayerActivity)
                .load(track.artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg"))
                .placeholder(R.drawable.ic_placeholder_player)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        Transform.dpToPx(
                            2f, this@AudioPlayerActivity
                        )
                    )
                )
                .into(ivTrackCover)

            tvPlayerTrackName.text = track.trackName
            tvPlayerArtistName.text = track.artistName
            tvTimer.text = getString(R.string.player_timer_placeholder)
            tvDurationTrack.text = track.trackTimeMillis
            if (track.collectionName.isNullOrEmpty()) {
                tvAlbumTrack.visibility = View.GONE
                tvTitleAlbumTrack.visibility = View.GONE
            } else {
                tvAlbumTrack.text = track.collectionName
            }
            tvYearTrack.text = track.releaseDate
            tvGenreTrack.text = track.primaryGenreName
            tvCountryTrack.text = track.country
        } else {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.audioPlayerScreenState.observe(this) { state ->
            when (state.playerState) {
                AudioPlayerState.PLAYING -> {
                    binding.ibPlayButton.setImageResource(R.drawable.ic_pause_button)
                }
                AudioPlayerState.PREPARED, AudioPlayerState.PAUSED -> {
                    binding.ibPlayButton.setImageResource(R.drawable.ic_play_button)
                }
                AudioPlayerState.DEFAULT -> {
                    binding.ibPlayButton.isEnabled = true
                }
            }
            binding.tvTimer.text = state.progressTime
        }

        viewModel.isFavorite.observe(this) { isClicked ->
            if (isClicked) {
                binding.ibAddToFavoriteButton.setImageResource(R.drawable.ic_added_to_favorite)
            } else {
                binding.ibAddToFavoriteButton.setImageResource(R.drawable.ic_add_to_favorite)
            }
        }

        viewModel.bottomSheetPlaylistState.observe(this) { state ->
            when (state) {
                is BottomSheetPlaylistState.Empty -> {
                    binding.rvPlaylistsBottomSheet.isVisible = false
//                    BottomSheetBehavior.from(binding.audioPlayerBottomSheet).state =
//                        BottomSheetBehavior.STATE_HIDDEN
                }
                is BottomSheetPlaylistState.Content -> {
                    binding.rvPlaylistsBottomSheet.isVisible = true
                    adapter = BottomSheetPlaylistAdapter(state.playlists) { playlist ->
                        viewModel.onPlaylistClicked(playlist)
                    }
                    binding.rvPlaylistsBottomSheet.adapter = adapter
                    adapter.notifyDataSetChanged()

                }
                is BottomSheetPlaylistState.ShowToast -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is BottomSheetPlaylistState.CloseBottomSheet -> {
                    BottomSheetBehavior.from(binding.audioPlayerBottomSheet).state =
                        BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}