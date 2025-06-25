package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {

    override fun getShareAppLink(): String {
        return context.getString(R.string.share_message)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.my_email),
            context.getString(R.string.letter_subject),
            context.getString(R.string.letter_text)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.url_user_agreement)
    }
}