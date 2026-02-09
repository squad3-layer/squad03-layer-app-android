package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feature.news.domain.model.NewsModel
import com.example.services.analytics.AnalyticsTags
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val analyticsHelper: AnalyticsTags
) : ViewModel() {

    fun logNotificationClick(){
        analyticsHelper.logEvent("notifications_icon_click")
    }


    private val _items = MutableLiveData<List<NewsModel>>()
    val items: LiveData<List<NewsModel>> get() = _items

    init {
       loadItems()
    }

    private fun loadItems() {
        // Exemplo de dados mockados
        val mockList = listOf(
            NewsModel(1, "Título 1", "Descrição 1"),
            NewsModel(2, "Título 2", "Descrição 2"),
            NewsModel(3, "Título 3", "Descrição 3")
        )

        _items.value = mockList


        analyticsHelper.logEvent("HomeViewModel_loadItems")
    }
}
