package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

class TrackHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : TrackHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()
    private val historyUpdates = MutableSharedFlow<Unit>(replay = 1)

    override fun addTrack(track: Track): Flow<Unit> = flow {
        val currentHistory = getHistorySync().toMutableList()

        currentHistory.removeAll { it.trackId == track.trackId }

        currentHistory.add(0, track)

        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.subList(MAX_HISTORY_SIZE, currentHistory.size).clear()
        }

        saveHistorySync(currentHistory)
        emit(Unit)
        historyUpdates.emit(Unit)
    }

    override fun getHistory(): Flow<List<Track>> = flow {
        emit(getHistorySync())

        historyUpdates.collect {
            emit(getHistorySync())
        }
    }

    override fun clearHistory(): Flow<Unit> = flow {
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()
        emit(Unit)
        historyUpdates.emit(Unit)
    }

    override fun saveHistory(history: List<Track>): Flow<Unit> = flow {
        saveHistorySync(history)
        emit(Unit)
        historyUpdates.emit(Unit)
    }

    private fun getHistorySync(): List<Track> {
        return try {
            val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson<List<Track>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("TrackHistoryRepo", "Error reading history", e)
            emptyList()
        }
    }

    private fun saveHistorySync(history: List<Track>) {
        try {
            val json = gson.toJson(history)
            sharedPreferences.edit()
                .putString(HISTORY_KEY, json)
                .apply()
        } catch (e: Exception) {
            Log.e("TrackHistoryRepo", "Error saving history", e)
        }
    }
}