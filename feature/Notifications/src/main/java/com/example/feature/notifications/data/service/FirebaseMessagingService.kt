package com.example.feature.notifications.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.feature.notifications.domain.usecase.SaveNotificationUseCase
import com.example.feature.notifications.domain.usecase.UpdateExpiredNotificationsUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var saveNotificationUseCase: SaveNotificationUseCase

    @Inject
    lateinit var updateExpiredNotificationsUseCase: UpdateExpiredNotificationsUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        serviceScope.launch {
            updateExpiredNotificationsUseCase()
        }

        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Notificação"
            val body = notification.body ?: ""

            val expirationDuration = remoteMessage.data["expiration_duration"]?.toLongOrNull()
                ?: (24 * 60 * 60 * 1000L)

            serviceScope.launch {
                try {
                    saveNotificationUseCase(
                        title = title,
                        description = body,
                        expirationDurationMillis = expirationDuration
                    )
                } catch (e: Exception) {
                    throw e
                }
            }

            showNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "main_notifications_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Avisos Gerais", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            //.setSmallIcon(com.example.mylibrary.R.drawable.ic_notification) // Use seu Design System
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}