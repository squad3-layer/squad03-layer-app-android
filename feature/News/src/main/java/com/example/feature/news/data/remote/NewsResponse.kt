package com.example.feature.news.data.remote

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsDto>
)
