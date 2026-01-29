package com.example.feature.authentication.domain.login.model

import dagger.Provides

interface AnalyticsHelper {
    fun logEvent(eventName: String, params: Map<String, Any>? = null)
    fun logScreenView(screenName: String)
}