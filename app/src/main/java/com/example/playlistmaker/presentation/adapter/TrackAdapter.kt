package com.example.playlistmaker.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.presentation.util.ClickDebounce
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.viewholder.TrackViewHolder

class TrackAdapter(
    private val onItemClickListener: (Track) -> Unit,
    private val onAddToHistoryClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var tracks: List<Track> = emptyList()
    private val clickDebounce = ClickDebounce(CLICK_DEBOUNCE_DELAY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val item = tracks[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            if (clickDebounce.clickDebounce()) {
                onItemClickListener(item)
                onAddToHistoryClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun submitList(newTracks: List<Track>) {
        val diffCallback = TrackDiffCallback(tracks, newTracks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        tracks = newTracks
        diffResult.dispatchUpdatesTo(this)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}




