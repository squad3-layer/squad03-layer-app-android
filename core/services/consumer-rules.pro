# Consumer ProGuard rules for core.services module

# Keep Firebase Messaging Service
-keep class com.example.services.notification.service.CoreFirebaseMessagingService { *; }

# Keep notification entities and DAOs
-keep class com.example.services.notification.entity.** { *; }
-keep interface com.example.services.notification.dao.** { *; }
-keep class com.example.services.notification.repository.** { *; }

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Keep Firebase
-keep class com.google.firebase.messaging.** { *; }

