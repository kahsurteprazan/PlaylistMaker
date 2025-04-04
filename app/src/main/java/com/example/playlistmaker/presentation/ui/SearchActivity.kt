package com.example.playlistmaker.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.presentation.viewmodel.search.SearchState
import com.example.playlistmaker.presentation.viewmodel.search.SearchViewModel
import com.example.playlistmaker.presentation.viewmodel.search.SearchViewModelFactory
import java.io.IOException


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var isFirstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupAdapters()
        setupRecyclerViews()
        setupSearchInput()
        setupButtons()
        observeViewModel()

        savedInstanceState?.let {
            isFirstLaunch = it.getBoolean("IS_FIRST_LAUNCH", true)
        }

        viewModel.loadHistory()
        binding.editTextSearch.post {
            binding.editTextSearch.requestFocus()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_FIRST_LAUNCH", isFirstLaunch)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                Creator.provideSearchTracksUseCase(),
                Creator.provideAddTrackToHistoryUseCase(),
                Creator.provideGetHistoryUseCase(),
                Creator.provideClearHistoryUseCase()
            )
        ).get(SearchViewModel::class.java)
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(
            onItemClickListener = ::onTrackClick,
            onAddToHistoryClickListener = ::onAddToHistoryClick
        )
        historyAdapter = TrackAdapter(
            onItemClickListener = ::onTrackClick,
            onAddToHistoryClickListener = ::onAddToHistoryClick
        )
    }

    private fun setupRecyclerViews() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = trackAdapter
        }
        binding.searchHistoryList.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyAdapter
        }
    }

    private fun setupSearchInput() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                binding.clearButtonSearch.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                viewModel.updateSearchQuery(query)

                if (query.isEmpty()) {
                    trackAdapter.submitList(emptyList())
                    viewModel.loadHistory()
                } else {
                    handler.postDelayed({
                        viewModel.searchDebounced(query)
                    }, SearchViewModel.SEARCH_DELAY)
                }
            }
        })

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.editTextSearch.text.isNullOrEmpty()) {
                viewModel.loadHistory()
            }
        }
    }

    private fun setupButtons() {
        binding.clearButtonSearch.setOnClickListener {
            binding.editTextSearch.text.clear()
            binding.editTextSearch.requestFocus()
            trackAdapter.submitList(emptyList())
            viewModel.loadHistory()
        }

        binding.clearButtonSearchHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.backAndSearch.setOnClickListener {
            finish()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.searchDebounced(binding.editTextSearch.text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.searchState.observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showSearchResults(state.tracks)
                is SearchState.Empty -> showEmptyResults()
                is SearchState.Error -> handleSearchError(state.throwable)
            }
        }

        viewModel.historyState.observe(this) { history ->
            historyAdapter.submitList(history)
            updateHistoryVisibility()
        }
    }

    private fun onTrackClick(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(KEY_TRACK, TrackUi(track))
        }
        startActivity(intent)
    }

    private fun onAddToHistoryClick(track: Track) {
        viewModel.addToHistory(track)
    }

    private fun showLoading() {
        binding.historySearch.isVisible = false
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
    }

    private fun showSearchResults(tracks: List<Track>) {
        binding.historySearch.isVisible = false
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = true
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        trackAdapter.submitList(tracks)
    }

    private fun showEmptyResults() {
        binding.historySearch.isVisible = false
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = true
        binding.placeholderNoInternet.isVisible = false
    }

    private fun showPlaceholderNoInternet() {
        binding.historySearch.isVisible = false
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = true
    }

    private fun handleSearchError(throwable: Throwable) {
        if (throwable is IOException) {
            showPlaceholderNoInternet()
        } else {
            showEmptyResults()
        }
    }

    private fun updateHistoryVisibility() {
        val hasHistory = viewModel.historyState.value?.isNotEmpty() == true
        val shouldShowHistory = hasHistory &&
                binding.editTextSearch.text.isNullOrEmpty() &&
                (binding.editTextSearch.hasFocus() || isFirstLaunch)

        binding.historySearch.isVisible = shouldShowHistory

        if (shouldShowHistory && isFirstLaunch) {
            isFirstLaunch = false
        }
    }

    companion object {
        const val KEY_TRACK = "track"
    }
}



