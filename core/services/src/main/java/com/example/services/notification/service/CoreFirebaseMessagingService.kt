package com.example.services.notification.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.services.R
import com.example.services.database.local.entity.NotificationEntity
import com.example.services.notification.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
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
class CoreFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var navigator: com.example.navigation.Navigator

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        serviceScope.launch {
            try {
                notificationRepository.updateExpiredNotifications()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating expired notifications", e)
            }
        }
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Notificação"
            val body = notification.body ?: ""

            val expirationDuration = remoteMessage.data["expiration_duration"]?.toLongOrNull()
                ?: DEFAULT_EXPIRATION_DURATION

            serviceScope.launch {
                try {
                    val notificationEntity = NotificationEntity(
                        title = title,
                        description = body,
                        sentTimestamp = System.currentTimeMillis(),
                        expirationTimestamp = System.currentTimeMillis() + expirationDuration,
                        isNew = true
                    )
                    notificationRepository.saveNotification(notificationEntity)
                    Log.d(TAG, "Notification saved to database")
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving notification to database", e)
                }
            }

            showNotification(title, body, remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
    }

    private fun showNotification(title: String, message: String, data: Map<String, String>) {
        // Check permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        val channelId = "main_notifications_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Notificações Gerais",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal para notificações gerais do aplicativo"
            enableLights(true)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)

        val intent = createNotificationIntent(data)
        val pendingIntent = PendingIntent.getActivity(
            this,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_logo_layer)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
        Log.d(TAG, "Notification displayed")
    }

    private fun createNotificationIntent(data: Map<String, String>): Intent {
        val isUserLoggedIn = auth.currentUser != null

        return if (isUserLoggedIn) {
            navigator.createIntentForRoute(
                this,
                com.example.navigation.routes.NavigationRoute.Notifications
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                data.forEach { (key, value) ->
                    putExtra(key, value)
                }
            }
        } else {
            navigator.createIntentForRoute(
                this,
                com.example.navigation.routes.NavigationRoute.Login(redirectToNotifications = true)
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                data.forEach { (key, value) ->
                    putExtra(key, value)
                }
            }
        }
    }

    companion object {
        private const val TAG = "CoreFCMService"
        private const val DEFAULT_EXPIRATION_DURATION = 24 * 60 * 60 * 1000L // 24 hours
    }
}


