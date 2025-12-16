package com.newsly.app.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for bookmark operations.
 */
@Dao
interface BookmarkDao {

    /**
     * Returns all bookmarked articles as a Flow, ordered by save date (newest first).
     */
    @Query("SELECT * FROM bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    /**
     * Returns a single bookmark by its ID, or null if not found.
     */
    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: String): BookmarkEntity?

    /**
     * Checks if an article is bookmarked.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    suspend fun isBookmarked(id: String): Boolean

    /**
     * Checks if an article is bookmarked as a Flow for reactive updates.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    fun isBookmarkedFlow(id: String): Flow<Boolean>

    /**
     * Inserts a bookmark. Replaces if already exists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    /**
     * Deletes a bookmark by its ID.
     */
    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)

    /**
     * Deletes a bookmark entity.
     */
    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    /**
     * Returns all bookmark IDs for quick lookup.
     */
    @Query("SELECT id FROM bookmarks")
    suspend fun getAllBookmarkIds(): List<String>
}
