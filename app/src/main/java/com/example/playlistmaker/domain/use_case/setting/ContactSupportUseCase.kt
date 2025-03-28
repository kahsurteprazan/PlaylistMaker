package com.example.playlistmaker.domain.use_case.setting

import com.example.playlistmaker.domain.repository.ContactSupportRepository

class ContactSupportUseCase(private val contactSupportRepository: ContactSupportRepository) {
    operator fun invoke() {
        contactSupportRepository.contactSupport()
    }
}