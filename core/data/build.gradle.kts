plugins {
    alias(libs.plugins.stemflow.android.library)
    alias(libs.plugins.stemflow.android.hilt)
}

android {
    namespace = "com.boni.stemflow.core.data"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(libs.paging.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.room.ktx)
    implementation(libs.retrofit.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.paging.testing)
    testImplementation(projects.core.testing)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.paging.testing)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(projects.core.testing)
}
