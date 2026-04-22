plugins {
    `kotlin-dsl`
}

group = "com.boni.stemflow.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "stemflow.android.application"
            implementationClass = "com.boni.stemflow.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "stemflow.android.library"
            implementationClass = "com.boni.stemflow.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "stemflow.android.library.compose"
            implementationClass = "com.boni.stemflow.AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "stemflow.android.feature"
            implementationClass = "com.boni.stemflow.AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "stemflow.android.hilt"
            implementationClass = "com.boni.stemflow.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "stemflow.android.room"
            implementationClass = "com.boni.stemflow.AndroidRoomConventionPlugin"
        }
        register("jvmLibrary") {
            id = "stemflow.jvm.library"
            implementationClass = "com.boni.stemflow.JvmLibraryConventionPlugin"
        }
    }
}
