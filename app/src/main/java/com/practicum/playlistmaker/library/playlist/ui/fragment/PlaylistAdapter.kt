package com.practicum.playlistmaker.library.playlist.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.playlist.domain.model.Playlist

class PlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onClick: (Playlist) -> Unit,
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaylistViewHolder {
        return PlaylistViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int,
    ) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onClick(playlists[position]) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}