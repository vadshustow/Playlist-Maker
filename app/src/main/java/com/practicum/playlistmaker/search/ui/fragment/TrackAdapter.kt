package com.practicum.playlistmaker.search.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.model.Track

class TrackAdapter() : RecyclerView.Adapter<TrackViewHolder> () {

    private var itemClickListener: ((Track) -> Unit)? = null
    private var itemLongClickListener: ((Track) -> Unit)? = null

    private var tracks = ArrayList<Track>()

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
        holder.itemView.setOnLongClickListener {
            itemLongClickListener?.invoke(tracks[position])
            true
        }
    }

    fun setItemClickListener(listener: (Track) -> Unit) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (Track) -> Unit) {
        itemLongClickListener = listener
    }

    fun submitTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}