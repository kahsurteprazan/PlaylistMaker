package com.example.playlistmaker.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.model.PlaylistUi
import com.example.playlistmaker.presentation.viewholder.PlaylistViewHolder

class AudioPlaylistAdapter(
    private val onPlaylistClick: (Long) -> Unit
): ListAdapter<PlaylistUi, PlaylistViewHolder>(DiffCallback) {

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<PlaylistUi>() {
            override fun areItemsTheSame(oldItem: PlaylistUi, newItem: PlaylistUi): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PlaylistUi, newItem: PlaylistUi): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_v2, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlistUi = getItem(position)
        holder.bind(playlistUi)

        holder.itemView.setOnClickListener {
            onPlaylistClick(playlistUi.id)
        }
    }
}

