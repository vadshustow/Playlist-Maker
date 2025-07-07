package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mediaLibraryToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.vp2MediaLibrary.adapter = MediaLibraryViewPagerAdapter(
            supportFragmentManager,
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

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}