plugins {
    alias(libs.plugins.stemflow.android.library)
    alias(libs.plugins.stemflow.android.hilt)
}

android {
    namespace = "com.boni.stemflow.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
