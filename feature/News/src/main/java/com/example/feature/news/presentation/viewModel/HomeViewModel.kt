package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.services.analytics.AnalyticsTags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    init {
        loadTopHeadlines()
    }

    fun loadTopHeadlines(country: String = "us") {
        viewModelScope.launch {
            _isLoading.value = true

            val result = getTopHeadlinesUseCase(country)
            result.onSuccess { newsResponse ->
                _articles.value = newsResponse.articles
                _error.value = null
            }.onFailure { exception ->
                val errorMessage = "Alinhar mensagem de feedback de erro"
                _error.value = errorMessage
            }
            _isLoading.value = false
        }
    }

    fun logNotificationClick() {
        analyticsHelper.logEvent("notifications_icon_click")

        fun logFavoriteClick() {
            analyticsHelper.logEvent("favorites_icon_click")
        }
    }
}
