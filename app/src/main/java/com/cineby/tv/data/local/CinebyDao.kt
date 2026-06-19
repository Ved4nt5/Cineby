package com.cineby.tv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CinebyDao {
    @Query("SELECT * FROM watch_progress ORDER BY updatedAt DESC")
    fun observeWatchProgress(): Flow<List<WatchProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchProgress(entity: WatchProgressEntity)

    @Query("SELECT * FROM watch_progress WHERE mediaId = :mediaId")
    suspend fun watchProgress(mediaId: String): WatchProgressEntity?

    @Query("DELETE FROM watch_progress")
    suspend fun clearWatchProgress()

    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavorite(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE mediaId = :mediaId")
    suspend fun removeFavorite(mediaId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mediaId = :mediaId)")
    fun observeIsFavorite(mediaId: String): Flow<Boolean>
}
