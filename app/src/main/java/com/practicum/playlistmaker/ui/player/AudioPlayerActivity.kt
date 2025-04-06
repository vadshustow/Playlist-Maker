package com.practicum.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.ui.search.INTENT_TRACK_INFO
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.ui.search.TrackViewHolder
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L
    }

    private val playerInteractor = Creator.providePlayerInteractor()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (playerInteractor.isPlaying()) {
                val formattedTime = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(playerInteractor.getCurrentPosition())
                binding.tvTimer.text = formattedTime
                handler.postDelayed(this, DELAY)
            }
        }
    }

    private lateinit var url: String
    private lateinit var binding: ActivityAudioPlayerBinding

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

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(INTENT_TRACK_INFO, Track::class.java)
        } else {
            intent.getParcelableExtra(INTENT_TRACK_INFO)
        }

        fillData(track)

        preparePlayer()

        binding.ibPlayButton.setOnClickListener {
            playbackControl()
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
                        TrackViewHolder(root).dpToPx(
                            8f,
                            this@AudioPlayerActivity
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

    private fun preparePlayer() {

        playerInteractor.preparePlayer(
            url = url,
            onPrepared = { playerState = STATE_PREPARED },
            onCompletion = {
                binding.ibPlayButton.setImageResource(R.drawable.ic_play_button)
                playerState = STATE_PREPARED
                handler.removeCallbacks(timerRunnable)
                binding.tvTimer.text = getString(R.string.player_timer_placeholder)
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        binding.ibPlayButton.setImageResource(R.drawable.ic_pause_button)
        playerState = STATE_PLAYING
        handler.post(timerRunnable)
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        binding.ibPlayButton.setImageResource(R.drawable.ic_play_button)
        playerState = STATE_PAUSED
        handler.removeCallbacks(timerRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> { pausePlayer() }
            STATE_PREPARED, STATE_PAUSED -> { startPlayer() }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
        playerInteractor.release()
    }

}