package com.example.services.notification.repository

import com.example.services.database.local.dao.NotificationDao
import com.example.services.database.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {

    suspend fun saveNotification(notification: NotificationEntity): Long {
        return notificationDao.insertNotification(notification)
    }

    suspend fun updateNotification(notification: NotificationEntity) {
        notificationDao.updateNotification(notification)
    }

    fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    suspend fun getNotificationById(notificationId: Long): NotificationEntity? {
        return notificationDao.getNotificationById(notificationId)
    }

    suspend fun deleteNotification(notificationId: Long) {
        notificationDao.deleteNotification(notificationId)
    }

    suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }

    suspend fun updateExpiredNotifications() {
        val currentTimestamp = System.currentTimeMillis()
        notificationDao.updateExpiredNotifications(currentTimestamp)
    }

    fun getNewNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getNewNotifications()
    }

    fun getNewNotificationsCount(): Flow<Int> {
        return notificationDao.getNewNotificationsCount()
    }
}

