package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences
import com.example.playlistmaker.creator.Creator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)

        val themeUseCase = Creator.provideThemeUseCase()
        AppCompatDelegate.setDefaultNightMode(
            if (themeUseCase.isDarkTheme()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}

