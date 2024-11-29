buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
    repositories {
        google()
        mavenCentral()
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
   // id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    id("com.google.dagger.hilt.android") version "2.52" apply false

}
