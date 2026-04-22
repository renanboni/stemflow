plugins {
    alias(libs.plugins.stemflow.android.library)
    alias(libs.plugins.stemflow.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.boni.stemflow.core.network"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)
}
