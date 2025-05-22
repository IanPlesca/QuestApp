plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.questapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.questapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configurare corectă pentru ndk
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    // Configurare pentru sourceSets
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs(listOf("src/main/jniLibs"))
        }
    }
}

dependencies {
    // Dependințe de bază pentru Android și Kotlin
    implementation(libs.androidx.core.ktx)
    //implementation(libs.androidx.appcompat) // Păstrat pentru compatibilitate cu alte activități
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended") // Păstrat pentru alte activități
    implementation("androidx.compose.runtime:runtime-livedata:1.5.0") // Pentru collectAsState
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation Compose (păstrat pentru alte activități)
    implementation(libs.androidx.navigation.compose)

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // ML Kit pentru detecția zâmbetului
    implementation("com.google.mlkit:face-detection:16.1.5")
    implementation(libs.mlkit.image.labeling) // Păstrat pentru alte activități
    implementation(libs.vision.common) // Păstrat pentru ML Kit

    // Coroutines pentru gestionarea asincronă
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel pentru Compose (păstrat pentru alte activități)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Alte dependințe pentru activități existente
    implementation(libs.osmdroid) // Păstrat pentru alte activități
    implementation(libs.play.services.location) // Păstrat pentru alte activități
    implementation(libs.protolite.well.known.types) // Păstrat pentru alte activități
    implementation("io.coil-kt:coil-compose:2.3.0") // Păstrat pentru alte activități

    // Teste
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}