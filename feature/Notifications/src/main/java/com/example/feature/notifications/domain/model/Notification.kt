package com.example.feature.notifications.domain.model

import com.example.services.database.local.entity.NotificationEntity

data class Notification(
    val id: Long,
    val title: String,
    val description: String,
    val sentTimestamp: Long,
    val expirationTimestamp: Long,
    val isNew: Boolean
) {
    companion object {
        fun fromEntity(entity: NotificationEntity): Notification {
            return Notification(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                sentTimestamp = entity.sentTimestamp,
                expirationTimestamp = entity.expirationTimestamp,
                isNew = entity.isNew
            )
        }
    }
}

fun NotificationEntity.toDomain(): Notification = Notification.fromEntity(this)
