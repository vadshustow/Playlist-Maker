package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter() : RecyclerView.Adapter<TrackViewHolder> () {

    private var itemClickListener: ((Track) -> Unit)? = null

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(tracks[position])
        }
    }

    fun setItemClickListener(listener: (Track) -> Unit) {
        itemClickListener = listener
    }
}