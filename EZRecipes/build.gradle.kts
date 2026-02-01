// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Kotlin versions: https://kotlinlang.org/docs/releases.html#release-details
    val kotlinVersion = "2.2.21"

    id("com.android.application") version "9.0.0" apply false
    id("com.google.devtools.ksp") version "2.3.5" apply false
    id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion apply false
}
