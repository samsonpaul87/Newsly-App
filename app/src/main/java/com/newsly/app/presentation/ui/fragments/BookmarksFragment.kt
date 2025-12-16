package com.newsly.app.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.newsly.app.databinding.FragmentBookmarksBinding
import com.newsly.app.presentation.state.BookmarksUiState
import com.newsly.app.presentation.state.UiEvent
import com.newsly.app.presentation.ui.activities.ArticleDetailActivity
import com.newsly.app.presentation.ui.adapters.ArticleAdapter
import com.newsly.app.presentation.viewmodel.BookmarksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment displaying bookmarked articles.
 */
@AndroidEntryPoint
class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookmarksViewModel by viewModels()

    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(
            onArticleClick = { article ->
                viewModel.onArticleClick(article)
            },
            onBookmarkClick = { article ->
                viewModel.removeBookmark(article)
            }
        )

        binding.rvBookmarks.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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

    private fun handleUiState(state: BookmarksUiState) {
        // Hide all state views first
        binding.loadingContainer.visibility = View.GONE
        binding.emptyContainer.visibility = View.GONE
        binding.rvBookmarks.visibility = View.GONE

        when (state) {
            is BookmarksUiState.Loading -> {
                binding.loadingContainer.visibility = View.VISIBLE
            }
            is BookmarksUiState.Success -> {
                binding.rvBookmarks.visibility = View.VISIBLE
                articleAdapter.submitList(state.bookmarks)
            }
            is BookmarksUiState.Empty -> {
                binding.emptyContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowSnackbar -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is UiEvent.NavigateToDetail -> {
                val intent = ArticleDetailActivity.newIntent(requireContext(), event.article)
                startActivity(intent)
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
