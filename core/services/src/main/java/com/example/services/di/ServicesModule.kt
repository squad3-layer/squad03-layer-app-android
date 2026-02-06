package com.example.services.di

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.example.services.analytics.AnalyticsService
import com.example.services.analytics.AnalyticsTags
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {
    @RequiresPermission(allOf = [Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK])
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAnalyticsService(
        firebaseAnalytics: FirebaseAnalytics
    ): AnalyticsTags {
        return AnalyticsService(firebaseAnalytics)
    }
}
