package com.example.services.di

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.example.services.analytics.AnalyticsService
import com.example.services.analytics.AnalyticsTags
import com.example.services.authentication.AuthenticationService
import com.example.services.authentication.AuthenticationServiceImpl
import com.example.services.database.FirestoreService
import com.example.services.database.FirestoreServiceImpl
import com.example.services.network.ApiService
import com.example.services.network.NetworkService
import com.example.services.network.NetworkServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.analytics.FirebaseAnalytics
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
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
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
        gson: Gson
    ): NetworkService {
        return NetworkServiceImpl(apiService, gson)
    }
}
