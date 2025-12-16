package com.newsly.app.domain.usecase

import com.newsly.app.domain.model.Article
import com.newsly.app.domain.repository.BookmarkRepository
import javax.inject.Inject

/**
 * Use case for toggling bookmark status of an article.
 */
class ToggleBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    /**
     * Toggles the bookmark status of an article.
     * @return true if now bookmarked, false if bookmark was removed
     */
    suspend operator fun invoke(article: Article): Boolean {
        return bookmarkRepository.toggleBookmark(article)
    }
}
