package com.newsly.app.data.repository

import com.newsly.app.data.local.database.BookmarkDao
import com.newsly.app.data.remote.mapper.ArticleMapper.toDomain
import com.newsly.app.data.remote.mapper.ArticleMapper.toEntity
import com.newsly.app.domain.model.Article
import com.newsly.app.domain.repository.BookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of BookmarkRepository using Room database.
 */
@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override fun getBookmarks(): Flow<List<Article>> {
        return bookmarkDao.getAllBookmarks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun isBookmarked(articleId: String): Boolean = withContext(Dispatchers.IO) {
        bookmarkDao.isBookmarked(articleId)
    }

    override fun isBookmarkedFlow(articleId: String): Flow<Boolean> {
        return bookmarkDao.isBookmarkedFlow(articleId)
    }

    override suspend fun addBookmark(article: Article) = withContext(Dispatchers.IO) {
        bookmarkDao.insertBookmark(article.toEntity())
    }

    override suspend fun removeBookmark(articleId: String) = withContext(Dispatchers.IO) {
        bookmarkDao.deleteBookmarkById(articleId)
    }

    override suspend fun toggleBookmark(article: Article): Boolean = withContext(Dispatchers.IO) {
        val isCurrentlyBookmarked = bookmarkDao.isBookmarked(article.id)
        if (isCurrentlyBookmarked) {
            bookmarkDao.deleteBookmarkById(article.id)
        } else {
            bookmarkDao.insertBookmark(article.toEntity())
        }
        !isCurrentlyBookmarked
    }
}
