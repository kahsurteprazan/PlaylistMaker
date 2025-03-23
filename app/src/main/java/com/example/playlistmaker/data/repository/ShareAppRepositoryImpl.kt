package com.example.playlistmaker.data.repository

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.repository.ShareAppRepository

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
}