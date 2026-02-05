package com.example.feature.notifications.data.repository

import com.example.feature.notifications.data.local.dao.NotificationDao
import com.example.feature.notifications.data.local.entity.NotificationEntity
import com.example.feature.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override suspend fun saveNotification(notification: NotificationEntity): Long {
        return notificationDao.insertNotification(notification)
    }

    override suspend fun updateNotification(notification: NotificationEntity) {
        notificationDao.updateNotification(notification)
    }

    override fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    override suspend fun getNotificationById(notificationId: Long): NotificationEntity? {
        return notificationDao.getNotificationById(notificationId)
    }

    override suspend fun deleteNotification(notificationId: Long) {
        notificationDao.deleteNotification(notificationId)
    }

    override suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }

    override suspend fun updateExpiredNotifications() {
        val currentTimestamp = System.currentTimeMillis()
        notificationDao.updateExpiredNotifications(currentTimestamp)
    }

    override fun getNewNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getNewNotifications()
    }

    override fun getNewNotificationsCount(): Flow<Int> {
        return notificationDao.getNewNotificationsCount()
    }
}
