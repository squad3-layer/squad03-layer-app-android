package com.example.feature.notifications.domain.usecase

import com.example.feature.notifications.data.local.entity.NotificationEntity
import com.example.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class SaveNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        sentTimestamp: Long = System.currentTimeMillis(),
        expirationDurationMillis: Long = DEFAULT_EXPIRATION_DURATION
    ): Long {
        val notification = NotificationEntity(
            title = title,
            description = description,
            sentTimestamp = sentTimestamp,
            expirationTimestamp = sentTimestamp + expirationDurationMillis,
            isNew = true
        )
        return repository.saveNotification(notification)
    }

    companion object {
        // Tempo de validade padrão: 24 horas
        private const val DEFAULT_EXPIRATION_DURATION = 24 * 60 * 60 * 1000L
    }
}
