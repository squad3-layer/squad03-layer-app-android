package com.example.feature.notifications.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.feature.notifications.data.local.dao.NotificationDao
import com.example.feature.notifications.data.local.entity.NotificationEntity

@Database(
    entities = [NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
