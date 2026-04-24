plugins {
    alias(libs.plugins.stemflow.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.boni.stemflow.feature.player"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)
}
