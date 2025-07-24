package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Update(entity = PlaylistEntity::class)
    suspend fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Query("""
        SELECT COUNT(*) > 0 
        FROM playlists 
        WHERE trackIdsJson LIKE '%' || :trackId || '%'
        AND id != :excludePlaylistId
    """)
    suspend fun containsTrackInOtherPlaylists(trackId: Int, excludePlaylistId: Long): Boolean

    @Query("""
        SELECT COUNT(*) > 0 
        FROM playlists 
        WHERE trackIdsJson LIKE '%' || :trackId || '%'
    """)
    suspend fun containsTrackInAnyPlaylist(trackId: Int): Boolean
}