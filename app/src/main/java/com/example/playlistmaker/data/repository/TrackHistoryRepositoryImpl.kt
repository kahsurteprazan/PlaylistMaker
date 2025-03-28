package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    TrackHistoryRepository {

    companion object {
        const val HISTORY_KEY = "search_history"
        const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf {
            Log.d("SearchHistory", "Removing track with ID: ${it.trackId}")
            it.trackId == track.trackId
        }
        Log.d("SearchHistory", "After removal: $history")
        history.add(0, track)
        Log.d("SearchHistory", "After adding new track: $history")
        if (history.size > MAX_HISTORY_SIZE) {
            history.subList(MAX_HISTORY_SIZE, history.size).clear()
        }
        saveHistory(history)
    }

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        Log.d("SearchHistory", "Loaded history JSON: $json")
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }


    override fun clearHistory() {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, "[]")
        Log.d("History", "Loaded history before clear: $historyJson")
        val editor = sharedPreferences.edit()
        editor.remove(HISTORY_KEY).apply()
        Log.d("History", "History cleared")
    }


    override fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        Log.d("SearchHistory", "Saving history JSON: $json")
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }


}