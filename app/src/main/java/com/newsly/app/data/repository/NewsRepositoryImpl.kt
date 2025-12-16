package com.newsly.app.data.repository

import com.newsly.app.BuildConfig
import com.newsly.app.data.local.database.BookmarkDao
import com.newsly.app.data.remote.api.NewsApiService
import com.newsly.app.data.remote.mapper.ArticleMapper.toDomain
import com.newsly.app.domain.model.Article
import com.newsly.app.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NewsRepository that fetches news from the API.
 */
@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val bookmarkDao: BookmarkDao
) : NewsRepository {

    override suspend fun getTopHeadlines(): Result<List<Article>> = withContext(Dispatchers.IO) {
        try {
            val response = newsApiService.getTopHeadlines(
                apiKey = BuildConfig.NEWS_API_KEY
            )

            if (response.status != "ok") {
                return@withContext Result.failure(Exception("API returned error status"))
            }

            val bookmarkIds = bookmarkDao.getAllBookmarkIds().toSet()
            val articles = response.articles
                .mapNotNull { it.toDomain() }
                .map { article ->
                    article.copy(isBookmarked = article.id in bookmarkIds)
                }

            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchNews(query: String): Result<List<Article>> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                return@withContext Result.success(emptyList())
            }

            val response = newsApiService.searchNews(
                query = query,
                apiKey = BuildConfig.NEWS_API_KEY
            )

            if (response.status != "ok") {
                return@withContext Result.failure(Exception("API returned error status"))
            }

            val bookmarkIds = bookmarkDao.getAllBookmarkIds().toSet()
            val articles = response.articles
                .mapNotNull { it.toDomain() }
                .map { article ->
                    article.copy(isBookmarked = article.id in bookmarkIds)
                }

            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
