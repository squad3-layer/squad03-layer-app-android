package com.example.feature.notifications.domain.usecase

import com.example.feature.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewNotificationsCountUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getNewNotificationsCount()
    }
}
