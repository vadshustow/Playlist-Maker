package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.sharing.domain.repository.ExternalNavigatorRepository

class ExternalNavigatorRepositoryImpl(private val context: Context) : ExternalNavigatorRepository {

    override fun shareLink(text: String) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "text/plain"
        context.startActivity(Intent.createChooser(intent, null).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openEmail(emailData: EmailData) {

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailData.subjectEmail)
        intent.putExtra(Intent.EXTRA_TEXT, emailData.textEmail)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openLink(url: String) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}