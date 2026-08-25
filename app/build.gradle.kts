plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mulplu.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mulplu.app"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }

    signingConfigs {
        create("release") {
            // Sideloading key. Local, never committed. Create with:
            //   keytool -genkeypair -keystore app/release.keystore -alias mulplu \
            //     -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Mulplu"
            // Passwords via env MULPLU_STORE_PASSWORD / MULPLU_KEY_PASSWORD.
            val ks = file("release.keystore")
            if (ks.exists()) {
                storeFile = ks
                storePassword = System.getenv("MULPLU_STORE_PASSWORD")
                keyAlias = "mulplu"
                keyPassword = System.getenv("MULPLU_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            // Manual-testing hooks (#30): the test panel exists only where this
            // is true, and the whole panel folds away in release.
            buildConfigField("boolean", "TEST_HOOKS", "true")
        }
        release {
            buildConfigField("boolean", "TEST_HOOKS", "false")
            isMinifyEnabled = false
            // Fall back to the debug key so assembleRelease always yields an
            // installable (sideloadable) APK, even without a local keystore.
            signingConfig = if (file("release.keystore").exists()) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
