plugins {
    alias(libs.plugins.stemflow.android.library.compose)
}

android {
    namespace = "com.boni.stemflow.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    implementation(libs.androidx.core.ktx)
}
