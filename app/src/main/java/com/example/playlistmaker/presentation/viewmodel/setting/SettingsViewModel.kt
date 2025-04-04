package com.example.playlistmaker.presentation.viewmodel.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.use_case.setting.ContactSupportUseCase
import com.example.playlistmaker.domain.use_case.setting.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.setting.ThemeInteract

class SettingsViewModel (
    private val themeInteract: ThemeInteract,
    private val shareAppUseCase: ShareAppUseCase,
    private val contactSupportUseCase: ContactSupportUseCase
): ViewModel()  {
    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    init {
        _isDarkTheme.value = themeInteract.isDarkTheme()
    }

    fun switchTheme(isDark: Boolean) {
        themeInteract.switchTheme(isDark)
        _isDarkTheme.value = isDark
    }

    fun shareApp() {
        shareAppUseCase()
    }

    fun contactSupport() {
        contactSupportUseCase()
    }
}