package com.newsly.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsly.app.domain.usecase.IsArticleBookmarkedUseCase
import com.newsly.app.domain.usecase.ToggleBookmarkUseCase
import com.newsly.app.presentation.state.ArticleUiMapper.toDomain
import com.newsly.app.presentation.state.ArticleUiModel
import com.newsly.app.presentation.state.DetailUiState
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
 * ViewModel for the Article Detail screen.
 */
@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val isArticleBookmarkedUseCase: IsArticleBookmarkedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private var currentArticle: ArticleUiModel? = null

    /**
     * Sets the article to display.
     */
    fun setArticle(article: ArticleUiModel) {
        currentArticle = article
        observeBookmarkStatus(article.id)
    }

    /**
     * Observes bookmark status for the current article.
     */
    private fun observeBookmarkStatus(articleId: String) {
        isArticleBookmarkedUseCase(articleId)
            .onEach { isBookmarked ->
                currentArticle?.let { article ->
                    _uiState.value = DetailUiState.Success(
                        article = article.copy(isBookmarked = isBookmarked),
                        isBookmarked = isBookmarked
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Toggles bookmark status for the current article.
     */
    fun toggleBookmark() {
        viewModelScope.launch {
            currentArticle?.let { article ->
                val isNowBookmarked = toggleBookmarkUseCase(article.toDomain())
                currentArticle = article.copy(isBookmarked = isNowBookmarked)

                val message = if (isNowBookmarked) "Article bookmarked" else "Bookmark removed"
                _events.emit(UiEvent.ShowSnackbar(message))
            }
        }
    }

    /**
     * Gets the current article URL for sharing/opening.
     */
    fun getArticleUrl(): String? = currentArticle?.url

    /**
     * Gets the current article for sharing.
     */
    fun getShareContent(): Pair<String, String>? {
        return currentArticle?.let { article ->
            Pair(article.title, article.url)
        }
    }
}
