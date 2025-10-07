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
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.library.playlist.ui.CreatePlaylistState
import com.practicum.playlistmaker.library.playlist.ui.view_model.CreatePlaylistViewModel
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

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

    private var isEditMode: Boolean = false
    private var playlistId: Int = -1

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEditMode) {
            binding.createPlaylistToolbar.title = getString(R.string.edit_playlist_title)
            binding.createButton.text = getString(R.string.save_button)
        }

        if (isEditMode && playlistId != -1) {
            viewModel.loadPlaylistForEditing(playlistId)
        }

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

            if (isEditMode) {
                viewModel.updatePlaylist(playlistId, name, description, selectedImageUri)
            } else {
                viewModel.createPlaylist(name, description, selectedImageUri)
            }
        }

        viewModel.createPlaylistState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CreatePlaylistState.Default -> {
                    binding.createPlaylistProgressBar.isVisible = false
                }
                is CreatePlaylistState.Loading -> {
                    binding.createPlaylistProgressBar.isVisible = true
                }
                is CreatePlaylistState.Success -> {
                    binding.createPlaylistProgressBar.isVisible = false
                    if (isEditMode) {
                        Toast.makeText(
                            requireContext(),
                            "Плейлист \"${binding.namePlaylistEditText.text}\" обновлён",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Плейлист \"${binding.namePlaylistEditText.text}\" создан",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    viewModel.resetState()
                    safeNavigateBack()
                }
                is CreatePlaylistState.Error -> {
                    binding.createPlaylistProgressBar.isVisible = false
                    Snackbar.make(binding.root, "Ошибка: ${state.message}", Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                is CreatePlaylistState.PlaylistLoaded -> {
                    // Заполнение полей данными плейлиста
                    binding.namePlaylistEditText.setText(state.playlist.name)
                    binding.descriptionPlaylistEditText.setText(state.playlist.description ?: "")
                    selectedImageUri = Uri.fromFile(File(state.playlist.coverImagePath))
                    Glide.with(this)
                        .load(state.playlist.coverImagePath)
                        .placeholder(R.drawable.placeholder)
                        .into(binding.imagePlaylist)
                }
            }
        }
    }

    private fun handleBackPressed() {
        if (isEditMode) {
            safeNavigateBack()
        } else {
            val hasUnsavedData = selectedImageUri != null ||
                    binding.namePlaylistEditText.text?.isNotBlank() == true ||
                    binding.descriptionPlaylistEditText.text?.isNotBlank() == true
            if (hasUnsavedData) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.create_playlist_alert_dialog_title)
                    .setMessage(R.string.create_playlist_alert_dialog_message)
                    .setNegativeButton(R.string.create_playlist_alert_dialog_negative_button, null)
                    .setPositiveButton(R.string.create_playlist_alert_dialog_positive_button) { _, _ -> safeNavigateBack() }
                    .show()
            } else {
                safeNavigateBack()
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEditMode = arguments?.getBoolean(ARG_IS_EDIT_MODE) ?: false
        playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: -1
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"
        private const val ARG_IS_EDIT_MODE = "is_edit_mode"

        fun newInstance(playlistId: Int? = null, isEditMode: Boolean = false): CreatePlaylistFragment {
            return CreatePlaylistFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLAYLIST_ID, playlistId ?: -1)
                    putBoolean(ARG_IS_EDIT_MODE, isEditMode)
                }
            }
        }
    }
}