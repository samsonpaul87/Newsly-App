package com.newsly.app.domain.usecase

import com.newsly.app.domain.model.Article
import com.newsly.app.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all bookmarked articles.
 */
class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<Article>> {
        return bookmarkRepository.getBookmarks()
    }
}
