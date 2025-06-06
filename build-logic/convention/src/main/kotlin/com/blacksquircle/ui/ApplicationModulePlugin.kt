/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.util.Properties

class ApplicationModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("com.blacksquircle.lint")
            }

            configure<BaseAppModuleExtension> {
                compileSdk = BuildConst.COMPILE_SDK

                defaultConfig {
                    minSdk = BuildConst.MIN_SDK
                    targetSdk = BuildConst.TARGET_SDK

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                setFlavorDimensions(listOf("platform_services"))
                productFlavors {
                    create("gms") {
                        dimension = "platform_services"
                    }
                    create("foss") {
                        dimension = "platform_services"
                    }
                }

                val properties = Properties().apply {
                    val localFile = rootProject.file("local.properties")
                    if (localFile.exists()) {
                        load(localFile.inputStream())
                    }
                }
                signingConfigs {
                    create("release") {
                        storeFile = file("${properties["KEYSTORE_PATH"]}")
                        storePassword = "${properties["KEYSTORE_PASSWORD"]}"
                        keyAlias = "${properties["KEY_ALIAS"]}"
                        keyPassword = "${properties["KEY_PASSWORD"]}"
                    }
                }
                buildTypes {
                    release {
                        signingConfig = signingConfigs.getByName("release")
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                bundle {
                    language {
                        enableSplit = false
                    }
                }
                tasks.withType<KotlinJvmCompile>().configureEach {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                        if (System.getProperty("idea.active") == "true") {
                            freeCompilerArgs.add("-Xdebug")
                        }
                    }
                }
                sourceSets.configureEach {
                    java.srcDirs("src/${this.name}/kotlin")
                }
            }
        }
    }
}