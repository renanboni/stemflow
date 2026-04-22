plugins {
    alias(libs.plugins.stemflow.android.library.compose)
}

android {
    namespace = "com.boni.stemflow.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.material.icons.extended)
    api(libs.coil.compose)
    implementation(libs.androidx.core.ktx)
}
