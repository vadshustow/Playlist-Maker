package com.practicum.playlistmaker.sharing.domain.repository

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigatorRepository {
    fun shareLink(text: String)
    fun openEmail(emailData: EmailData)
    fun openLink(url: String)
}