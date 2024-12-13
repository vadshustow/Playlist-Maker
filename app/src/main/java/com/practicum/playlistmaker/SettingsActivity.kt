package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsToolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.settings_toolbar)
        val shareButton = findViewById<com.google.android.material.textview.MaterialTextView>(R.id.share_btn)
        val supportButton = findViewById<com.google.android.material.textview.MaterialTextView>(R.id.support_btn)
        val userAgreementButton = findViewById<com.google.android.material.textview.MaterialTextView>(R.id.user_agreement_btn)

        settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val shareMessage = getString(R.string.share_message)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, null))
        }

        supportButton.setOnClickListener {
            val letterSubject = getString(R.string.letter_subject)
            val letterText = getString(R.string.letter_text)
            val email = getString(R.string.my_email)

            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, letterSubject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, letterText)
            startActivity(supportIntent)
        }

        userAgreementButton.setOnClickListener {
            val urlUserAgreement = getString(R.string.url_user_agreement)

            val userAgreementIntent = Intent(Intent.ACTION_VIEW)
            userAgreementIntent.data = Uri.parse(urlUserAgreement)
            startActivity(userAgreementIntent)
        }
    }
}