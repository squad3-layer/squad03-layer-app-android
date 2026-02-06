package com.example.navigation

import android.content.Context
import android.content.Intent
import com.example.navigation.routes.NavigationRoute

interface Navigator {
    fun navigateToActivity(context: Context, route: NavigationRoute)
    fun navigateToActivityForResult(context: Context, route: NavigationRoute, requestCode: Int)
    fun createIntentForRoute(context: Context, route: NavigationRoute): Intent
}
