package com.example.feature.notifications.domain.usecase

import com.example.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class UpdateExpiredNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() {
        repository.updateExpiredNotifications()
    }
}
