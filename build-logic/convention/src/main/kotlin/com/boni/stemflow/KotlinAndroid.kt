package com.boni.stemflow

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariantBuilder
import com.android.build.api.variant.LibraryVariantBuilder
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.compileSdk = 36
    commonExtension.defaultConfig.minSdk = 24
    commonExtension.compileOptions.sourceCompatibility = JavaVersion.VERSION_17
    commonExtension.compileOptions.targetCompatibility = JavaVersion.VERSION_17
    configureKotlin<KotlinAndroidProjectExtension>()
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    configureKotlin<KotlinJvmProjectExtension>()
}

internal fun Project.hasAndroidTestSources(): Boolean =
    fileTree("src/androidTest") {
        include("**/*.java", "**/*.kt")
    }.files.isNotEmpty()

internal fun Project.disableAndroidTestWhenNoSources(hasAndroidTestSources: Boolean) {
    if (hasAndroidTestSources) return

    extensions.configure(AndroidComponentsExtension::class.java) {
        beforeVariants(selector().all()) { variantBuilder ->
            when (variantBuilder) {
                is ApplicationVariantBuilder -> variantBuilder.enableAndroidTest = false
                is LibraryVariantBuilder -> variantBuilder.enableAndroidTest = false
            }
        }
    }
}

private inline fun <reified T : Any> Project.configureKotlin() = extensions.configure<T> {
    when (this) {
        is KotlinAndroidProjectExtension -> compilerOptions
        is KotlinJvmProjectExtension -> compilerOptions
        else -> error("Unsupported Kotlin extension")
    }.apply {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.addAll(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
        )
    }
}
