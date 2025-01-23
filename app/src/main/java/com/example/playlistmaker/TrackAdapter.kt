package com.example.playlistmaker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private var tracks: List<Track>,
    private val searchHistory: SearchHistory,
    private val onItemClickListener: OnTrackClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val item = tracks[position]
        holder.bind(item, onItemClickListener)

        holder.itemView.setOnClickListener {
            Log.d("TrackAdapter", "Clicked on track: ${item.trackId}")
            searchHistory.addTrack(item)
            Log.d("TrackAdapter", "History after add: ${searchHistory.getHistory()}")
            searchHistory.saveHistory(searchHistory.getHistory())
            onItemClickListener.onTrackClick(item)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun submitList(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}





