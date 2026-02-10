package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.news.domain.model.News
import com.example.feature.news.domain.repository.NewsRepository
import com.example.services.analytics.AnalyticsTags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val analyticsHelper: AnalyticsTags
) : ViewModel() {

    private val _items = MutableLiveData<List<News>>()
    val items: LiveData<List<News>> get() = _items

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            try {
                val newsList = repository.getTopHeadlines()
                _items.value = newsList
                analyticsHelper.logEvent("HomeViewModel_loadItems_success")
            } catch (e: Exception) {
                analyticsHelper.logEvent("HomeViewModel_loadItems_error")
            }
        }
    }

    // 🔹 Adicione estes métodos para evitar o erro
    fun logNotificationClick() {
        analyticsHelper.logEvent("notifications_icon_click")
    }

    fun logFavoriteClick() {
        analyticsHelper.logEvent("favorites_icon_click")
    }
}
