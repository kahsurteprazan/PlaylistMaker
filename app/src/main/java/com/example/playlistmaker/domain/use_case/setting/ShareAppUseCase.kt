package com.example.playlistmaker.domain.use_case.setting

import com.example.playlistmaker.domain.repository.ShareAppRepository

class ShareAppUseCase(private val shareAppRepository: ShareAppRepository) {
    operator fun invoke() {
        shareAppRepository.shareApp()
    }
}