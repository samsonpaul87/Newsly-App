package com.newsly.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for Newsly app.
 * Currently only stores bookmarks.
 */
@Database(
    entities = [BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NewslyDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        const val DATABASE_NAME = "newsly_database"
    }
}
