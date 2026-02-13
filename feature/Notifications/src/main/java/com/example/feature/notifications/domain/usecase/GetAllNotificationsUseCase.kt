package com.example.feature.notifications.domain.usecase

import com.example.services.database.local.entity.NotificationEntity
import com.example.feature.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<List<NotificationEntity>> {
        return repository.getAllNotifications()
    }
}
