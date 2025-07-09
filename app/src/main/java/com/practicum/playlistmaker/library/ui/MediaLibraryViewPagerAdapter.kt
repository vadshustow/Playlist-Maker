package com.practicum.playlistmaker.library.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.library.ui.fragment.FavoriteTracksFragment
import com.practicum.playlistmaker.library.ui.fragment.PlaylistsFragment

class MediaLibraryViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteTracksFragment.Companion.newInstance()
            else -> PlaylistsFragment.Companion.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}