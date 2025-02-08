package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private var searchText: String = TEXT_DEF

    private lateinit var rvSearchHistoryTrack: RecyclerView
    private lateinit var searchHistory: SearchHistory
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchTrackHistory: LinearLayout
    private lateinit var searchHistoryClearButton: Button
    private lateinit var inputEditText: EditText
    private lateinit var searchToolbar: MaterialToolbar
    private lateinit var clearButton: ImageView
    private lateinit var rvTrack: RecyclerView
    private lateinit var phError: LinearLayout
    private lateinit var phImage: ImageView
    private lateinit var phMessage: TextView
    private lateinit var phButton: MaterialButton

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchToolbar = findViewById(R.id.search_toolbar)
        searchToolbar.setNavigationOnClickListener {
            finish()
        }

        rvSearchHistoryTrack = findViewById(R.id.rvSearchHistoryTrack)
        sharedPrefs = getSharedPreferences(TRACK_LIST_KEY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        historyAdapter.tracks = searchHistory.getHistoryList() as ArrayList<Track>
        rvSearchHistoryTrack.adapter = historyAdapter

        adapter.setItemClickListener { track ->
            searchHistory.addTrackToSearchHistory(track)
        }

        searchTrackHistory = findViewById(R.id.searchTrackHistory)
        inputEditText = findViewById(R.id.search_edittext)
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (searchHistory.getHistoryList().isNotEmpty()) {
                searchTrackHistory.visibility = View.VISIBLE
                tracks.addAll(searchHistory.getHistoryList())
            }
        }

        searchHistoryClearButton = findViewById(R.id.searchHistoryClearButton)
        searchHistoryClearButton.setOnClickListener {
            searchHistory.clearSearchHistory()
            searchTrackHistory.visibility = View.GONE
            tracks.clear()
            historyAdapter.notifyDataSetChanged()

        }

        clearButton = findViewById(R.id.search_clear_btn)
        rvTrack = findViewById(R.id.rvTrack)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            closeKeyboard(inputEditText)
            tracks.clear()
            adapter.notifyDataSetChanged()
            if (searchHistory.getHistoryList().isNotEmpty()) {
                searchTrackHistory.visibility = View.VISIBLE
                rvTrack.visibility = View.GONE
                tracks.addAll(searchHistory.getHistoryList())
                historyAdapter.notifyDataSetChanged()
            } else {
                searchTrackHistory.visibility = View.GONE
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
                searchTrackHistory.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        adapter.tracks = tracks
        rvTrack.adapter = adapter


        phButton = findViewById(R.id.placeholderButton)
        phButton.setOnClickListener {
            searchTrack()
        }

    }

    private fun searchTrack() {
        if (inputEditText.text.isNotEmpty()) {
            itunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                showErrorMessage("", "", null)
                            }
                            if (tracks.isEmpty()) {
                                showErrorMessage(
                                    getString(R.string.nothing_found),
                                    "",
                                    R.drawable.ic_nothing_found
                                )
                                phButton.visibility = View.GONE
                            } else {
                                showErrorMessage("", "", null)
                            }
                        } else {
                            showErrorMessage(
                                getString(R.string.connection_error),
                                "",
                                R.drawable.ic_connection_error
                            )
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        showErrorMessage(
                            getString(R.string.connection_error),
                            "",
                            R.drawable.ic_connection_error
                        )
                    }

                })
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun showErrorMessage(text: String, additionalMessage: String, imageId: Int?) {
        phMessage = findViewById(R.id.placeholderErrorMessage)
        phImage = findViewById(R.id.placeholderImage)
        phError = findViewById(R.id.placeholderError)
        if (text.isNotEmpty()) {
            rvTrack.visibility = View.GONE
            phError.visibility = View.VISIBLE
            phButton.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            phMessage.text = text
            if (imageId != null) {
                phImage.setImageResource(imageId)
            }
        } else {
            phError.visibility = View.GONE
            rvTrack.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(INPUT_TEXT, TEXT_DEF)
        inputEditText.setText(searchText)
        if (searchText.isNotEmpty()) searchTrack()
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

    private companion object {
        const val TEXT_DEF = ""
        const val INPUT_TEXT = "INPUT_TEXT"
    }
}