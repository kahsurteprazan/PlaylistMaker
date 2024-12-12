package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.shareAppOption.setOnClickListener {
            shareApp()
        }

        binding.heeeelp.setOnClickListener {
            contactSupport()
        }

        binding.sogl.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/")))
        }


        findViewById<ImageButton>(R.id.ib_backSetting).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Привет! Я прохожу крутой курс по Android-разработке в Практикуме. Присоединяйся по ссылке: https://practicum.yandex.ru/android-developer/")
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
    }
    private fun contactSupport() {
        val recipient = "nunu.27@mail.ru"
        val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        val message = "Спасибо разработчикам и разработчицам за крутое приложение!"

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipient")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(emailIntent)
    }
}
