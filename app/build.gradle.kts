plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// The one release key (#46). It signs the sideload APK, and Play App Signing
// holds the same key, so a Play install is an *update* of a sideloaded one.
// Local, never committed — not even password-protected, since a keystore
// password only buys time against an offline attack and an Android signing key
// cannot be rotated. Created with:
//   keytool -genkeypair -keystore app/release.keystore -alias mulplu \
//     -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Mulplu"
val releaseKeystore = file("release.keystore")
val releaseStorePassword: String? = System.getenv("MULPLU_STORE_PASSWORD")
val releaseKeyPassword: String? = System.getenv("MULPLU_KEY_PASSWORD")
val releaseSignable =
    releaseKeystore.exists() && releaseStorePassword != null && releaseKeyPassword != null

android {
    namespace = "com.mulplu.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mulplu.app"
        minSdk = 28
        targetSdk = 35
        // Monotone across *all* channels — every artefact that leaves this
        // machine takes the next code, sideload or Play. These are the next
        // free numbers, not the last shipped ones. Tag v<versionName> on ship.
        versionCode = 2
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            if (releaseSignable) {
                storeFile = releaseKeystore
                storePassword = releaseStorePassword
                keyAlias = "mulplu"
                keyPassword = releaseKeyPassword
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
            // R8 stays off: the saving is invisible at this size, while a
            // release-only serialization break would surface on the child's
            // device, where adb cannot reach the supervised user (#44).
            isMinifyEnabled = false
            // Never the debug key: Play rejects debug-signed artefacts, and one
            // would not update the sideloaded install. Unsignable means no
            // signing config at all — the packaging guard below then aborts.
            signingConfig = if (releaseSignable) signingConfigs.getByName("release") else null
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

// Abort before an unsigned APK or AAB exists. Attached to the packaging tasks
// rather than the whole build so that `test` and debug builds still work on a
// fresh clone, where the keystore is absent by design.
tasks.matching { it.name == "packageRelease" || it.name == "packageReleaseBundle" }
    .configureEach {
        doFirst {
            check(releaseSignable) {
                "Cannot sign the release: needs app/release.keystore plus " +
                    "MULPLU_STORE_PASSWORD and MULPLU_KEY_PASSWORD in the environment."
            }
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
