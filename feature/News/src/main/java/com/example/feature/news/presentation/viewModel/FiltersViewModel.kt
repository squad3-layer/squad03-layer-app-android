package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feature.news.domain.model.NewsFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class FiltersViewModel @Inject constructor(): ViewModel() {
    private val _selectedOrdering = MutableLiveData<Int>()
    val selectedOrdering: LiveData<Int> = _selectedOrdering

    private val _selectedCategory = MutableLiveData<Int>()
    val selectedCategory: LiveData<Int> = _selectedCategory

    private val _appliedFilters = MutableLiveData<NewsFilters>()
    val appliedFilters: LiveData<NewsFilters> = _appliedFilters

    private val orderingOptions = listOf(
        "Mais recentes",
        "Mais antigos"
    )

    private val categories = listOf(
        "Geral",
        "Negócios",
        "Entretenimento",
        "Saúde",
        "Ciência",
        "Esportes",
        "Tecnologia"
    )

    private val categoryMapping = mapOf(
        "Geral" to "general",
        "Negócios" to "business",
        "Entretenimento" to "entertainment",
        "Saúde" to "health",
        "Ciência" to "science",
        "Esportes" to "sports",
        "Tecnologia" to "technology"
    )

    init {
        _selectedOrdering.value = 0
        _selectedCategory.value = 0
    }

    fun getOrderingOptions(): List<String> = orderingOptions

    fun getCategories(): List<String> = categories

    fun onOrderingSelected(position: Int) {
        _selectedOrdering.value = position
    }

    fun onCategorySelected(position: Int) {
        _selectedCategory.value = position
    }

    fun applyFilters() {
        val orderingIndex = _selectedOrdering.value ?: 0
        val categoryIndex = _selectedCategory.value ?: 0

        val categoryName = categories[categoryIndex]

        val filters = NewsFilters(
            category = categoryMapping[categoryName],
            shouldReverseOrder = orderingIndex == 1
        )

        _appliedFilters.value = filters
    }

    fun clearFilters() {
        _selectedOrdering.value = 0
        _selectedCategory.value = 0

        _appliedFilters.value = NewsFilters(
            category = null,
            shouldReverseOrder = false
        )
    }

    fun getCurrentFilters(): NewsFilters {
        return _appliedFilters.value ?: NewsFilters()
    }
}
