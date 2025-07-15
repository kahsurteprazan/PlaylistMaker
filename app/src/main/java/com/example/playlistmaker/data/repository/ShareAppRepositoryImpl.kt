package com.example.playlistmaker.data.repository

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.ShareAppRepository
import java.util.concurrent.TimeUnit

class ShareAppRepositoryImpl(private val context: Context) : ShareAppRepository {
    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.practicum_share_url))
        }
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Извините, сейчас ссылка не работает", Toast.LENGTH_SHORT).show()
        }
    }

    override fun sharePlaylist(playlist: Playlist, tracks: List<Track>, context: Context) {
        val shareText = buildShareText(playlist, tracks)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Плейлист: ${playlist.name}")
        }

        try {
            context.startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Невозможно поделиться", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        return buildString {
            appendLine(playlist.name)
            playlist.description?.takeIf { it.isNotBlank() }?.let { appendLine(it) }
            appendLine()
            appendLine("[${tracks.size}] треков")
            appendLine()
            tracks.forEachIndexed { index, track ->
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${formatDuration(track.trackTimeMillis)})")
            }
        }
    }

    private fun formatDuration(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return "$minutes:${seconds.toString().padStart(2, '0')}"
    }
}