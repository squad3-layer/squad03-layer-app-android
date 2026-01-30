package com.example.feature.authentication.di

import android.os.Bundle
import com.example.feature.authentication.domain.login.model.AnalyticsHelper
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticsHelper @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsHelper {
    override fun logEvent(eventName: String, params: Map<String, Any>?) {
        val bundle = Bundle()
        params?.forEach { (key, value) ->
            bundle.putString(key, value.toString())
        }
        analytics.logEvent(eventName, bundle)
    }

    override fun logScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}