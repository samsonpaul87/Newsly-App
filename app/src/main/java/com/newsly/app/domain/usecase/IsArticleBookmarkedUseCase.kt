package com.newsly.app.domain.usecase

import com.newsly.app.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for checking if an article is bookmarked.
 */
class IsArticleBookmarkedUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    /**
     * Returns a Flow that emits true if the article is bookmarked.
     */
    operator fun invoke(articleId: String): Flow<Boolean> {
        return bookmarkRepository.isBookmarkedFlow(articleId)
    }

    /**
     * Checks if an article is bookmarked (one-time check).
     */
    suspend fun check(articleId: String): Boolean {
        return bookmarkRepository.isBookmarked(articleId)
    }
}
