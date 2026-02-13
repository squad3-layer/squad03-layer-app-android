package com.example.feature.news.di

import com.example.feature.news.data.favorites.FavoritesRepositoryImpl
import com.example.feature.news.data.preferences.FilterPreferences
import com.example.feature.news.data.repository.NewsRepositoryImpl
import com.example.feature.news.domain.repository.FavoritesRepository
import com.example.feature.news.domain.repository.NewsRepository
import com.example.services.storage.PreferencesService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        impl: FavoritesRepositoryImpl
    ): FavoritesRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NewsPreferencesModule {

    @Provides
    @Singleton
    fun provideFilterPreferences(
        preferencesService: PreferencesService
    ): FilterPreferences {
        return FilterPreferences(preferencesService)
    }
}
