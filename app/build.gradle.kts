// `java` in a build script resolves to the Java extension, not the package, so
// java.util.Properties has to be imported rather than spelled out.
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
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

// The version lives in version.properties at the repo root, not here (#59): the
// `releaseVersion` task bumps it mechanically, and a task that rewrites a Kotlin
// build script by regex is a diff nobody would trust two years later.
// providers.fileContents (not File.readText) so the configuration cache notices
// a bump instead of serving a stale versionName.
val versionProperties =
    Properties().apply {
        load(
            providers
                .fileContents(rootProject.layout.projectDirectory.file("version.properties"))
                .asText
                .get()
                .reader(),
        )
    }
val appVersionCode = versionProperties.getProperty("versionCode").toInt()
val appVersionName = versionProperties.getProperty("versionName")

android {
    namespace = "com.mulplu.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mulplu.app"
        minSdk = 28
        // Play's upload floor, not a preference (#66): since 31.08.2026 a new
        // app or an update must target 36 to be accepted at all. The extension
        // to 01.11.2026 covers only already-published apps with a Console
        // policy warning, so it was never available to us. Raising this is the
        // only lever — do not lower it.
        targetSdk = 36
        // Monotone across *all* channels — every artefact that leaves this
        // machine takes the next code, sideload or Play. Set by `releaseVersion`
        // (#59), which also creates the tag the guard below insists on.
        versionCode = appVersionCode
        versionName = appVersionName
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

// Abort before an unsigned or unattributable APK or AAB exists. Attached to the
// packaging tasks rather than the whole build so that `test` and debug builds
// still work on a fresh clone, where the keystore is absent by design.
tasks.matching { it.name == "packageRelease" || it.name == "packageReleaseBundle" }
    .configureEach {
        // Read at configuration time: the doFirst below must capture plain
        // values, not the build script, or the configuration cache cannot
        // serialize the task.
        val signable = releaseSignable
        val repoDir = rootProject.rootDir
        val expectedTag = "v$appVersionName"
        doFirst {
            check(signable) {
                "Cannot sign the release: needs app/release.keystore plus " +
                    "MULPLU_STORE_PASSWORD and MULPLU_KEY_PASSWORD in the environment."
            }

            // LegalScreen prints github.com/ewirch/mulplu/releases/tag/v<versionName>
            // as the GPLv3 § 6(d) source offer, and that binary outlives every
            // intention we have about it. So the check is not "a tag exists" but
            // the obligation itself: what is packaged here is exactly what that
            // tag serves. Deliberately offline — pushing is `releaseVersion`'s
            // job (#59); this only verifies nothing moved since.
            fun run(vararg args: String): Pair<Int, String> {
                val process = ProcessBuilder(listOf("git", *args))
                    .directory(repoDir)
                    .redirectErrorStream(true)
                    .start()
                val output = process.inputStream.bufferedReader().readText().trim()
                return process.waitFor() to output
            }

            val (tagCode, taggedCommit) = run("rev-list", "--max-count=1", expectedTag)
            check(tagCode == 0) {
                "No tag $expectedTag — run `./gradlew releaseVersion` before packaging, " +
                    "or the shipped app's source link is a 404."
            }
            val head = run("rev-parse", "HEAD").second
            check(taggedCommit == head) {
                "Tag $expectedTag points at $taggedCommit but HEAD is $head — " +
                    "the packaged code is not the code that tag serves."
            }
            check(run("status", "--porcelain").second.isEmpty()) {
                "Working tree is not clean — the packaged code is not the code " +
                    "tag $expectedTag serves."
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
