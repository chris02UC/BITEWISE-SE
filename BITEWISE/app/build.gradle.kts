// File: <project_root>/app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.compose) // Now correctly defined in TOML
    alias(libs.plugins.google.gms.google.services) // Use the alias
}

android {
    namespace = "com.example.bitewise"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bitewise"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        // This version should align with your Kotlin version and Compose library compatibility.
        // Check the compatibility matrix if issues arise:
        // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.5.1" // For Kotlin 2.1.10, this might need to be updated.
        // Often, this aligns with a specific Compose Compiler version compatible with your Kotlin.
        // For Kotlin 1.9.x, 1.5.x for compose compiler is common.
        // For Kotlin 2.x, you might need a newer compose compiler.
        // However, since you have "org.jetbrains.kotlin.plugin.compose" versioned with Kotlin itself in TOML,
        // you might not need to specify kotlinCompilerExtensionVersion explicitly here if it's managed by that plugin.
        // Or, if you do, ensure it's compatible.
        // Let's assume for now 1.5.1 is what you intend, but be mindful of this.
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.coil.compose)
    implementation(libs.androidx.material.icons.extended)
//    implementation(libs.androidx.navigation.compose.android)
//    implementation(libs.androidx.navigation.compose.jvmstubs)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // Updated to a slightly more common recent version, you can use 33.14.0 if you prefer
    implementation("com.google.firebase:firebase-analytics-ktx") // Use -ktx for Kotlin ananlytics
    implementation("com.google.firebase:firebase-auth-ktx")
    // Add this for Realtime Database
    implementation("com.google.firebase:firebase-database-ktx")
    // You don't need to declare the BOM twice. One platform entry is sufficient.
}