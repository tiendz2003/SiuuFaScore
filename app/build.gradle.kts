import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")



    /*
        id ("com.google.gms.google-services")
    */
}

android {
    namespace = "com.example.minilivescore"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.minilivescore"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            val properties = Properties().apply {
                load(rootProject.file("local.properties").inputStream())
            }
            val apiKey = checkNotNull(properties.getProperty("API_KEY")){
                "API_KEY is not set in local.properties"
            }
            val youtubeApiKey = checkNotNull(properties.getProperty("YOUTUBE_API_KEY")){
                "YOTUBE_API_KEY is not set in local.properties"
            }
            buildConfigField(
                type = "String",
                name = "YOUTUBE_API_KEY",
                value = "\"$youtubeApiKey\""
            )
            buildConfigField(
                type = "String",
                name = "API_KEY",
                value = "\"$apiKey\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding =  true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
/*
    implementation("com.google.android.gms:play-services-auth:20.5.0")
*/
    implementation("androidx.multidex:multidex:2.0.1")
    implementation ("com.amazonaws:ivs-player:1.16.0")
    implementation ("com.airbnb.android:lottie:3.4.0")
    implementation ("com.google.apis:google-api-services-youtube:v3-rev20210915-1.32.1")
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.0.0")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity)
  //  implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    val glideVersion = "4.16.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("com.github.bumptech.glide:okhttp3-integration:$glideVersion")
    ksp("com.github.bumptech.glide:ksp:$glideVersion")
    // Additional dependencies from the original request
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation ("androidx.core:core-ktx:1.13.1")
    implementation ("com.squareup.moshi:moshi-kotlin:1.12.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation(libs.androidx.paging.runtime)
    implementation(libs.com.github.bumptech.glide)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.com.google.dagger.hilt.android)
    kapt(libs.com.google.dagger.hilt.android.compiler)
    implementation ("androidx.test:core-ktx:1.6.1")
    ksp(libs.androidx.room.compiler)
    implementation(libs.com.squareup.retrofit2)
    implementation(libs.com.squareup.okhttp3.logging.interceptor)
    implementation(libs.com.squareup.retrofit2.converter.gson)

    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.io.coil.kt.coil)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
}
