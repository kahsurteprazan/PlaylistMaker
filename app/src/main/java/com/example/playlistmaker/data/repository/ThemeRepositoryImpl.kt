package com.example.playlistmaker.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(context: Context) : ThemeRepository {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME, false)
    }

    override fun switchTheme(isChecked: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME, isChecked).apply()

        AppCompatDelegate.setDefaultNightMode(
            if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        private const val DARK_THEME = "dark_theme"
    }
}
