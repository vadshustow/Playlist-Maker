package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.sharing.domain.repository.ExternalNavigatorRepository
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigatorRepository,
    private val repository: SharingRepository,
) : SharingInteractor {
    override fun shareApp() {
        return externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        return externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        return externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return repository.getShareAppLink()
    }

    private fun getSupportEmailData(): EmailData {
        return repository.getSupportEmailData()
    }

    private fun getTermsLink(): String {
        return repository.getTermsLink()
    }
}