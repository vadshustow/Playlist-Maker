package com.practicum.playlistmaker.search.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.ui.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel

const val INTENT_TRACK_INFO = "track_info"

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private val handler = Handler(Looper.getMainLooper())
    private val searchTrackRunnable = Runnable { search() }

    private var isClickAllowed = true
    private var searchText: String = ""

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvTrack.adapter = adapter
        binding.rvSearchHistoryTrack.adapter = historyAdapter

        viewModel = ViewModelProvider(
            this, SearchViewModel
                .getFactory()
        ).get(SearchViewModel::class.java)

        viewModel.searchState.observe(this) { state ->
            when (state) {
                is SearchState.Loading -> {
                    binding.searchProgressBar.visibility = View.VISIBLE
                    binding.rvTrack.visibility = View.GONE
                }
                is SearchState.Tracks -> {
                    binding.searchProgressBar.visibility = View.GONE
                    adapter.submitTracks(state.tracks)
                    togglePlaceholder(false)
                    binding.rvTrack.visibility = if (state.tracks.isEmpty()) View.GONE else View.VISIBLE
                }
                is SearchState.History -> {
                    binding.rvTrack.visibility = View.GONE
                    historyAdapter.submitTracks(state.history)
                    binding.searchTrackHistory.visibility = if (state.history.isNotEmpty()) View.VISIBLE else View.GONE
                }
                is SearchState.Error -> {
                    binding.searchProgressBar.visibility = View.GONE
                    togglePlaceholder(true, state.message, state.imageRes)
                }
            }
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        adapter.setItemClickListener { track ->
            if (clickDebounce()) {
                openAudioPlayer(track)
                viewModel.addTrackToHistory(track)
            }
        }

        historyAdapter.setItemClickListener { track ->
            if (clickDebounce()) {
                openAudioPlayer(track)
            }
        }

        binding.searchClearBtn.setOnClickListener {
            binding.searchEdittext.setText("")
            closeKeyboard(binding.searchEdittext)
            togglePlaceholder(false)
            viewModel.clearSearchResults()
            viewModel.loadHistory()
        }

        binding.placeholderButton.setOnClickListener {
            togglePlaceholder(false)
            search()
        }

        binding.searchEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchClearBtn.visibility = clearButtonVisibility(s)
                searchText = s.toString()
                binding.searchTrackHistory.visibility =
                    if (binding.searchEdittext.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
                searchTrackDebounce()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEdittext.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }

        binding.searchHistoryClearButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.searchEdittext.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(searchText)
                true
            }
            false
        }
    }

    private fun search() {
        if (searchText.isNotBlank()) {
            viewModel.search(searchText)
        }
    }

    private fun openAudioPlayer(track: Track) {
        val playerIntent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
        playerIntent.putExtra(INTENT_TRACK_INFO, track)
        startActivity(playerIntent)
    }

    private fun togglePlaceholder(show: Boolean, message: String = "", imageRes: Int? = null) {
        binding.placeholderError.visibility = if (show) View.VISIBLE else View.GONE
        findViewById<RecyclerView>(R.id.rvTrack).visibility = if (show) View.GONE else View.VISIBLE
        binding.placeholderButton.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            binding.placeholderErrorMessage.text = message
            imageRes?.let { binding.placeholderImage.setImageResource(it) }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun closeKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchTrackDebounce() {
        handler.removeCallbacks(searchTrackRunnable)
        handler.postDelayed(searchTrackRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}