plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

android {
    namespace = "com.guitarsimulator.guitar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.guitar"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        compose = true
    }
}

dependencies {
    implementation("com.airbnb.android:lottie-compose:6.6.7")
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.lifecycle.service)
    kapt("androidx.room:room-compiler:2.7.2")
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.material3)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.media)
    implementation("androidx.room:room-ktx:2.7.2")
    //noinspection UseTomlInstead
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation(libs.okhttp)
    implementation(libs.converter.gson)

    implementation(libs.androidx.core.splashscreen)

// Test helpers
    testImplementation("androidx.room:room-testing:2.7.2")
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.core.ktx)
    implementation(libs.coil.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose) // phiên bản mới nhất 2025
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.navigation3.ui.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
}