package com.newsly.app.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a bookmarked article.
 */
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val author: String,
    val sourceName: String,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val savedAt: Long
)
