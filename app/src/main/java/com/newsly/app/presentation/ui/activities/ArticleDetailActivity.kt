package com.newsly.app.presentation.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.newsly.app.R
import com.newsly.app.databinding.ActivityArticleDetailBinding
import com.newsly.app.presentation.state.ArticleUiModel
import com.newsly.app.presentation.state.DetailUiState
import com.newsly.app.presentation.state.UiEvent
import com.newsly.app.presentation.viewmodel.ArticleDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity for displaying article details.
 */
@AndroidEntryPoint
class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding
    private val viewModel: ArticleDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        observeViewModel()

        // Get article from intent
        val article = intent.getParcelableExtra<ArticleUiModel>(EXTRA_ARTICLE)
            ?: run {
                finish()
                return
            }
        viewModel.setArticle(article)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupClickListeners() {
        binding.btnShare.setOnClickListener {
            shareArticle()
        }

        binding.btnBookmark.setOnClickListener {
            viewModel.toggleBookmark()
        }

        binding.btnOpenBrowser.setOnClickListener {
            openInBrowser()
        }

        binding.btnReadFull.setOnClickListener {
            openInBrowser()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }

                launch {
                    viewModel.events.collect { event ->
                        handleEvent(event)
                    }
                }
            }
        }
    }

    private fun handleUiState(state: DetailUiState) {
        when (state) {
            is DetailUiState.Loading -> {
                // Show loading if needed
            }
            is DetailUiState.Success -> {
                displayArticle(state.article, state.isBookmarked)
            }
            is DetailUiState.Error -> {
                showSnackbar(state.message)
            }
        }
    }

    private fun displayArticle(article: ArticleUiModel, isBookmarked: Boolean) {
        binding.apply {
            // Set title in toolbar
            collapsingToolbar.title = ""
            toolbar.title = ""

            // Load hero image
            if (article.imageUrl != null) {
                ivHero.load(article.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_image)
                    error(R.drawable.placeholder_image)
                }
            } else {
                ivHero.setImageResource(R.drawable.placeholder_image)
            }

            // Set text content
            tvTitle.text = article.title
            tvSource.text = article.sourceName
            tvDate.text = article.formattedDate

            if (article.author.isNotBlank()) {
                tvAuthor.text = getString(R.string.unknown_source).replace("Unknown Source", "By ${article.author}")
                tvAuthor.visibility = android.view.View.VISIBLE
            } else {
                tvAuthor.visibility = android.view.View.GONE
            }

            tvDescription.text = article.description

            if (article.content.isNotBlank() && article.content != article.description) {
                tvContent.text = article.content
                tvContent.visibility = android.view.View.VISIBLE
            } else {
                tvContent.visibility = android.view.View.GONE
            }

            // Update bookmark button
            updateBookmarkButton(isBookmarked)
        }
    }

    private fun updateBookmarkButton(isBookmarked: Boolean) {
        val icon = if (isBookmarked) R.drawable.ic_bookmark else R.drawable.ic_bookmark_outline
        val text = if (isBookmarked) R.string.remove_bookmark else R.string.bookmark
        binding.btnBookmark.setIconResource(icon)
        binding.btnBookmark.setText(text)
    }

    private fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowSnackbar -> showSnackbar(event.message)
            else -> {}
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun shareArticle() {
        viewModel.getShareContent()?.let { (title, url) ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "$title\n\n$url")
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        }
    }

    private fun openInBrowser() {
        viewModel.getArticleUrl()?.let { url ->
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            } catch (e: Exception) {
                showSnackbar("Unable to open browser")
            }
        }
    }

    companion object {
        private const val EXTRA_ARTICLE = "extra_article"

        fun newIntent(context: Context, article: ArticleUiModel): Intent {
            return Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra(EXTRA_ARTICLE, article)
            }
        }
    }
}
