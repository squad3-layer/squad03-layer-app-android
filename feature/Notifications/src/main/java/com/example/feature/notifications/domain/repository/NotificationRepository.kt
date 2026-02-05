package com.example.feature.notifications.domain.repository

import com.example.feature.notifications.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun saveNotification(notification: NotificationEntity): Long
    suspend fun updateNotification(notification: NotificationEntity)
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    suspend fun getNotificationById(notificationId: Long): NotificationEntity?
    suspend fun deleteNotification(notificationId: Long)
    suspend fun deleteAllNotifications()
    suspend fun updateExpiredNotifications()
    fun getNewNotifications(): Flow<List<NotificationEntity>>
    fun getNewNotificationsCount(): Flow<Int>
}
