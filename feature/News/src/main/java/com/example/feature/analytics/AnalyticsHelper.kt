package com.example.feature.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsHelper(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    private fun logEvent(eventName: String, params: Map<String, String>? = null) {
        val bundle = Bundle()
        params?.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun logViewHome() {
        logEvent("view_home")
    }

    fun logClickNews(identifier: String) {
        logEvent("click_news", mapOf("news_identifier" to identifier))
    }

    fun logClickMenu() {
        logEvent("click_menu")
    }

    fun logClickMenuOption(optionName: String) {
        logEvent("click_menu_option", mapOf("option" to optionName))
    }

    fun logViewDetails(newsId: String) {
        logEvent("view_details", mapOf("news_id" to newsId))
    }

    fun logShareNews(newsId: String) {
        logEvent("share_news", mapOf("news_id" to newsId))
    }


    fun logAddToFavorites(identifier: String) {
        logEvent("add_to_favorites", mapOf("news_identifier" to identifier))
    }

    fun logRemoveFavorite(identifier: String) {
        logEvent("remove_favorite", mapOf("news_identifier" to identifier))
    }

    fun logViewFavorites() {
        logEvent("view_favorites")
    }

    fun logClickFavoriteItem(newsId: String) {
        logEvent("click_favorite_item", mapOf("news_id" to newsId))
    }

}
