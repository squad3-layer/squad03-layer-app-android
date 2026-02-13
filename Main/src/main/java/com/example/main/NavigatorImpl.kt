package com.example.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.navigation.Navigator
import com.example.navigation.routes.NavigationRoute
import javax.inject.Inject

class NavigatorImpl @Inject constructor() : Navigator {

    override fun navigateToActivity(context: Context, route: NavigationRoute) {
        val intent = createIntent(context, route)
        context.startActivity(intent)
    }

    override fun navigateToActivityForResult(context: Context, route: NavigationRoute, requestCode: Int) {
        val intent = createIntent(context, route)
        if (context is Activity) {
            context.startActivityForResult(intent, requestCode)
        }
    }

    override fun createIntentForRoute(context: Context, route: NavigationRoute): Intent {
        return createIntent(context, route)
    }

    private fun createIntent(context: Context, route: NavigationRoute): Intent {
        return Intent().apply {
            setClassName(context, route.className)

            when (route) {
                is NavigationRoute.Login -> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                is NavigationRoute.Home -> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                is NavigationRoute.Filters -> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                is NavigationRoute.Notifications, is NavigationRoute.Favorites -> {}
            }

            route.getExtras()?.forEach { (key, value) ->
                when (value) {
                    is String -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                }
            }
        }
    }
}

