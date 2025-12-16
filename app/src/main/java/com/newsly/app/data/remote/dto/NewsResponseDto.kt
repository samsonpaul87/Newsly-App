package com.newsly.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO representing the response from NewsAPI top headlines and search endpoints.
 */
data class NewsResponseDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("articles")
    val articles: List<ArticleDto>
)

/**
 * DTO representing a single news article from the API.
 */
data class ArticleDto(
    @SerializedName("source")
    val source: SourceDto?,
    @SerializedName("author")
    val author: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("urlToImage")
    val urlToImage: String?,
    @SerializedName("publishedAt")
    val publishedAt: String?,
    @SerializedName("content")
    val content: String?
)

/**
 * DTO representing the source of an article.
 */
data class SourceDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)
