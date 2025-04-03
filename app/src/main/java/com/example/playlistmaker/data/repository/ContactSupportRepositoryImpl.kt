package com.example.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.repository.ContactSupportRepository

class ContactSupportRepositoryImpl(private val context: Context) : ContactSupportRepository {
    override fun contactSupport() {
        val recipient = context.getString(R.string.support_email)
        val subject = context.getString(R.string.support_subject)
        val message = context.getString(R.string.support_message)

        val mailtoUri = Uri.parse("mailto:$recipient" +
                "?subject=${Uri.encode(subject)}" +
                "&body=${Uri.encode(message)}")

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = mailtoUri
        }

        context.startActivity(emailIntent)
    }
}