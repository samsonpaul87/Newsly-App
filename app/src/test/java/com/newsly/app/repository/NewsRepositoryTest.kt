package com.newsly.app.repository

import com.newsly.app.data.local.database.BookmarkDao
import com.newsly.app.data.remote.api.NewsApiService
import com.newsly.app.data.remote.dto.ArticleDto
import com.newsly.app.data.remote.dto.NewsResponseDto
import com.newsly.app.data.remote.dto.SourceDto
import com.newsly.app.data.repository.NewsRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryTest {

    @Mock
    private lateinit var newsApiService: NewsApiService

    @Mock
    private lateinit var bookmarkDao: BookmarkDao

    private lateinit var repository: NewsRepositoryImpl

    private val testArticleDto = ArticleDto(
        source = SourceDto(id = "bbc", name = "BBC News"),
        author = "John Doe",
        title = "Test Article Title",
        description = "Test article description",
        url = "https://example.com/article",
        urlToImage = "https://example.com/image.jpg",
        publishedAt = "2024-12-15T10:00:00Z",
        content = "Full article content here"
    )

    private val testNewsResponse = NewsResponseDto(
        status = "ok",
        totalResults = 1,
        articles = listOf(testArticleDto)
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = NewsRepositoryImpl(newsApiService, bookmarkDao)
    }

    @Test
    fun `getTopHeadlines returns success with articles`() = runTest {
        // Given
        whenever(newsApiService.getTopHeadlines(any(), any(), any())).thenReturn(testNewsResponse)
        whenever(bookmarkDao.getAllBookmarkIds()).thenReturn(emptyList())

        // When
        val result = repository.getTopHeadlines()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Article Title", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `getTopHeadlines returns failure on error status`() = runTest {
        // Given
        val errorResponse = testNewsResponse.copy(status = "error")
        whenever(newsApiService.getTopHeadlines(any(), any(), any())).thenReturn(errorResponse)
        whenever(bookmarkDao.getAllBookmarkIds()).thenReturn(emptyList())

        // When
        val result = repository.getTopHeadlines()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `getTopHeadlines returns failure on exception`() = runTest {
        // Given
        whenever(newsApiService.getTopHeadlines(any(), any(), any())).thenThrow(RuntimeException("Network error"))
        whenever(bookmarkDao.getAllBookmarkIds()).thenReturn(emptyList())

        // When
        val result = repository.getTopHeadlines()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `getTopHeadlines marks bookmarked articles correctly`() = runTest {
        // Given
        whenever(newsApiService.getTopHeadlines(any(), any(), any())).thenReturn(testNewsResponse)
        val articleId = java.security.MessageDigest.getInstance("MD5")
            .digest(testArticleDto.url!!.toByteArray())
            .joinToString("") { "%02x".format(it) }
        whenever(bookmarkDao.getAllBookmarkIds()).thenReturn(listOf(articleId))

        // When
        val result = repository.getTopHeadlines()

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.first()?.isBookmarked == true)
    }

    @Test
    fun `searchNews returns success with articles`() = runTest {
        // Given
        whenever(newsApiService.searchNews(any(), any(), any(), any())).thenReturn(testNewsResponse)
        whenever(bookmarkDao.getAllBookmarkIds()).thenReturn(emptyList())

        // When
        val result = repository.searchNews("test")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `searchNews with blank query returns empty list`() = runTest {
        // When
        val result = repository.searchNews("   ")

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `articles with removed title are filtered out`() = runTest {
        // Given
        val removedArticle = testArticleDto.copy(title = "[Removed]")
        val response = testNewsResponse.copy(articles = listOf(removedArticle, testArticleDto))
        whenever(newsApiService.getTopHeadlines(any(), any(), any())).thenReturn(response)
        whenever(bookmarkDao.getAllBookmarkIds()).thenReturn(emptyList())

        // When
        val result = repository.getTopHeadlines()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Article Title", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `articles with null title are filtered out`() = runTest {
        // Given
        val nullTitleArticle = testArticleDto.copy(title = null)
        val response = testNewsResponse.copy(articles = listOf(nullTitleArticle, testArticleDto))
        whenever(newsApiService.getTopHeadlines(any(), any(), any())).thenReturn(response)
        whenever(bookmarkDao.getAllBookmarkIds()).thenReturn(emptyList())

        // When
        val result = repository.getTopHeadlines()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
}
