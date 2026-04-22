plugins {
    alias(libs.plugins.stemflow.android.feature)
}

android {
    namespace = "com.boni.stemflow.feature.album"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.coil.compose)
}
