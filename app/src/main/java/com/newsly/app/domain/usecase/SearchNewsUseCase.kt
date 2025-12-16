package com.newsly.app.domain.usecase

import com.newsly.app.domain.model.Article
import com.newsly.app.domain.repository.NewsRepository
import javax.inject.Inject

/**
 * Use case for searching news articles.
 */
class SearchNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(query: String): Result<List<Article>> {
        return newsRepository.searchNews(query.trim())
    }
}
