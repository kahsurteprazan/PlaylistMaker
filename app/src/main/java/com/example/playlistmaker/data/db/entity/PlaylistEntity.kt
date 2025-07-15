package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val coverImagePath: String? = null,
    val trackIdsJson: String = "[]",
    val trackCount: Int = 0,
) {
    fun getTrackIds(): List<Int> {
        return try {
            Gson().fromJson(trackIdsJson, object : TypeToken<List<Int>>() {}.type)
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun withTrackIds(ids: List<Int>): PlaylistEntity {
        val distinctIds = ids.distinct()
        return this.copy(
            trackIdsJson = Gson().toJson(distinctIds),
            trackCount = distinctIds.size
        )
    }

    fun addTrackId(trackId: Int): PlaylistEntity {
        val currentIds = getTrackIds()
        return if (currentIds.contains(trackId)) {
            this
        } else {
            withTrackIds(currentIds + trackId)
        }
    }

    fun removeTrackId(trackId: Int): PlaylistEntity {
        return withTrackIds(getTrackIds() - trackId)
    }
}