package com.newsly.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsly.app.domain.usecase.GetTopHeadlinesUseCase
import com.newsly.app.domain.usecase.SearchNewsUseCase
import com.newsly.app.domain.usecase.ToggleBookmarkUseCase
import com.newsly.app.presentation.state.ArticleUiMapper.toDomain
import com.newsly.app.presentation.state.ArticleUiMapper.toUiModel
import com.newsly.app.presentation.state.ArticleUiModel
import com.newsly.app.presentation.state.HomeUiState
import com.newsly.app.presentation.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val searchNewsUseCase: SearchNewsUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var searchJob: Job? = null
    private var currentArticles: List<ArticleUiModel> = emptyList()

    init {
        loadTopHeadlines()
    }

    /**
     * Loads top headlines from the API.
     */
    fun loadTopHeadlines() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            _searchQuery.value = ""

            getTopHeadlinesUseCase()
                .onSuccess { articles ->
                    currentArticles = articles.map { it.toUiModel() }
                    _uiState.value = if (currentArticles.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(currentArticles)
                    }
                }
                .onFailure { error ->
                    val isNetworkError = error is UnknownHostException
                    _uiState.value = HomeUiState.Error(
                        message = error.message ?: "Unknown error occurred",
                        isNetworkError = isNetworkError
                    )
                }
        }
    }

    /**
     * Refreshes the current content (pull-to-refresh).
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val result = if (_searchQuery.value.isBlank()) {
                getTopHeadlinesUseCase()
            } else {
                searchNewsUseCase(_searchQuery.value)
            }

            result
                .onSuccess { articles ->
                    currentArticles = articles.map { it.toUiModel() }
                    _uiState.value = if (currentArticles.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(currentArticles)
                    }
                }
                .onFailure { error ->
                    _events.emit(UiEvent.ShowSnackbar(error.message ?: "Failed to refresh"))
                }

            _isRefreshing.value = false
        }
    }

    /**
     * Updates search query and triggers search after debounce.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            loadTopHeadlines()
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)  // Debounce
            searchNews(query)
        }
    }

    /**
     * Searches for news articles.
     */
    private suspend fun searchNews(query: String) {
        _uiState.value = HomeUiState.Loading

        searchNewsUseCase(query)
            .onSuccess { articles ->
                currentArticles = articles.map { it.toUiModel() }
                _uiState.value = if (currentArticles.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    HomeUiState.Success(currentArticles)
                }
            }
            .onFailure { error ->
                val isNetworkError = error is UnknownHostException
                _uiState.value = HomeUiState.Error(
                    message = error.message ?: "Search failed",
                    isNetworkError = isNetworkError
                )
            }
    }

    /**
     * Toggles bookmark status for an article.
     */
    fun toggleBookmark(article: ArticleUiModel) {
        viewModelScope.launch {
            val isNowBookmarked = toggleBookmarkUseCase(article.toDomain())

            // Update local list
            currentArticles = currentArticles.map {
                if (it.id == article.id) it.copy(isBookmarked = isNowBookmarked) else it
            }

            // Update UI state
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                _uiState.value = HomeUiState.Success(currentArticles)
            }

            // Show feedback
            val message = if (isNowBookmarked) "Article bookmarked" else "Bookmark removed"
            _events.emit(UiEvent.ShowSnackbar(message))
        }
    }

    /**
     * Handles article click - navigates to detail.
     */
    fun onArticleClick(article: ArticleUiModel) {
        viewModelScope.launch {
            _events.emit(UiEvent.NavigateToDetail(article))
        }
    }
}
