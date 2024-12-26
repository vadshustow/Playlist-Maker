package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)) {

        private val image: ImageView = itemView.findViewById(R.id.ivArtwork)
        private val trackName: TextView = itemView.findViewById(R.id.tvTrackName)
        private val artistName: TextView = itemView.findViewById(R.id.tvArtistName)
        private val trackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(image)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}