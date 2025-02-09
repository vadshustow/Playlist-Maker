package com.practicum.playlistmaker

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding

class AudioPlayerActivity : AppCompatActivity() {

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
    }

    private fun fillData(track: Track?) {
        if (track != null) {
            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder_player)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder(binding.root).dpToPx(8f, this)))
                .into(binding.ivTrackCover)

            binding.tvPlayerTrackName.text = track.trackName
            binding.tvPlayerArtistName.text = track.artistName
            binding.tvTimer.text = getString(R.string.player_timer_placeholder)
            binding.tvDurationTrack.text = track.getTrackTime()
            if (track.collectionName.isNullOrEmpty()) {
                binding.tvAlbumTrack.visibility = View.GONE
                binding.tvTitleAlbumTrack.visibility = View.GONE
            } else {
                binding.tvAlbumTrack.text = track.collectionName
            }
            binding.tvYearTrack.text = track.getReleaseYear()
            binding.tvGenreTrack.text = track.primaryGenreName
            binding.tvCountryTrack.text = track.country
        } else {
            finish()
        }
    }

}