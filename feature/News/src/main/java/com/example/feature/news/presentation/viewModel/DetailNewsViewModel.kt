package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailNewsViewModel : ViewModel() {
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    fun toggleFavorite(newsId: String) {
        val newState = !_isFavorite.value
        _isFavorite.value = newState


        if (newState) {
            saveToFavoritesApi(newsId)
        } else {
            removeFromFavoritesApi(newsId)
        }
    }

    private fun saveToFavoritesApi(id: String) {
        // Lógica de Retrofit para POST /favorites
    }
    private fun removeFromFavoritesApi(id: String) {
        // Lógica de Retrofit para DELETE /favorites
    }
}