package com.newsly.app.domain.usecase

import com.newsly.app.domain.model.Article
import com.newsly.app.domain.repository.NewsRepository
import javax.inject.Inject

/**
 * Use case for fetching top headlines.
 */
class GetTopHeadlinesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(): Result<List<Article>> {
        return newsRepository.getTopHeadlines()
    }
}
