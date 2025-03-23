package com.example.playlistmaker.domain.use_case.setting

import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeInteract(private val repository: ThemeRepository) {
    fun isDarkTheme(): Boolean = repository.isDarkTheme()
    fun switchTheme(isChecked: Boolean) = repository.switchTheme(isChecked)
}