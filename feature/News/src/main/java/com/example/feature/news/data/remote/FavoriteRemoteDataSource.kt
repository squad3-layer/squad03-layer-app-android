package com.example.feature.news.data.remote

import com.example.feature.news.domain.model.Article
import com.example.services.database.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FavoritesRemoteDataSource @Inject constructor(
    private val firestoreService: FirestoreService,
    private val auth: FirebaseAuth
) {
    private companion object {
        const val USERS_COLLECTION = "users"
        const val FAVORITES_COLLECTION = "favorites"
    }

    private fun getUserId(): String? = auth.currentUser?.uid

    private fun articleToMap(article: Article): Map<String, Any> {
        return mapOf(
            "title" to article.title,
            "description" to (article.description ?: ""),
            "url" to article.url,
            "urlToImage" to (article.urlToImage ?: ""),
            "publishedAt" to article.publishedAt,
            "author" to (article.author ?: ""),
            "content" to (article.content ?: ""),
            "source" to mapOf(
                "id" to (article.source?.id ?: ""),
                "name" to (article.source?.name ?: "")
            )
        )
    }

    private fun mapToArticle(data: Map<String, Any>): Article {
        val sourceMap = data["source"] as? Map<*, *>
        return Article(
            source = if (sourceMap != null) {
                com.example.feature.news.domain.model.Source(
                    id = sourceMap["id"] as? String,
                    name = sourceMap["name"] as? String ?: ""
                )
            } else null,
            author = data["author"] as? String,
            title = data["title"] as? String ?: "",
            description = data["description"] as? String,
            url = data["url"] as? String ?: "",
            urlToImage = data["urlToImage"] as? String,
            publishedAt = data["publishedAt"] as? String ?: "",
            content = data["content"] as? String
        )
    }

    suspend fun addFavorite(article: Article): Result<Unit> {
        val userId = getUserId() ?: return Result.failure(Exception("User not authenticated"))
        val documentId = article.url.hashCode().toString()
        val path = "$USERS_COLLECTION/$userId/$FAVORITES_COLLECTION"

        return firestoreService.saveDocument(
            collection = path,
            document = documentId,
            data = articleToMap(article)
        )
    }

    suspend fun removeFavorite(articleUrl: String): Result<Unit> {
        val userId = getUserId() ?: return Result.failure(Exception("User not authenticated"))
        val documentId = articleUrl.hashCode().toString()
        val path = "$USERS_COLLECTION/$userId/$FAVORITES_COLLECTION"

        return firestoreService.deleteDocument(
            collection = path,
            document = documentId
        )
    }

    suspend fun getFavorites(): Result<List<Article>> {
        val userId = getUserId() ?: return Result.failure(Exception("User not authenticated"))
        val path = "$USERS_COLLECTION/$userId/$FAVORITES_COLLECTION"

        return firestoreService.getCollection(path).map { documents ->
            documents.mapNotNull { data ->
                try {
                    mapToArticle(data)
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    suspend fun isFavorite(articleUrl: String): Result<Boolean> {
        val userId = getUserId() ?: return Result.failure(Exception("User not authenticated"))
        val documentId = articleUrl.hashCode().toString()
        val path = "$USERS_COLLECTION/$userId/$FAVORITES_COLLECTION"

        return firestoreService.getDocument(path, documentId).map { data ->
            data.isNotEmpty()
        }
    }
}
