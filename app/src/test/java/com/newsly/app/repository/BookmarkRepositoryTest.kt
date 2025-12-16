package com.newsly.app.repository

import app.cash.turbine.test
import com.newsly.app.data.local.database.BookmarkDao
import com.newsly.app.data.local.database.BookmarkEntity
import com.newsly.app.data.repository.BookmarkRepositoryImpl
import com.newsly.app.domain.model.Article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkRepositoryTest {

    @Mock
    private lateinit var bookmarkDao: BookmarkDao

    private lateinit var repository: BookmarkRepositoryImpl

    private val testArticle = Article(
        id = "test-id",
        title = "Test Article",
        description = "Test description",
        content = "Test content",
        author = "Test Author",
        sourceName = "Test Source",
        url = "https://example.com",
        imageUrl = "https://example.com/image.jpg",
        publishedAt = "2024-12-15T10:00:00Z",
        isBookmarked = false
    )

    private val testBookmarkEntity = BookmarkEntity(
        id = "test-id",
        title = "Test Article",
        description = "Test description",
        content = "Test content",
        author = "Test Author",
        sourceName = "Test Source",
        url = "https://example.com",
        imageUrl = "https://example.com/image.jpg",
        publishedAt = "2024-12-15T10:00:00Z",
        savedAt = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = BookmarkRepositoryImpl(bookmarkDao)
    }

    @Test
    fun `getBookmarks returns flow of articles`() = runTest {
        // Given
        whenever(bookmarkDao.getAllBookmarks()).thenReturn(flowOf(listOf(testBookmarkEntity)))

        // When & Then
        repository.getBookmarks().test {
            val bookmarks = awaitItem()
            assertEquals(1, bookmarks.size)
            assertEquals("Test Article", bookmarks.first().title)
            assertTrue(bookmarks.first().isBookmarked)
            awaitComplete()
        }
    }

    @Test
    fun `getBookmarks returns empty flow when no bookmarks`() = runTest {
        // Given
        whenever(bookmarkDao.getAllBookmarks()).thenReturn(flowOf(emptyList()))

        // When & Then
        repository.getBookmarks().test {
            val bookmarks = awaitItem()
            assertTrue(bookmarks.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `isBookmarked returns true when article is bookmarked`() = runTest {
        // Given
        whenever(bookmarkDao.isBookmarked("test-id")).thenReturn(true)

        // When
        val result = repository.isBookmarked("test-id")

        // Then
        assertTrue(result)
    }

    @Test
    fun `isBookmarked returns false when article is not bookmarked`() = runTest {
        // Given
        whenever(bookmarkDao.isBookmarked("test-id")).thenReturn(false)

        // When
        val result = repository.isBookmarked("test-id")

        // Then
        assertFalse(result)
    }

    @Test
    fun `isBookmarkedFlow returns correct flow`() = runTest {
        // Given
        whenever(bookmarkDao.isBookmarkedFlow("test-id")).thenReturn(flowOf(true))

        // When & Then
        repository.isBookmarkedFlow("test-id").test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `addBookmark inserts entity into database`() = runTest {
        // When
        repository.addBookmark(testArticle)

        // Then
        verify(bookmarkDao).insertBookmark(any())
    }

    @Test
    fun `removeBookmark deletes entity from database`() = runTest {
        // When
        repository.removeBookmark("test-id")

        // Then
        verify(bookmarkDao).deleteBookmarkById("test-id")
    }

    @Test
    fun `toggleBookmark adds bookmark when not bookmarked`() = runTest {
        // Given
        whenever(bookmarkDao.isBookmarked("test-id")).thenReturn(false)

        // When
        val result = repository.toggleBookmark(testArticle)

        // Then
        assertTrue(result)
        verify(bookmarkDao).insertBookmark(any())
    }

    @Test
    fun `toggleBookmark removes bookmark when already bookmarked`() = runTest {
        // Given
        whenever(bookmarkDao.isBookmarked("test-id")).thenReturn(true)

        // When
        val result = repository.toggleBookmark(testArticle)

        // Then
        assertFalse(result)
        verify(bookmarkDao).deleteBookmarkById("test-id")
    }
}
