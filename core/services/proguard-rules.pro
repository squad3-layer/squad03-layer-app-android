# Add project specific ProGuard rules here.

# Keep Firebase Messaging Service
-keep class com.example.services.notification.service.CoreFirebaseMessagingService { *; }

# Keep Room entities
-keep class com.example.services.database.local.entity.** { *; }

# Keep Room DAOs
-keep interface com.example.services.database.local.dao.** { *; }

# Keep AppDatabase
-keep class com.example.services.database.AppDatabase { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Room runtime
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep notification repository
-keep class com.example.services.notification.repository.** { *; }

# Keep Navigator
-keep class com.example.navigation.Navigator { *; }
-keep class com.example.navigation.routes.** { *; }
