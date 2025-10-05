package com.practicum.playlistmaker.library.playlist.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.playlist.domain.model.Playlist
import com.practicum.playlistmaker.utils.Transform

class PlaylistViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.playlist_item, parent, false)
) {

    private val cover: ImageView = itemView.findViewById(R.id.ivPlaylistCover)
    private val name: TextView = itemView.findViewById(R.id.tvPlaylistName)
    private val countTracks: TextView = itemView.findViewById(R.id.tvPlaylistCount)

    fun bind(playlist: Playlist) {
        name.text = playlist.name
        countTracks.text = when (playlist.trackCount) {
            1,101 -> "${playlist.trackCount} трек"
            2,3,4,102,103,104 -> "${playlist.trackCount} трека"
            else -> "${playlist.trackCount} треков"
        }

        Glide.with(itemView)
            .load(playlist.coverImagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    Transform.dpToPx(8f, itemView.context
                    )
                )
            )
            .into(cover)
    }
}