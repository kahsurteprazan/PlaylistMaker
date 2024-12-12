package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    // Переменная для сохранения текста
    private var searchQuery: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Восстановление текста из savedInstanceState
        searchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
        binding.editTextSearch.setText(searchQuery)

        val editText = binding.editTextSearch
        val clearButton = binding.clearButtonSearch

        // Реализация очистить текст
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchQuery = s?.toString() // Сохраняем значение в переменную
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearButton.setOnClickListener {
            editText.text.clear()
        }

        // Полоска текста
        editText.setOnFocusChangeListener { _, hasFocus ->
            editText.isCursorVisible = hasFocus
        }

        // Кнопка назад
        binding.ibBackToMainFromSearch.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_QUERY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY)
        binding.editTextSearch.setText(searchQuery)
    }

    companion object {
        private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
    }
}