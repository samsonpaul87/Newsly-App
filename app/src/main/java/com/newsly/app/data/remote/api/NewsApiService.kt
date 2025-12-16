package com.newsly.app.data.remote.api

import com.newsly.app.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for NewsAPI.org endpoints.
 */
interface NewsApiService {

    /**
     * Fetches top headlines for a specific country.
     *
     * @param country ISO 3166-1 country code (default: "us")
     * @param apiKey NewsAPI key
     * @param pageSize Number of results per page
     */
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponseDto

    /**
     * Searches for news articles by keyword.
     *
     * @param query Search keyword
     * @param apiKey NewsAPI key
     * @param pageSize Number of results per page
     * @param sortBy Sort order (relevancy, popularity, publishedAt)
     */
    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy") sortBy: String = "publishedAt"
    ): NewsResponseDto

    companion object {
        const val BASE_URL = "https://newsapi.org/"
    }
}
