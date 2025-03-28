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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.use_case.search.AddTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.ClearHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SaveHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SearchTracksUseCase
import com.example.playlistmaker.presentation.adapter.TrackAdapter

import java.io.IOException

class SearchActivity : AppCompatActivity() {

    private val searchHandler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivitySearchBinding

    // Use cases
    private lateinit var addTrackToHistoryUseCase: AddTrackToHistoryUseCase
    private lateinit var getHistoryUseCase: GetHistoryUseCase
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase
    private lateinit var saveHistoryUseCase: SaveHistoryUseCase
    private lateinit var searchTracksUseCase: SearchTracksUseCase

    // Adapters
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var trackAdapter: TrackAdapter

    private var searchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация use cases через Creator
        addTrackToHistoryUseCase = Creator.provideAddTrackToHistoryUseCase()
        getHistoryUseCase = Creator.provideGetHistoryUseCase()
        clearHistoryUseCase = Creator.provideClearHistoryUseCase()
        saveHistoryUseCase = Creator.provideSaveHistoryUseCase()
        searchTracksUseCase = Creator.provideSearchTracksUseCase()

        // Восстановление состояния поиска
        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
        binding.editTextSearch.setText(searchQuery)

        // Инициализация адаптеров
        trackAdapter = TrackAdapter(
            onItemClickListener = { track ->

                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra(KEY_TRACK, track)
                startActivity(intent)
            },
            onAddToHistoryClickListener = { track ->
                addTrackToHistoryUseCase(track)
                updateHistory(false)
            }
        )
        historyAdapter = TrackAdapter(
            onItemClickListener = { track ->
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra(KEY_TRACK, track)
                startActivity(intent)
            },
            onAddToHistoryClickListener = { track ->
                addTrackToHistoryUseCase(track)
                updateHistory()
            }
        )

        // Настройка RecyclerView для истории
        binding.searchHistoryList.layoutManager = LinearLayoutManager(this)
        binding.searchHistoryList.adapter = historyAdapter

        // Настройка RecyclerView для результатов поиска
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = trackAdapter

        updateHistory()

        // Обработка ввода текста
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isQueryEmpty = s.isNullOrEmpty()
                searchQuery = s?.toString()

                binding.clearButtonSearch.isVisible = !isQueryEmpty

                binding.recyclerView.isVisible = false

                searchHandler.removeCallbacksAndMessages(null)
                searchHandler.postDelayed({
                    if (!s.isNullOrEmpty()) {
                        binding.progressBar.isVisible = true
                        performSearch(s.toString())
                    } else {
                        binding.progressBar.isVisible = false
                    }
                }, SEARCH_DELAY)

                val showHistory = binding.editTextSearch.hasFocus() && getHistoryUseCase().isNotEmpty() && isQueryEmpty
                binding.historySearch.isVisible = showHistory
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Очистка текста
        binding.clearButtonSearch.setOnClickListener {
            binding.editTextSearch.text.clear()
            showHistory()
        }

        // Поиск при нажатии на кнопку Done
        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !searchQuery.isNullOrEmpty()) {
                performSearch(searchQuery ?: "")
                true
            } else {
                false
            }
        }

        // Кнопка "Очистить историю"
        binding.clearButtonSearchHistory.setOnClickListener {
            clearHistoryUseCase()
            updateHistory()
        }

        // Кнопка "Назад"
        binding.backAndSearch.setOnClickListener {
            finish()
        }

        // Кнопка "Обновить"
        binding.refreshButton.setOnClickListener {
            retrySearch()
        }
    }

    // Выполнение поиска
    private fun performSearch(query: String) {
        showProgressBar()

        searchTracksUseCase.search(query) { result ->
            binding.progressBar.isVisible = false

            result.onSuccess { data ->
                if (data.isNotEmpty()) {
                    trackAdapter.submitList(data)
                    showResults()
                } else {
                    showPlaceholderNoResults()
                }
            }.onFailure { exception ->
                if (exception is IOException) {
                    showPlaceholderNoInternet()
                } else {
                    showPlaceholderNoResults()
                }
            }
        }
    }

    // Обновление истории
    private fun updateHistory(showHistory: Boolean = true) {
        val history = getHistoryUseCase()
        historyAdapter.submitList(history)
        binding.historySearch.isVisible = showHistory && history.isNotEmpty()
        binding.searchHistoryList.scrollToPosition(0)
    }

    // Показать историю
    private fun showHistory() {
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.historySearch.isVisible = getHistoryUseCase().isNotEmpty()
    }

    // Показать индикатор загрузки
    private fun showProgressBar() {
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
    }

    // Показать результаты поиска
    private fun showResults() {
        binding.recyclerView.isVisible = true
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
    }

    // Показать плейсхолдер "Нет результатов"
    private fun showPlaceholderNoResults() {
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = true
        binding.placeholderNoInternet.isVisible = false
    }

    // Показать плейсхолдер "Нет интернета"
    private fun showPlaceholderNoInternet() {
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = true
        binding.refreshButton.isVisible = true
    }

    // Повторный запрос при нажатии кнопки "Обновить"
    private fun retrySearch() {
        binding.refreshButton.isVisible = false
        performSearch(binding.editTextSearch.text.toString())
    }

    // Сохранение состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_QUERY, searchQuery)
    }

    // Восстановление состояния
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY)
        binding.editTextSearch.setText(searchQuery)
    }

    companion object {
        private const val KEY_TRACK = "track"
        private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
        private const val SEARCH_DELAY = 2000L
    }
}



