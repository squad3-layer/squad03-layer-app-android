package com.example.feature.news.domain.repository

import com.example.feature.news.domain.model.News

interface NewsRepository {
    suspend fun getTopHeadlines(): List<News>
}

