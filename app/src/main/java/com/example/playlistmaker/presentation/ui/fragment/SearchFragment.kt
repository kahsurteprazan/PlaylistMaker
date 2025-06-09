package com.example.playlistmaker.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.presentation.ui.AudioPlayerActivity
import com.example.playlistmaker.presentation.viewmodel.search.SearchState
import com.example.playlistmaker.presentation.viewmodel.search.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding ?: throw RuntimeException("FragmentSearchBinding = null")

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupRecyclerViews()
        setupSearchInput()
        setupButtons()
        observeViewModel()
        updateHistoryVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
        binding.searchHistoryList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupSearchInput() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.placeholderNoResults.isVisible = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButtonSearch.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                viewModel.updateSearchQuery(query)

                if (query.isEmpty()) {
                    trackAdapter.submitList(emptyList())
                    updateHistoryVisibility()
                }
            }
        })
    }

    private fun setupButtons() {
        binding.clearButtonSearch.setOnClickListener {
            binding.editTextSearch.text.clear()
            binding.placeholderNoResults.isVisible = false
            binding.placeholderNoInternet.isVisible = false
            binding.editTextSearch.requestFocus()
            trackAdapter.submitList(emptyList())
            updateHistoryVisibility()
        }

        binding.clearButtonSearchHistory.setOnClickListener {
            coroutineScope.launch {
                viewModel.clearHistory()
            }
        }

        binding.refreshButton.setOnClickListener {
            val query = binding.editTextSearch.text.toString()
            if (query.isNotEmpty()) {
                viewModel.updateSearchQuery(query)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchState.collect { state ->
                        when (state) {
                            is SearchState.Initial -> showInitialState()
                            SearchState.Loading -> showLoading()
                            is SearchState.Content -> showSearchResults(state.tracks)
                            SearchState.Empty -> showEmptyResults()
                            is SearchState.Error -> handleSearchError(state.throwable)
                        }
                    }
                }

                launch {
                    viewModel.historyState.collect { history ->
                        historyAdapter.submitList(history)
                        updateHistoryVisibility()
                    }
                }
            }
        }
    }

    private fun showInitialState() {
        binding.apply {
            progressBar.isVisible = false
            recyclerView.isVisible = false
            placeholderNoResults.isVisible = false
            placeholderNoInternet.isVisible = false
            updateHistoryVisibility()
        }
    }

    private fun onTrackClick(track: Track) {
        startActivity(
            Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                putExtra(KEY_TRACK, TrackUi(track))
            }
        )
    }

    private fun onAddToHistoryClick(track: Track) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addToHistory(track)
        }
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
        val hasHistory = viewModel.historyState.value.isNotEmpty()
        val hasSearchQuery = !binding.editTextSearch.text.isNullOrEmpty()

        binding.historySearch.isVisible = hasHistory && !hasSearchQuery
        binding.recyclerView.isVisible = !binding.historySearch.isVisible
    }

    companion object {
        const val KEY_TRACK = "track"
    }
}
