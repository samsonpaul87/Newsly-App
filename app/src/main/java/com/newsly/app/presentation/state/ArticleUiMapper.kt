package com.newsly.app.presentation.state

import com.newsly.app.domain.model.Article
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Mapper to convert domain Article to UI model.
 */
object ArticleUiMapper {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    /**
     * Converts a domain Article to ArticleUiModel.
     */
    fun Article.toUiModel(): ArticleUiModel {
        return ArticleUiModel(
            id = id,
            title = title,
            description = description,
            content = content,
            author = author,
            sourceName = sourceName,
            url = url,
            imageUrl = imageUrl,
            formattedDate = formatDate(publishedAt),
            isBookmarked = isBookmarked
        )
    }

    /**
     * Converts ArticleUiModel back to domain Article.
     */
    fun ArticleUiModel.toDomain(): Article {
        return Article(
            id = id,
            title = title,
            description = description,
            content = content,
            author = author,
            sourceName = sourceName,
            url = url,
            imageUrl = imageUrl,
            publishedAt = "",  // Original date not preserved in UI model
            isBookmarked = isBookmarked
        )
    }

    /**
     * Formats ISO 8601 date string to user-friendly format.
     */
    private fun formatDate(isoDate: String): String {
        return try {
            val date = inputFormat.parse(isoDate)
            date?.let { outputFormat.format(it) } ?: ""
        } catch (e: Exception) {
            isoDate.take(10)  // Fallback: just show date part
        }
    }
}
