package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

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

        // Обработчик ввода текста
        val editText = binding.editTextSearch
        val clearButton = binding.clearButtonSearch

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchQuery = s?.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Очистка текста
        clearButton.setOnClickListener {
            editText.text.clear()
            trackAdapter.submitList(emptyList())
        }

        // Поиск при нажатии на кнопку Done
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchQuery ?: "")
                true
            }
            false
        }

        // Кнопка назад
        binding.backAndSearch.setOnClickListener {
            finish()
        }

        // Инициализация RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracks)
        recyclerView.adapter = trackAdapter

        // Обработчик кнопки "Обновить"
        binding.refreshButton.setOnClickListener {
            retrySearch() // Повторить поиск
        }
    }

    // Выполнение поиска
    private fun performSearch(query: String) {
        // Очистить RecyclerView перед запросом
        trackAdapter.submitList(emptyList())

        // Выполнить запрос
        itunesService.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null) {
                        handleSearchResults(searchResponse)
                    } else {
                        showPlaceholderNoResults()
                    }
                } else {
                    showPlaceholderNoResults() // Ошибка ответа от сервера
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                if (t is IOException) {
                    showPlaceholderNoInternet()
                } else {
                    showPlaceholderNoResults() // Используем заглушку "Нет результатов" для любых других ошибок
                }
            }
        })
    }

    private fun handleSearchResults(response: SearchResponse) {
        if (response.results.isEmpty()) {
            showPlaceholderNoResults()
        } else {
            trackAdapter.submitList(response.results)
            showResults() // Показываем список
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
    }
}

