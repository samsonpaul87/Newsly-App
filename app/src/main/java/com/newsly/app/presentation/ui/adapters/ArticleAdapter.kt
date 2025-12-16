package com.newsly.app.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.newsly.app.R
import com.newsly.app.databinding.ItemArticleBinding
import com.newsly.app.presentation.state.ArticleUiModel

/**
 * RecyclerView adapter for displaying news articles.
 */
class ArticleAdapter(
    private val onArticleClick: (ArticleUiModel) -> Unit,
    private val onBookmarkClick: (ArticleUiModel) -> Unit
) : ListAdapter<ArticleUiModel, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleViewHolder(
        private val binding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cardArticle.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onArticleClick(getItem(position))
                }
            }

            binding.btnBookmark.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onBookmarkClick(getItem(position))
                }
            }
        }

        fun bind(article: ArticleUiModel) {
            binding.apply {
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvSource.text = article.sourceName
                tvDate.text = article.formattedDate

                // Load image with Coil
                if (article.imageUrl != null) {
                    ivArticleImage.load(article.imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.placeholder_image)
                        error(R.drawable.placeholder_image)
                        transformations(RoundedCornersTransformation(0f))
                    }
                } else {
                    ivArticleImage.setImageResource(R.drawable.placeholder_image)
                }

                // Update bookmark icon
                val bookmarkIcon = if (article.isBookmarked) {
                    R.drawable.ic_bookmark
                } else {
                    R.drawable.ic_bookmark_outline
                }
                btnBookmark.setImageResource(bookmarkIcon)
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    class ArticleDiffCallback : DiffUtil.ItemCallback<ArticleUiModel>() {
        override fun areItemsTheSame(oldItem: ArticleUiModel, newItem: ArticleUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ArticleUiModel, newItem: ArticleUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
