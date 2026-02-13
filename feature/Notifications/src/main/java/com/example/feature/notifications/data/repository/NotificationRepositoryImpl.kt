package com.example.feature.notifications.data.repository

import com.example.services.database.local.entity.NotificationEntity
import com.example.feature.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.services.notification.repository.NotificationRepository as CoreNotificationRepository

class NotificationRepositoryImpl @Inject constructor(
    private val coreNotificationRepository: CoreNotificationRepository
) : NotificationRepository {

    override suspend fun saveNotification(notification: NotificationEntity): Long {
        return coreNotificationRepository.saveNotification(notification)
    }

    override suspend fun updateNotification(notification: NotificationEntity) {
        coreNotificationRepository.updateNotification(notification)
    }

    override fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return coreNotificationRepository.getAllNotifications()
    }

    override suspend fun getNotificationById(notificationId: Long): NotificationEntity? {
        return coreNotificationRepository.getNotificationById(notificationId)
    }

    override suspend fun deleteNotification(notificationId: Long) {
        coreNotificationRepository.deleteNotification(notificationId)
    }

    override suspend fun deleteAllNotifications() {
        coreNotificationRepository.deleteAllNotifications()
    }

    override suspend fun updateExpiredNotifications() {
        coreNotificationRepository.updateExpiredNotifications()
    }

    override fun getNewNotifications(): Flow<List<NotificationEntity>> {
        return coreNotificationRepository.getNewNotifications()
    }

    override fun getNewNotificationsCount(): Flow<Int> {
        return coreNotificationRepository.getNewNotificationsCount()
    }
}
