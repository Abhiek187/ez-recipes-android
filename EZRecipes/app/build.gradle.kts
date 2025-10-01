import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

val okHttpVersion = "5.1.0"

android {
    namespace = "com.abhiek.ezrecipes"
    compileSdk = 36

    defaultConfig {
        // GITHUB_ACTIONS = true if running in a workflow
        testInstrumentationRunnerArguments["ci"] = System.getenv("GITHUB_ACTIONS") ?: "null"
        applicationId = "com.abhiek.ezrecipes"
        minSdk = 23
        targetSdk = 36
        versionCode = 12
        versionName = "3.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Expose build variables to the app
        buildConfigField("String", "OK_HTTP_VERSION", "\"$okHttpVersion\"")
    }

    // Uncomment to test a release build
//    signingConfigs {
//        create("release") {
//            storeFile = file("${project.rootDir}/../../keystore.jks")
//            storePassword = "***"
//            keyAlias = "key1"
//            keyPassword = "***"
//        }
//    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("release")

            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        // Adds exported schema location as test app assets
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
}

dependencies {
    // BOM to version: https://developer.android.com/develop/ui/compose/bom/bom-mapping
    val composeBomVersion = "2025.09.00"
    val lifecycleVersion = "2.9.3"
    val activityVersion = "1.11.0"
    val coroutineVersion = "1.10.2"
    val retrofitVersion = "3.0.0"
    val roomVersion = "2.8.0"
    val googlePlayVersion = "2.0.2"
    val jupiterVersion = "5.13.4"
    val espressoVersion = "3.7.0"

    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:$activityVersion")
    implementation("androidx.activity:activity-ktx:$activityVersion")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.navigation:navigation-compose:2.9.4")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")

    // AsyncImage
    implementation("io.coil-kt:coil-compose:2.7.0")
    // Retrofit
    implementation(platform("com.squareup.retrofit2:retrofit-bom:$retrofitVersion"))
    implementation("com.squareup.retrofit2:retrofit")
    implementation("com.squareup.retrofit2:converter-gson")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")
    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // Google Play
    implementation("com.google.android.play:review:$googlePlayVersion")
    implementation("com.google.android.play:review-ktx:$googlePlayVersion")

    testImplementation(platform("org.junit:junit-bom:$jupiterVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    testImplementation("io.mockk:mockk:1.14.6")

    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("tools.fastlane:screengrab:2.1.1")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

ksp {
    // Export the Room schema to be version controlled (but not included in the build)
    arg("room.schemaLocation", "$projectDir/schemas")
}

// Log Gradle test results
tasks.withType<Test> {
    useJUnitPlatform() // enable JUnit 5 (Jupiter)
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events(
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_OUT,
            TestLogEvent.STANDARD_ERROR
        )
        showStandardStreams = true
    }
}
