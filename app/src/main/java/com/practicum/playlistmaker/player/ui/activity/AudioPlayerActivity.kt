package com.practicum.playlistmaker.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerState
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.fragment.INTENT_TRACK_INFO
import com.practicum.playlistmaker.utils.Transform
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

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

        binding.ibPlayButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.ibAddToFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
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
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}