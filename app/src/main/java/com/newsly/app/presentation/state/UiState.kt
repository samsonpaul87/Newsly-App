package com.newsly.app.presentation.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Generic sealed class representing UI states.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val isNetworkError: Boolean = false) : UiState<Nothing>()
}

/**
 * Sealed class for home screen UI states.
 */
sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val articles: List<ArticleUiModel>) : HomeUiState()
    data class Error(val message: String, val isNetworkError: Boolean = false) : HomeUiState()
    data object Empty : HomeUiState()
}

/**
 * Sealed class for bookmarks screen UI states.
 */
sealed class BookmarksUiState {
    data object Loading : BookmarksUiState()
    data class Success(val bookmarks: List<ArticleUiModel>) : BookmarksUiState()
    data object Empty : BookmarksUiState()
}

/**
 * Sealed class for article detail UI states.
 */
sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val article: ArticleUiModel, val isBookmarked: Boolean) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

/**
 * UI model for displaying an article in the UI.
 */
@Parcelize
data class ArticleUiModel(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val author: String,
    val sourceName: String,
    val url: String,
    val imageUrl: String?,
    val formattedDate: String,
    val isBookmarked: Boolean
) : Parcelable {
    companion object {
        val EMPTY = ArticleUiModel(
            id = "",
            title = "",
            description = "",
            content = "",
            author = "",
            sourceName = "",
            url = "",
            imageUrl = null,
            formattedDate = "",
            isBookmarked = false
        )
    }
}

/**
 * One-time UI events (snackbar, navigation, etc.)
 */
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class NavigateToDetail(val article: ArticleUiModel) : UiEvent()
    data object NavigateBack : UiEvent()
}
