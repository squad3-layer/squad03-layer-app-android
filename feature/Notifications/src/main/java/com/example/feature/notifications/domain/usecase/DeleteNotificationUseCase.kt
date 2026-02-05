package com.example.feature.notifications.domain.usecase

import com.example.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long) {
        repository.deleteNotification(notificationId)
    }
}
