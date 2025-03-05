package com.example.playlistmaker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class SearchActivity : AppCompatActivity(){
    private val searchHandler = Handler(Looper.getMainLooper())
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding

    private var tracks: List<Track> = emptyList()
    private var searchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
        binding.editTextSearch.setText(searchQuery)

        val editText = binding.editTextSearch
        val clearButton = binding.clearButtonSearch



        // Инциализация ШерПрефернс
        val sharedPreferences = getSharedPreferences("search_history", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        // Инициализация RecyclerViewHistory Истории
        recyclerViewHistory = binding.searchHistoryList
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(emptyList(), searchHistory) { track ->
            searchHistory.addTrack(track)
            searchHistory.saveHistory(searchHistory.getHistory())
            historyAdapter.submitList(searchHistory.getHistory())
        }

        recyclerViewHistory.adapter = historyAdapter

        val history = searchHistory.getHistory()
        historyAdapter.submitList(history)
        binding.historySearch.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE

        binding.clearButtonSearchHistory.setOnClickListener {
            searchHistory.clearHistory()
            binding.historySearch.visibility = View.GONE
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            binding.historySearch.isVisible = hasFocus &&
                    searchHistory.getHistory().isNotEmpty() &&
                    editText.text.isNullOrEmpty()
        }


        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isQueryEmpty = s.isNullOrEmpty()
                searchQuery = s?.toString()

                clearButton.isVisible = !isQueryEmpty

                // Если пользователь начал вводить текст — показываем ProgressBar
                binding.progressBar.isVisible = !isQueryEmpty
                binding.recyclerView.isVisible = false

                searchHandler.removeCallbacksAndMessages(null)
                searchHandler.postDelayed({
                    if (!s.isNullOrEmpty()) {
                        performSearch(s.toString())
                    } else {
                        binding.progressBar.isVisible = false
                    }
                }, SEARCH_DELAY)

                val showHistory = editText.hasFocus() && searchHistory.getHistory().isNotEmpty() && isQueryEmpty
                binding.historySearch.isVisible = showHistory
            }


            override fun afterTextChanged(s: Editable?) {}
        })

        // Очистка текста
        clearButton.setOnClickListener {
            editText.text.clear()
            binding.placeholderNoResults.isVisible = false
            binding.placeholderNoInternet.isVisible = false
            binding.refreshButton.isVisible = false
            trackAdapter.submitList(emptyList())
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

        // Кнопка назад
        binding.backAndSearch.setOnClickListener {
            finish()
        }

        // Инициализация RecyclerView ПОИСКА
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracks, searchHistory) { track ->
            searchHistory.addTrack(track)
            searchHistory.saveHistory(searchHistory.getHistory())

            val updatedHistory = searchHistory.getHistory()
            (recyclerViewHistory.adapter as TrackAdapter).submitList(updatedHistory)
        }
        recyclerView.adapter = trackAdapter

        // Обработчик кнопки "Обновить"
        binding.refreshButton.setOnClickListener {
            retrySearch()
        }
    }


    // Выполнение поиска
    private fun performSearch(query: String) {
        // Показываем индикатор загрузки
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false

        trackAdapter.submitList(emptyList())

        itunesService.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                binding.progressBar.isVisible = false

                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null) {
                        handleSearchResults(searchResponse)
                    }
                } else {
                    showPlaceholderNoResults()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                binding.progressBar.isVisible = false

                if (t is IOException) {
                    showPlaceholderNoInternet()
                } else {
                    showPlaceholderNoResults()
                }
            }
        })
    }


    private fun handleSearchResults(response: SearchResponse) {
        if (response.results.isEmpty()) {
            showPlaceholderNoResults()
        } else {
            trackAdapter.submitList(response.results)
            showResults()
        }
    }

    private fun showPlaceholderNoResults() {
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = true
        binding.placeholderNoInternet.isVisible = false
        binding.refreshButton.isVisible = false
    }

    private fun showPlaceholderNoInternet() {
        binding.recyclerView.isVisible = false
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = true
        binding.refreshButton.isVisible = true
    }

    private fun showResults() {
        binding.recyclerView.isVisible = true
        binding.placeholderNoResults.isVisible = false
        binding.placeholderNoInternet.isVisible = false
    }


    // Повторный запрос при нажатии кнопки "Обновить"
    private fun retrySearch() {
        binding.refreshButton.isVisible = false // Скрыть кнопку обновления
        performSearch(binding.editTextSearch.text.toString()) // Повторить поиск
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
        private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
        private const val SEARCH_DELAY = 2000L
    }
}



