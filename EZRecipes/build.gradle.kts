// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Kotlin versions: https://kotlinlang.org/docs/releases.html#release-details
    val kotlinVersion = "2.2.0"

    id("com.android.application") version "8.12.0" apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    // Version must match Kotlin: https://github.com/google/ksp/releases
    id("com.google.devtools.ksp") version "$kotlinVersion-2.0.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion apply false
}
