package com.practicum.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.utils.App
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.darkThemeEnabled.observe(viewLifecycleOwner, { darkTheme ->
            binding.themeSwitcher.isChecked = darkTheme
        })

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkThemeEnabled(isChecked)
            (requireContext().applicationContext as App).switchTheme(isChecked)
        }

        binding.shareBtn.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportBtn.setOnClickListener {
            viewModel.sendEmailToSupport()
        }

        binding.userAgreementBtn.setOnClickListener {
            viewModel.openTerms()
        }
    }
}