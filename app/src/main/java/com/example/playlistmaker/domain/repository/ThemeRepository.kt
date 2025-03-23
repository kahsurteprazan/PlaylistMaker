package com.example.playlistmaker.domain.repository

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun switchTheme(isChecked: Boolean)
}
