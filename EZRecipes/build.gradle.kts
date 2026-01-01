// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Kotlin versions: https://kotlinlang.org/docs/releases.html#release-details
    val kotlinVersion = "2.3.0"

    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("com.google.devtools.ksp") version "2.3.4" apply false
    id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion apply false
}
