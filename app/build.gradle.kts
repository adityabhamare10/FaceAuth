plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.aditya.faceauth"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aditya.faceauth"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // CameraX core library
    implementation ("androidx.camera:camera-core:1.1.0-beta01")
    implementation ("androidx.camera:camera-camera2:1.1.0-beta01")
    implementation ("androidx.camera:camera-lifecycle:1.1.0-beta01")
    implementation ("androidx.camera:camera-view:1.0.0-alpha29")

    // Retrofit and OkHttp for networking
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.2")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.2")

}