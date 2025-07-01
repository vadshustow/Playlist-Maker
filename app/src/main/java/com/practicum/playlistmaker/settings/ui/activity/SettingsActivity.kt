package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val settingsToolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        val shareButton = findViewById<MaterialTextView>(R.id.share_btn)
        val supportButton = findViewById<MaterialTextView>(R.id.support_btn)
        val userAgreementButton = findViewById<MaterialTextView>(R.id.user_agreement_btn)

        settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.darkThemeEnabled.observe(this, { darkTheme ->
            themeSwitcher.isChecked = darkTheme
        })

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkThemeEnabled(isChecked)
            (applicationContext as App).switchTheme(isChecked)
        }

        shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        supportButton.setOnClickListener {
            viewModel.sendEmailToSupport()
        }

        userAgreementButton.setOnClickListener {
            viewModel.openTerms()
        }
    }
}