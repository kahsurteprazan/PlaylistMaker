package com.example.playlistmaker.presentation.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.use_case.setting.ContactSupportUseCase
import com.example.playlistmaker.domain.use_case.setting.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.setting.ThemeInteract

class SettingsViewModelFactory(
    private val themeInteract: ThemeInteract,
    private val shareAppUseCase: ShareAppUseCase,
    private val contactSupportUseCase: ContactSupportUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            themeInteract,
            shareAppUseCase,
            contactSupportUseCase
        ) as T
    }
}