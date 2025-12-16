package com.newsly.app.domain.repository

import com.newsly.app.domain.model.Article
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for news operations.
 * Defines the contract for fetching and managing news articles.
 */
interface NewsRepository {

    /**
     * Fetches top headlines from the network.
     * @return Result containing list of articles or error
     */
    suspend fun getTopHeadlines(): Result<List<Article>>

    /**
     * Searches for articles by keyword.
     * @param query Search keyword
     * @return Result containing list of articles or error
     */
    suspend fun searchNews(query: String): Result<List<Article>>
}

/**
 * Repository interface for bookmark operations.
 */
interface BookmarkRepository {

    /**
     * Returns all bookmarked articles as a Flow.
     */
    fun getBookmarks(): Flow<List<Article>>

    /**
     * Checks if an article is bookmarked.
     */
    suspend fun isBookmarked(articleId: String): Boolean

    /**
     * Checks if an article is bookmarked as a Flow.
     */
    fun isBookmarkedFlow(articleId: String): Flow<Boolean>

    /**
     * Adds an article to bookmarks.
     */
    suspend fun addBookmark(article: Article)

    /**
     * Removes an article from bookmarks.
     */
    suspend fun removeBookmark(articleId: String)

    /**
     * Toggles bookmark status and returns the new status.
     */
    suspend fun toggleBookmark(article: Article): Boolean
}
