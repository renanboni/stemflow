package com.boni.stemflow

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.android")
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            testOptions.unitTests.isIncludeAndroidResources = true
        }
        val hasAndroidTestSources = hasAndroidTestSources()
        if (hasAndroidTestSources) {
            dependencies {
                add("androidTestImplementation", libs.findLibrary("androidx-test-runner").get())
            }
        }
        disableAndroidTestWhenNoSources(hasAndroidTestSources)
        tasks.withType<Test>().configureEach {
            failOnNoDiscoveredTests.set(false)
        }
    }
}
