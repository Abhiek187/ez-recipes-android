// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val kotlinVersion = "1.9.22"

    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    // Version must match Kotlin: https://github.com/google/ksp/releases
    id("com.google.devtools.ksp") version "$kotlinVersion-1.0.17" apply false
}
