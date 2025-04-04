package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.presentation.viewmodel.setting.SettingsViewModel
import com.example.playlistmaker.presentation.viewmodel.setting.SettingsViewModelFactory

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupObservers()
        setupListeners()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(
                Creator.provideThemeUseCase(),
                Creator.provideShareAppUseCase(this),
                Creator.provideContactSupportUseCase(this)
            )
        ).get(SettingsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.isDarkTheme.observe(this) { isDark ->
            binding.themeSwitcher.isChecked = isDark
        }
    }

    private fun setupListeners() {
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        binding.shareAppOption.setOnClickListener {
            viewModel.shareApp()
        }

        binding.helpOption.setOnClickListener {
            viewModel.contactSupport()
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


