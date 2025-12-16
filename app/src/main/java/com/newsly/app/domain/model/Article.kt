package com.newsly.app.domain.model

/**
 * Domain model representing a news article.
 * This model is independent of data sources and used throughout the app.
 */
data class Article(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val author: String,
    val sourceName: String,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val isBookmarked: Boolean = false
)
