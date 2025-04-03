package com.example.playlistmaker.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.viewmodel.SearchState
import com.example.playlistmaker.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.viewmodel.SearchViewModelFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val handler = Handler(Looper.getMainLooper())

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
                s?.let {
                    if (it.isNotEmpty()) {
                        handler.postDelayed({
                            viewModel.searchDebounced(it.toString())
                        }, SearchViewModel.SEARCH_DELAY)
                    } else {
                        showHistory()
                    }
                }
            }
        })

        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.editTextSearch.text.isNotEmpty()) {
                viewModel.searchDebounced(binding.editTextSearch.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setupButtons() {
        binding.clearButtonSearch.setOnClickListener {
            binding.editTextSearch.text.clear()
            showHistory()
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
                is SearchState.Content -> {
                    trackAdapter.submitList(state.tracks)
                    showResults()
                }
                is SearchState.Empty -> showPlaceholderNoResults()
                is SearchState.Error -> {
                    if (state.throwable is java.io.IOException) {
                        showPlaceholderNoInternet()
                    } else {
                        showPlaceholderNoResults()
                    }
                }
            }
        }

        viewModel.historyState.observe(this) { history ->
            historyAdapter.submitList(history)
            binding.historySearch.isVisible = history.isNotEmpty() &&
                    binding.editTextSearch.text.isNullOrEmpty()
        }
    }

    private fun onTrackClick(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(KEY_TRACK, track)
        startActivity(intent)
    }

    private fun onAddToHistoryClick(track: Track) {
        viewModel.addToHistory(track)
    }

    private fun showHistory() {
        viewModel.loadHistory()
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
    }

    private fun showResults() {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = true
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
    }

    private fun showPlaceholderNoResults() {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = true
        binding.placeholderNoInternet.isVisible = false
    }

    private fun showPlaceholderNoInternet() {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = true
    }

    companion object {
        const val KEY_TRACK = "track"
    }
}



