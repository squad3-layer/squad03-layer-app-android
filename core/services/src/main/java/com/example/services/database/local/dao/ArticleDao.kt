package com.example.services.database.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.services.database.local.entity.ArticleEntity

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Query("""
        SELECT * FROM articles 
        WHERE (:category IS NULL OR category = :category)
        AND (:query IS NULL OR query = :query)
        ORDER BY publishedAt DESC
    """)
    fun getArticlesPaging(
        category: String?,
        query: String?
    ): PagingSource<Int, ArticleEntity>

    @Query("""
        SELECT * FROM articles 
        WHERE (:category IS NULL OR category = :category)
        AND (:query IS NULL OR query = :query)
        ORDER BY publishedAt ASC
    """)
    fun getArticlesPagingReversed(
        category: String?,
        query: String?
    ): PagingSource<Int, ArticleEntity>


    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Query("DELETE FROM articles WHERE cachedAt < :timestamp")
    suspend fun deleteOldArticles(timestamp: Long)

    @Query("""
        SELECT * FROM articles 
        WHERE (:category IS NULL OR category = :category)
        AND (:query IS NULL OR query = :query)
        LIMIT 1
    """)
    suspend fun getArticlesByFilter(category: String?, query: String?): ArticleEntity?
}

