plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "me.tsukinome.project"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.tsukinome.project"
        minSdk = 29
        targetSdk = 29
        versionCode = 2
        versionName = "1.1.0"
        ndk {
            abiFilters += listOf("armeabi-v7a")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        debug {
            isMinifyEnabled = false
        }
    }

    packaging {
        jniLibs {
            excludes += setOf("**/arm64-v8a/**", "**/x86/**", "**/x86_64/**")
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.core:core:1.12.0")
}
