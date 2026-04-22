plugins {
    alias(libs.plugins.stemflow.android.library)
}

android {
    namespace = "com.boni.stemflow.core.testing"
}

dependencies {
    api(projects.core.domain)
    api(projects.core.network)
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    implementation(libs.kotlinx.coroutines.core)
}
