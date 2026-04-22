plugins {
    alias(libs.plugins.stemflow.android.feature)
}

android {
    namespace = "com.boni.stemflow.feature.player"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.media3.exoplayer)
    implementation(libs.coil.compose)
}
