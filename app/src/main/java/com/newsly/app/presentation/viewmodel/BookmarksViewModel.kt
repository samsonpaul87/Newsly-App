package com.newsly.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsly.app.domain.usecase.GetBookmarksUseCase
import com.newsly.app.domain.usecase.ToggleBookmarkUseCase
import com.newsly.app.presentation.state.ArticleUiMapper.toDomain
import com.newsly.app.presentation.state.ArticleUiMapper.toUiModel
import com.newsly.app.presentation.state.ArticleUiModel
import com.newsly.app.presentation.state.BookmarksUiState
import com.newsly.app.presentation.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Bookmarks screen.
 */
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarksUiState>(BookmarksUiState.Loading)
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        observeBookmarks()
    }

    /**
     * Observes bookmarks from the database.
     */
    private fun observeBookmarks() {
        getBookmarksUseCase()
            .onEach { bookmarks ->
                val uiModels = bookmarks.map { it.toUiModel() }
                _uiState.value = if (uiModels.isEmpty()) {
                    BookmarksUiState.Empty
                } else {
                    BookmarksUiState.Success(uiModels)
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Removes a bookmark.
     */
    fun removeBookmark(article: ArticleUiModel) {
        viewModelScope.launch {
            toggleBookmarkUseCase(article.toDomain())
            _events.emit(UiEvent.ShowSnackbar("Bookmark removed"))
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
