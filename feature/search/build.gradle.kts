plugins {
    alias(libs.plugins.stemflow.android.feature)
}

android {
    namespace = "com.boni.stemflow.feature.search"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.paging.compose)
    implementation(libs.coil.compose)
}
