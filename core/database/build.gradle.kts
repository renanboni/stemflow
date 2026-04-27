plugins {
    alias(libs.plugins.stemflow.android.library)
    alias(libs.plugins.stemflow.android.hilt)
    alias(libs.plugins.stemflow.android.room)
}

android {
    namespace = "com.boni.stemflow.core.database"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(libs.room.paging)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.turbine)
}
