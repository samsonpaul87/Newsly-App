package com.newsly.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.newsly.app.domain.model.Article
import com.newsly.app.domain.usecase.GetTopHeadlinesUseCase
import com.newsly.app.domain.usecase.SearchNewsUseCase
import com.newsly.app.domain.usecase.ToggleBookmarkUseCase
import com.newsly.app.presentation.state.HomeUiState
import com.newsly.app.presentation.state.UiEvent
import com.newsly.app.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getTopHeadlinesUseCase: GetTopHeadlinesUseCase

    @Mock
    private lateinit var searchNewsUseCase: SearchNewsUseCase

    @Mock
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    private lateinit var viewModel: HomeViewModel

    private val testArticles = listOf(
        Article(
            id = "1",
            title = "Test Article 1",
            description = "Description 1",
            content = "Content 1",
            author = "Author 1",
            sourceName = "Source 1",
            url = "https://example.com/1",
            imageUrl = "https://example.com/image1.jpg",
            publishedAt = "2024-12-15T10:00:00Z",
            isBookmarked = false
        ),
        Article(
            id = "2",
            title = "Test Article 2",
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
    fun `loadTopHeadlines success updates state with articles`() = runTest {
        // Given
        whenever(getTopHeadlinesUseCase.invoke()).thenReturn(Result.success(testArticles))

        // When
        viewModel = HomeViewModel(getTopHeadlinesUseCase, searchNewsUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, (state as HomeUiState.Success).articles.size)
        }
    }

    @Test
    fun `loadTopHeadlines failure updates state with error`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(getTopHeadlinesUseCase.invoke()).thenReturn(Result.failure(Exception(errorMessage)))

        // When
        viewModel = HomeViewModel(getTopHeadlinesUseCase, searchNewsUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            assertEquals(errorMessage, (state as HomeUiState.Error).message)
        }
    }

    @Test
    fun `loadTopHeadlines with empty list updates state to Empty`() = runTest {
        // Given
        whenever(getTopHeadlinesUseCase.invoke()).thenReturn(Result.success(emptyList()))

        // When
        viewModel = HomeViewModel(getTopHeadlinesUseCase, searchNewsUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Empty)
        }
    }

    @Test
    fun `toggleBookmark emits snackbar event`() = runTest {
        // Given
        whenever(getTopHeadlinesUseCase.invoke()).thenReturn(Result.success(testArticles))
        whenever(toggleBookmarkUseCase.invoke(any())).thenReturn(true)

        viewModel = HomeViewModel(getTopHeadlinesUseCase, searchNewsUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.events.test {
            val uiState = viewModel.uiState.value
            if (uiState is HomeUiState.Success) {
                viewModel.toggleBookmark(uiState.articles.first())
                testDispatcher.scheduler.advanceUntilIdle()

                // Then
                val event = awaitItem()
                assertTrue(event is UiEvent.ShowSnackbar)
                assertEquals("Article bookmarked", (event as UiEvent.ShowSnackbar).message)
            }
        }
    }

    @Test
    fun `onArticleClick emits NavigateToDetail event`() = runTest {
        // Given
        whenever(getTopHeadlinesUseCase.invoke()).thenReturn(Result.success(testArticles))

        viewModel = HomeViewModel(getTopHeadlinesUseCase, searchNewsUseCase, toggleBookmarkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.events.test {
            val uiState = viewModel.uiState.value
            if (uiState is HomeUiState.Success) {
                viewModel.onArticleClick(uiState.articles.first())
                testDispatcher.scheduler.advanceUntilIdle()

                // Then
                val event = awaitItem()
                assertTrue(event is UiEvent.NavigateToDetail)
            }
        }
    }
}
