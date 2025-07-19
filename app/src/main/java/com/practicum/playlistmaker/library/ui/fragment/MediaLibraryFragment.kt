package com.practicum.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.practicum.playlistmaker.library.ui.MediaLibraryViewPagerAdapter
import com.practicum.playlistmaker.util.BindingFragment

class MediaLibraryFragment : BindingFragment<FragmentMediaLibraryBinding>() {

    private lateinit var tabMediator: TabLayoutMediator

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentMediaLibraryBinding {
        return FragmentMediaLibraryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vp2MediaLibrary.adapter = MediaLibraryViewPagerAdapter(
            childFragmentManager,
            lifecycle
        )

        tabMediator =
            TabLayoutMediator(
                binding.tabMediaLibrary,
                binding.vp2MediaLibrary
            ) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.library_tab_favorite_tracks)
                    1 -> tab.text = getString(R.string.library_tab_playlists)
                }
            }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }
}