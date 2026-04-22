plugins {
    alias(libs.plugins.stemflow.android.library.compose)
}

android {
    namespace = "com.boni.stemflow.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
