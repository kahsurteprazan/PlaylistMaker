package com.example.playlistmaker

import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .fitCenter()
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .transform(RoundedCorners(2.toPx()))
            .into(trackImage)
    }
    fun Int.toPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}