package com.cineby.tv.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WatchProgressEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cinebyDao(): CinebyDao
}
