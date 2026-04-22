plugins {
    alias(libs.plugins.stemflow.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.boni.stemflow.feature.album"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)
}
