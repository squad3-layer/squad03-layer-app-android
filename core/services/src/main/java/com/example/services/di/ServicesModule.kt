package com.example.services.di

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.room.Room
import com.example.services.analytics.AnalyticsService
import com.example.services.analytics.AnalyticsTags
import com.example.services.authentication.AuthenticationService
import com.example.services.authentication.AuthenticationServiceImpl
import com.example.services.database.FirestoreService
import com.example.services.database.FirestoreServiceImpl
import com.example.services.database.AppDatabase
import com.example.services.database.local.dao.ArticleDao
import com.example.services.database.local.dao.NotificationDao
import com.example.services.network.ApiService
import com.example.services.network.NetworkService
import com.example.services.network.NetworkServiceImpl
import com.example.services.notification.repository.NotificationRepository
import com.example.services.storage.PreferencesService
import com.example.services.storage.PreferencesServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthenticationService(
        firebaseAuth: FirebaseAuth
    ): AuthenticationService {
        return AuthenticationServiceImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestoreService(
        firestore: FirebaseFirestore
    ): FirestoreService {
        return FirestoreServiceImpl(firestore)
    }

    @Provides
    @Singleton
    @ServicesGson
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideRetrofit(@ServicesGson gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkService(
        apiService: ApiService,
        @ServicesGson gson: Gson
    ): NetworkService {
        return NetworkServiceImpl(apiService, gson)
    }
    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        return remoteConfig
    }

    @Provides
    @Singleton
    fun providePreferencesService(
        @ApplicationContext context: Context
    ): PreferencesService {
        return PreferencesServiceImpl(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(
        database: AppDatabase
    ): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    @Singleton
    fun provideArticleDao(
        database: AppDatabase
    ): ArticleDao {
        return database.articleDao()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationDao: NotificationDao
    ): NotificationRepository {
        return NotificationRepository(notificationDao)
    }
}
