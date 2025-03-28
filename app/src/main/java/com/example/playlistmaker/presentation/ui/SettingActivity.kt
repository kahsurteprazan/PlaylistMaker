package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.use_case.setting.ContactSupportUseCase
import com.example.playlistmaker.domain.use_case.setting.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.setting.ThemeInteract

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val themeInteract: ThemeInteract by lazy { Creator.provideThemeUseCase() }
    private lateinit var shareAppUseCase: ShareAppUseCase
    private lateinit var contactSupportUseCase: ContactSupportUseCase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shareAppUseCase = Creator.provideShareAppUseCase(this)
        contactSupportUseCase = Creator.provideContactSupportUseCase(this)

        binding.themeSwitcher.isChecked = themeInteract.isDarkTheme()

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            themeInteract.switchTheme(isChecked)
        }

        binding.shareAppOption.setOnClickListener {
            shareAppUseCase()
        }

        binding.helpOption.setOnClickListener {
            contactSupportUseCase()
        }


        binding.agreementOption.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.practicum_offer_url))
                )
            )
        }


        binding.settingsToolbar.setOnClickListener {
            finish()
        }

    }
}


