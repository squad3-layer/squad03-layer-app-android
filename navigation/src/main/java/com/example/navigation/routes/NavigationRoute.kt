package com.example.navigation.routes

// navigation/src/main/java/com/example/navigation/routes/NavigationRoute.kt
sealed class NavigationRoute(val className: String) {
    data class Login(
        val redirectToNotifications: Boolean = false
    ) : NavigationRoute("com.example.feature.authentication.presentation.login.view.LoginActivity") {
        override fun getExtras(): Map<String, Any> = mapOf(
            "REDIRECT_TO_NOTIFICATIONS" to redirectToNotifications
        )
    }

    data object Home : NavigationRoute("com.example.feature.news.presentation.view.HomeActivity")
    data object Notifications : NavigationRoute("com.example.feature.notifications.presentation.view.NotificationsActivity")

    data object Favorites : NavigationRoute("com.example.feature.news.presentation.view.favorites.FavoritesActivity")
    data object Filters : NavigationRoute("com.example.feature.news.presentation.view.FiltersActivity")
    open fun getExtras(): Map<String, Any>? = null
}
