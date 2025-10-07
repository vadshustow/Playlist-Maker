package com.practicum.playlistmaker.library.playlist.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentInfoPlaylistBinding
import com.practicum.playlistmaker.library.playlist.ui.view_model.InfoPlaylistViewModel
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import com.practicum.playlistmaker.utils.BindingFragment
import com.practicum.playlistmaker.utils.Transform
import org.koin.androidx.viewmodel.ext.android.viewModel

class InfoPlaylistFragment : BindingFragment<FragmentInfoPlaylistBinding>() {

    private val viewModel by viewModel<InfoPlaylistViewModel>()

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentInfoPlaylistBinding {
        return FragmentInfoPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = requireArguments().getInt("playlistId")
        viewModel.loadPlaylistInfo(playlistId)

        binding.infoPlaylistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivPlaylistInfoShare.setOnClickListener {
            sharePlaylist()
        }

        binding.ivPlaylistInfoMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        setupTrackAdapter()
        observeViewModel()
        setupMenuBottomSheet()

    }

    private fun setupTrackAdapter() {
        trackAdapter = TrackAdapter().apply {
            setItemClickListener { track ->
                openAudioPlayer(track)
            }
            setOnItemLongClickListener { track ->
                showDeleteTrackDialog(track)
            }
        }
        binding.rvInfoPlaylistBottomSheet.adapter = trackAdapter
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.infoPlaylistMenuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
        }
        binding.ivPlaylistInfoMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

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
        }
        binding.tvInfoPlaylistMenuShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }
        binding.tvInfoPlaylistMenuDeletePlaylist.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }
        binding.tvInfoPlaylistMenuEditInfo.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val playlistId = requireArguments().getInt("playlistId")
            val bundle = Bundle().apply {
                putInt("playlist_id", playlistId)
                putBoolean("is_edit_mode", true)
            }
            findNavController().navigate(R.id.action_info_playlist_to_edit_playlist, bundle)
        }
    }

    private fun sharePlaylist() {
        val shareText = viewModel.getShareText()
        if (shareText == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.info_playlist_no_playlist),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Поделиться плейлистом")
        startActivity(shareIntent)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                state.playlist?.let { playlist ->
                    binding.tvPlaylistInfoName.text = playlist.name
                    binding.tvPlaylistInfoDescription.text = playlist.description ?: ""
                    binding.tvPlaylistInfoTracksCount.text = Transform.getTracksCountText(playlist.trackCount)
                    Glide.with(this@InfoPlaylistFragment)
                        .load(playlist.coverImagePath)
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .into(binding.ivPlaylistInfoCover)

                    binding.tvInfoPlaylistMenuName.text = playlist.name
                    binding.tvInfoPlaylistMenuTracksCount.text = Transform.getTracksCountText(playlist.trackCount)
                    Glide.with(this@InfoPlaylistFragment)
                        .load(playlist.coverImagePath)
                        .placeholder(R.drawable.placeholder)
                        .transform(CenterCrop(),
                            RoundedCorners(
                            Transform.dpToPx(2f, requireContext())
                        ))
                        .into(binding.ivInfoPlaylistMenuCover)

                    val durationSum = state.tracks.sumOf { track ->
                        val parts = track.trackTimeMillis.split(":")
                        if (parts.size == 2) {
                            val minutes = parts[0].toLongOrNull() ?: 0L
                            val seconds = parts[1].toLongOrNull() ?: 0L
                            (minutes * 60_000) + (seconds * 1000)
                        } else {
                            track.trackTimeMillis.toLongOrNull() ?: 0L
                        }
                    }
                    val totalMinutes = durationSum / 60000
                    binding.tvPlaylistInfoTotalDurationTracks.text = Transform.getMinutesText(totalMinutes.toInt())
                    trackAdapter.submitTracks(state.tracks)
                }
            }
        }
    }

    private fun openAudioPlayer(track: Track) {
        val playerIntent = Intent(requireContext(), AudioPlayerActivity::class.java)
        playerIntent.putExtra("track_info", track)
        startActivity(playerIntent)
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.info_playlist_delete_track)
            .setNegativeButton("НЕТ") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("ДА") { dialog, _ ->
                viewModel.removeTrackFromPlaylist(track.trackId)
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val currentPlaylist = viewModel.state.value.playlist
        val playlistName = currentPlaylist?.name ?: ""

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить плейлист \"$playlistName\"?")
            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Да") { dialog, _ ->
                currentPlaylist?.let { playlist ->
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
                dialog.dismiss()
            }
            .show()
    }
}