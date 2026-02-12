package com.example.feature.analytics


import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsHelper(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(eventName: String, params: Map<String, String>? = null) {
        val bundle = Bundle()
        params?.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    // 👉 Funções específicas para cada evento

    fun logViewHome() {
        logEvent("view_home")
    }

    fun logClickNewsCard(newsId: String, category: String) {
        logEvent("click_news_card", mapOf("news_id" to newsId, "category" to category))
    }

    fun logAddToFavorites(newsId: String) {
        logEvent("add_to_favorites", mapOf("news_id" to newsId))
    }

    fun logRemoveFavorite(newsId: String) {
        logEvent("remove_favorite", mapOf("news_id" to newsId))
    }

    fun logViewFavorites(count: Int) {
        logEvent("view_favorites", mapOf("favorites_count" to count.toString()))
    }


    fun logClickMenuFavorites() {
        logEvent("click_menu_favorites")
    }

    fun logClickMenuNotifications() {
        logEvent("click_menu_notifications")
    }
}

