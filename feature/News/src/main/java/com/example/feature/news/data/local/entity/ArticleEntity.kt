package com.example.feature.news.data.local.entity

import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.model.Source
import com.example.services.database.local.entity.ArticleEntity

fun ArticleEntity.toDomain(): Article {
    return Article(
        source = Source(id = sourceId, name = sourceName),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

fun Article.toEntity(category: String? = null, query: String? = null): ArticleEntity {
    return ArticleEntity(
        id = "${url}_${publishedAt}",
        sourceId = source?.id,
        sourceName = source?.name ?: "",
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        category = category,
        query = query
    )
}

