package com.example.feature.authentication.di

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.example.feature.authentication.domain.login.model.AnalyticsHelper
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    abstract fun bindAnalyticsHelper(
        impl: FirebaseAnalyticsHelper
    ): AnalyticsHelper
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK])
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}

