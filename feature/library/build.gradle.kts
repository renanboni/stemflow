plugins {
    alias(libs.plugins.stemflow.android.feature)
}

android {
    namespace = "com.boni.stemflow.feature.library"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.paging.compose)
    implementation(libs.coil.compose)
}
