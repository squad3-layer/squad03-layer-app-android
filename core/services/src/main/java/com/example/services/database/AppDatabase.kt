package com.example.services.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.services.database.local.dao.ArticleDao
import com.example.services.database.local.dao.NotificationDao
import com.example.services.database.local.entity.ArticleEntity
import com.example.services.database.local.entity.NotificationEntity

@Database(
    entities = [NotificationEntity::class, ArticleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun articleDao(): ArticleDao
}

