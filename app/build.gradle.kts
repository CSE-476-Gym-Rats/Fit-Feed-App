plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.fitfeed"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fitfeed"
        minSdk = 24
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 24
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_API_KEY"] = project.findProperty("MAPS_API_KEY") as String
    }

    buildTypes {
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.security.crypto)
    coreLibraryDesugaring (libs.desugar.jdk.libs)

    // Jetpack Compose integration
    implementation(libs.navigation.compose)

    // Views/Fragments integration
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.navigation.dynamic.features.fragment)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)

    // Testing Navigation
    androidTestImplementation(libs.navigation.testing)

    // Cloudinary
    implementation(libs.cloudinary.android)
    implementation(libs.cloudinary.android.download)
    implementation(libs.cloudinary.android.preprocess)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Glide
    implementation(libs.glide)
    annotationProcessor (libs.compiler)

    // Apache Commons
    implementation("commons-validator:commons-validator:1.9.0")

    implementation(libs.gson)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}