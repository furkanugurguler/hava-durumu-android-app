plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.havadurumu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.havadurumu"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx) // Sadece bu kaldı
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // Sadece bu kaldı
    implementation(libs.androidx.navigation.fragment.ktx) // Sadece bu kaldı
    implementation(libs.androidx.navigation.ui.ktx)      // Sadece bu kaldı
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.github.bumptech.glide:glide:4.16.0") // Sadece bu ve doğru versiyon kaldı
    // implementation("com.github.bumptech.glide:glide:4.12.0") // BU SİLİNDİ
    // implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1") // BU SİLİNDİ
    // implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1") // BU SİLİNDİ
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7") // BU SİLİNDİ
    // implementation ("androidx.navigation:navigation-ui-ktx:2.7.7") // BU SİLİNDİ
}