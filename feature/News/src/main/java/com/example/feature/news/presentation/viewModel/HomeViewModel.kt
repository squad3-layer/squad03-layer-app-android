package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.model.NewsFilters
import com.example.feature.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.services.analytics.AnalyticsTags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    val analyticsHelper: AnalyticsTags
) : ViewModel() {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentCategory: String? = null
    private var currentShouldReverse: Boolean = false

    init {
        loadTopHeadlines()
    }

    fun loadTopHeadlines(
        country: String = "br",
        category: String? = null,
        shouldReverseOrder: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            currentCategory = category
            currentShouldReverse = shouldReverseOrder

            val result = getTopHeadlinesUseCase(
                country = country,
                category = category
            )

            result.onSuccess { newsResponse ->
                var articlesList = newsResponse.articles

                // Se shouldReverseOrder for true, inverte a lista baseado nas datas
                if (shouldReverseOrder) {
                    articlesList = sortArticlesByDateAscending(articlesList)
                }

                _articles.value = articlesList
                _error.value = null
            }.onFailure { exception ->
                val errorMessage = "Erro ao carregar notícias. Tente novamente."
                _error.value = errorMessage
            }
            _isLoading.value = false
        }
    }

    private fun sortArticlesByDateAscending(articles: List<Article>): List<Article> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

        return articles.sortedBy { article ->
            try {
                dateFormat.parse(article.publishedAt)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
    }

    fun applyFilters(filters: NewsFilters) {
        loadTopHeadlines(
            category = filters.category,
            shouldReverseOrder = filters.shouldReverseOrder
        )
    }

    fun logNotificationClick() {
        analyticsHelper.logEvent("notifications_icon_click")
    }

    fun logFavoriteClick() {
        analyticsHelper.logEvent("favorites_icon_click")
    }
}


