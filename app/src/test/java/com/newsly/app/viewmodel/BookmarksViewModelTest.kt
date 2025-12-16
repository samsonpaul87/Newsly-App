package com.newsly.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.newsly.app.domain.model.Article
import com.newsly.app.domain.usecase.GetBookmarksUseCase
import com.newsly.app.domain.usecase.ToggleBookmarkUseCase
import com.newsly.app.presentation.state.BookmarksUiState
import com.newsly.app.presentation.state.UiEvent
import com.newsly.app.presentation.viewmodel.BookmarksViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarksViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getBookmarksUseCase: GetBookmarksUseCase

    @Mock
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    private lateinit var viewModel: BookmarksViewModel

    private val testBookmarks = listOf(
        Article(
            id = "1",
            title = "Bookmarked Article 1",
            description = "Description 1",
            content = "Content 1",
            author = "Author 1",
            sourceName = "Source 1",
            url = "https://example.com/1",
            imageUrl = "https://example.com/image1.jpg",
            publishedAt = "2024-12-15T10:00:00Z",
            isBookmarked = true
        ),
        Article(
            id = "2",
            title = "Bookmarked Article 2",
            description = "Description 2",
            content = "Content 2",
            author = "Author 2",
            sourceName = "Source 2",
            url = "https://example.com/2",
            imageUrl = "https://example.com/image2.jpg",
            publishedAt = "2024-12-15T11:00:00Z",
            isBookmarked = true
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows bookmarks when available`() = runTest {
        // Given
        whenever(getBookmarksUseCase.invoke()).thenReturn(flowOf(testBookmarks))

        // When
        viewModel = BookmarksViewModel(getBookmarksUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BookmarksUiState.Success)
            assertEquals(2, (state as BookmarksUiState.Success).bookmarks.size)
        }
    }

    @Test
    fun `empty bookmarks shows Empty state`() = runTest {
        // Given
        whenever(getBookmarksUseCase.invoke()).thenReturn(flowOf(emptyList()))

        // When
        viewModel = BookmarksViewModel(getBookmarksUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BookmarksUiState.Empty)
        }
    }

    @Test
    fun `removeBookmark emits snackbar event`() = runTest {
        // Given
        whenever(getBookmarksUseCase.invoke()).thenReturn(flowOf(testBookmarks))
        whenever(toggleBookmarkUseCase.invoke(any())).thenReturn(false)

        viewModel = BookmarksViewModel(getBookmarksUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.events.test {
            val uiState = viewModel.uiState.value
            if (uiState is BookmarksUiState.Success) {
                viewModel.removeBookmark(uiState.bookmarks.first())
                testDispatcher.scheduler.advanceUntilIdle()

                // Then
                val event = awaitItem()
                assertTrue(event is UiEvent.ShowSnackbar)
                assertEquals("Bookmark removed", (event as UiEvent.ShowSnackbar).message)
            }
        }
    }

    @Test
    fun `onArticleClick emits NavigateToDetail event`() = runTest {
        // Given
        whenever(getBookmarksUseCase.invoke()).thenReturn(flowOf(testBookmarks))

        viewModel = BookmarksViewModel(getBookmarksUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.events.test {
            val uiState = viewModel.uiState.value
            if (uiState is BookmarksUiState.Success) {
                viewModel.onArticleClick(uiState.bookmarks.first())
                testDispatcher.scheduler.advanceUntilIdle()

                // Then
                val event = awaitItem()
                assertTrue(event is UiEvent.NavigateToDetail)
            }
        }
    }
}
