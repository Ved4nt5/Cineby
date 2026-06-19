// Cineby/app/build.gradle.kts (Module-level / App-level)

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    // ... your compileSdk, targetSdk, etc.
}

dependencies {
    // 💡 PASTE YOUR DEPENDENCIES HERE:
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.material:material:1.12.0")
}
    // Hilt, KSP, and other libraries go here too
}
