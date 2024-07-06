import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.abhiek.ezrecipes"
    compileSdk = 34

    defaultConfig {
        // GITHUB_ACTIONS = true if running in a workflow
        testInstrumentationRunnerArguments["ci"] = System.getenv("GITHUB_ACTIONS") ?: "null"
        applicationId = "com.abhiek.ezrecipes"
        minSdk = 23
        targetSdk = 34
        versionCode = 8
        versionName = "2.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("release")
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
        // Check compatibility table before updating:
        // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBomVersion = "2024.06.00"
    val lifecycleVersion = "2.8.3"
    val materialVersion = "1.6.8"
    val material3Version = "1.2.1"
    val retrofitVersion = "2.11.0"
    val roomVersion = "2.6.1"
    val jupiterVersion = "5.10.3"
    val espressoVersion = "3.6.1"

    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material:$materialVersion")
    implementation("androidx.compose.material:material-icons-extended:$materialVersion")
    //implementation("androidx.compose.material3:material3-android:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // AsyncImage
    implementation("io.coil-kt:coil-compose:2.6.0")
    // Retrofit
    implementation(platform("com.squareup.retrofit2:retrofit-bom:$retrofitVersion"))
    implementation("com.squareup.retrofit2:retrofit")
    implementation("com.squareup.retrofit2:converter-gson")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    testImplementation(platform("org.junit:junit-bom:$jupiterVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("io.mockk:mockk:1.13.11")

    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("tools.fastlane:screengrab:2.1.1")

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
