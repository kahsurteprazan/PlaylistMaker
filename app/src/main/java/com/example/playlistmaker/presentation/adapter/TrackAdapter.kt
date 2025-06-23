package com.example.playlistmaker.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.presentation.util.ClickDebounce
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.viewholder.TrackViewHolder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onItemClickListener(track)
            onAddToHistoryClickListener(track)
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




