package com.example.feature.news.domain.model

data class NewsFilters(
    val category: String? = null,
    val shouldReverseOrder: Boolean = false,
    val query: String? = null
)
