package com.practicum.playlistmaker.player.ui.activity

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.playlist.domain.model.Playlist

class BottomSheetPlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BottomSheetPlaylistViewHolder {
        return BottomSheetPlaylistViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: BottomSheetPlaylistViewHolder,
        position: Int,
    ) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onPlaylistClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}