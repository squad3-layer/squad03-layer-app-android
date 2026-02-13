package com.example.services.database.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val sentTimestamp: Long,
    val expirationTimestamp: Long,
    val isNew: Boolean = true
)

