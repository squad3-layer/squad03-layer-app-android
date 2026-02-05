package com.example.feature.notifications.di

import android.content.Context
import androidx.room.Room
import com.example.feature.notifications.data.local.dao.NotificationDao
import com.example.feature.notifications.data.local.database.NotificationDatabase
import com.example.feature.notifications.data.repository.NotificationRepositoryImpl
import com.example.feature.notifications.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun provideNotificationDatabase(
            @ApplicationContext context: Context
        ): NotificationDatabase {
            return Room.databaseBuilder(
                context,
                NotificationDatabase::class.java,
                "notification_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        @Singleton
        fun provideNotificationDao(
            database: NotificationDatabase
        ): NotificationDao {
            return database.notificationDao()
        }
    }
}
