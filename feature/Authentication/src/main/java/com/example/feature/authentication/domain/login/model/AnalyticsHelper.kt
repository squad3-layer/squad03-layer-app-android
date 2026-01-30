package com.example.feature.authentication.domain.login.model

interface AnalyticsHelper {
    fun logEvent(eventName: String, params: Map<String, Any>? = null)
    fun logScreenView(screenName: String)
}