package com.example.playlistmaker.presentation.viewholder

import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.model.PlaylistUi
import com.example.playlistmaker.presentation.util.DpToPx.toPx

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val name: TextView = itemView.findViewById(R.id.namePlaylist)
    private val img: ImageView = itemView.findViewById(R.id.img)
    private val trackCount: TextView = itemView.findViewById(R.id.trackCount)

    fun bind(playlist: PlaylistUi) {
        name.text = playlist.name
        trackCount.text = "${playlist.trackCount} треков"

        if (!playlist.imgPath.isNullOrBlank()) {
            Glide.with(img.context)
                .load(playlist.imgPath)
                .centerCrop()
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(8.toPx())
                    )
                )
                .into(img)
        } else {
            img.setImageResource(R.drawable.placeholder_image)
        }
    }
}