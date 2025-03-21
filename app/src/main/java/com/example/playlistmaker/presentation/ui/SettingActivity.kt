package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.App

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = applicationContext as App
        binding.themeSwitcher.isChecked = app.darkTheme

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            app.switchTheme(isChecked)
        }

        binding.shareAppOption.setOnClickListener {
            shareApp()
        }

        binding.helpOption.setOnClickListener {
            contactSupport()
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

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_share_url))
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
    }


    private fun contactSupport() {
        val recipient = getString(R.string.support_email)
        val subject = getString(R.string.support_subject)
        val message = getString(R.string.support_message)

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipient")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(emailIntent)
    }
}


