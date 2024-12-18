plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.FootBall"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.FootBall"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        // 다른 중복 파일이 있을 경우 추가
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
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.recyclerview)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("com.github.bumptech.glide:glide:4.15.1")  // Glide 의존성 추가
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.github.yalantis:ucrop:2.2.8")

    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.google.firebase:firebase-auth:22.3.1") // 최신 버전 확인
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")// 최신 버전으로 업데이트
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0") // 최신 버전으로 업데이
    implementation("com.google.mlkit:text-recognition-korean:16.0.1")
    implementation("io.github.bonigarcia:webdrivermanager:5.0.3")
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("androidx.activity:activity-ktx:1.6.1")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation(libs.androidx.core.ktx) // Core KTX
    implementation("androidx.appcompat:appcompat:1.6.1") // Stable version of AppCompat
    implementation(libs.material) // Material Components
    implementation("androidx.activity:activity-ktx:1.9.2") // Activity KTX
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Stable version of ConstraintLayout

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}

configurations.all {
    resolutionStrategy {
        force("androidx.constraintlayout:constraintlayout:2.1.4")
        force("androidx.appcompat:appcompat:1.6.1")
    }
}