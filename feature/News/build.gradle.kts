plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
}


android {
    namespace = "com.example.feature.news"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(projects.navigation)
    implementation(projects.core.services)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation("io.coil-kt:coil:2.4.0")

    // Design System
    implementation(libs.mylibrary)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation("com.google.firebase:firebase-firestore")

    // Lifecycle / Retrofit
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Glide / Hilt
    implementation(libs.glide.core)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.fragment)
    ksp(libs.glide.compiler)
    ksp(libs.hilt.compiler)
}