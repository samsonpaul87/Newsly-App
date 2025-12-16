package com.newsly.app.data.remote.mapper

import com.newsly.app.data.local.database.BookmarkEntity
import com.newsly.app.data.remote.dto.ArticleDto
import com.newsly.app.domain.model.Article
import java.security.MessageDigest

/**
 * Mapper functions to convert between DTOs, entities, and domain models.
 */
object ArticleMapper {

    /**
     * Converts an ArticleDto from the API to a domain Article model.
     */
    fun ArticleDto.toDomain(): Article? {
        // Skip articles with missing required fields
        if (title.isNullOrBlank() || url.isNullOrBlank()) return null

        // Skip removed articles
        if (title == "[Removed]") return null

        return Article(
            id = generateId(url),
            title = title.trim(),
            description = description?.trim() ?: "",
            content = content?.trim() ?: description?.trim() ?: "",
            author = author?.trim() ?: "",
            sourceName = source?.name?.trim() ?: "Unknown",
            url = url,
            imageUrl = urlToImage?.takeIf { it.isNotBlank() },
            publishedAt = publishedAt ?: "",
            isBookmarked = false
        )
    }

    /**
     * Converts a BookmarkEntity from Room to a domain Article model.
     */
    fun BookmarkEntity.toDomain(): Article {
        return Article(
            id = id,
            title = title,
            description = description,
            content = content,
            author = author,
            sourceName = sourceName,
            url = url,
            imageUrl = imageUrl,
            publishedAt = publishedAt,
            isBookmarked = true
        )
    }

    /**
     * Converts a domain Article model to a BookmarkEntity for Room storage.
     */
    fun Article.toEntity(): BookmarkEntity {
        return BookmarkEntity(
            id = id,
            title = title,
            description = description,
            content = content,
            author = author,
            sourceName = sourceName,
            url = url,
            imageUrl = imageUrl,
            publishedAt = publishedAt,
            savedAt = System.currentTimeMillis()
        )
    }

    /**
     * Generates a unique ID for an article based on its URL.
     */
    private fun generateId(url: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(url.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
