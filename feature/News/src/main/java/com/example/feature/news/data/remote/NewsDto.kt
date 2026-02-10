package com.example.feature.news.data.remote

import com.example.feature.news.domain.model.News

data class NewsDto(
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String
)


fun NewsDto.toDomain(): News {
    return News(
        id = 0, // pode ser gerado internamente
        title = title,
        description = description ?: "",
        imageUrl = urlToImage ?: "",
        date = publishedAt
    )
}
