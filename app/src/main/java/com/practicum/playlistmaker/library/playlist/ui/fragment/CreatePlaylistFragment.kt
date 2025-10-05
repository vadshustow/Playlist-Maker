package com.practicum.playlistmaker.library.playlist.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.library.playlist.ui.CreatePlaylistState
import com.practicum.playlistmaker.library.playlist.ui.view_model.CreatePlaylistViewModel
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : BindingFragment<FragmentCreatePlaylistBinding>() {

    private var selectedImageUri: Uri? = null

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.imagePlaylist.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }

        binding.createPlaylistToolbar.setOnClickListener {
            handleBackPressed()
        }

        binding.namePlaylistEditText.doOnTextChanged { text, start, before, count ->
            binding.createButton.isEnabled = text?.isNotBlank() == true
        }

        binding.imagePlaylist.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            val name = binding.namePlaylistEditText.text.toString().trim()
            val description = binding.descriptionPlaylistEditText.text.toString().trim()

            viewModel.createPlaylist(name, description, selectedImageUri)
        }

        viewModel.createPlaylistState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CreatePlaylistState.Default ->
                    binding.createPlaylistProgressBar.isVisible = false

                is CreatePlaylistState.Loading ->
                    binding.createPlaylistProgressBar.isVisible = true

                is CreatePlaylistState.Success -> {
                    binding.createPlaylistProgressBar.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        "Плейлист ${binding.namePlaylistEditText.text} создан",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetState()
                    safeNavigateBack()
                }

                is CreatePlaylistState.Error -> {
                    binding.createPlaylistProgressBar.isVisible = false
                    Snackbar.make(binding.root, "Ошибка: ${state.message}", Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
            }
        }
    }

    private fun handleBackPressed() {
        val hasUnsavedData = selectedImageUri != null ||
                binding.namePlaylistEditText.text?.isNotBlank() == true ||
                binding.descriptionPlaylistEditText.text?.isNotBlank() == true

        if (hasUnsavedData) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохранённые данные будут потеряны")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Завершить") { _, _ -> safeNavigateBack() }
                .show()
        } else {
            safeNavigateBack()
        }
    }

    private fun safeNavigateBack() {
        val navController = runCatching { findNavController() }.getOrNull()
        if (navController != null) {
            navController.navigateUp()
        } else {
            parentFragmentManager.popBackStack()
        }
    }
}