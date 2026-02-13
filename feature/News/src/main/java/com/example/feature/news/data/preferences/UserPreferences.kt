package com.example.feature.news.data.preferences

import com.example.services.storage.PreferencesService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterPreferences @Inject constructor(
    private val preferencesService: PreferencesService
) {

    companion object {
        private const val KEY_SELECTED_ORDERING = "filter_selected_ordering"
        private const val KEY_SELECTED_CATEGORY = "filter_selected_category"
        private const val KEY_ACTIVE_FILTERS_COUNT = "filter_active_filters_count"
    }

    fun getSelectedOrdering(): Int {
        return preferencesService.getInt(KEY_SELECTED_ORDERING, 0)
    }

    fun getSelectedCategory(): Int {
        return preferencesService.getInt(KEY_SELECTED_CATEGORY, 0)
    }

    fun getActiveFiltersCount(): Int {
        return preferencesService.getInt(KEY_ACTIVE_FILTERS_COUNT, 0)
    }

    fun saveSelectedOrdering(position: Int) {
        preferencesService.putInt(KEY_SELECTED_ORDERING, position)
    }

    fun saveSelectedCategory(position: Int) {
        preferencesService.putInt(KEY_SELECTED_CATEGORY, position)
    }

    fun saveActiveFiltersCount(count: Int) {
        preferencesService.putInt(KEY_ACTIVE_FILTERS_COUNT, count)
    }

    fun clearFilters() {
        preferencesService.putInt(KEY_SELECTED_ORDERING, 0)
        preferencesService.putInt(KEY_SELECTED_CATEGORY, 0)
        preferencesService.putInt(KEY_ACTIVE_FILTERS_COUNT, 0)
    }
}
