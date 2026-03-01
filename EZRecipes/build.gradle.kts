// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    // Kotlin versions: https://kotlinlang.org/docs/releases.html#release-details
    val kotlinVersion = "2.3.10"

    id("com.android.application") version "9.0.1" apply false
    id("com.google.devtools.ksp") version "2.3.6" apply false
    id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion apply false
}
