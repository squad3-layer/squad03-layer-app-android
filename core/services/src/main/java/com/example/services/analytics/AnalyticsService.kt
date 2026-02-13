package com.example.services.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

interface AnalyticsTags {
    fun logEvent(eventName: String, params: Map<String, Any>? = null)
    fun logScreenView(screenName: String)
}

class AnalyticsService(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsTags {

    override fun logEvent(eventName: String, params: Map<String, Any>?) {
        val bundle = Bundle().apply {
            params?.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    override fun logScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}

