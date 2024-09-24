import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.gptorganizier"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gptorganizier"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
        }
        jniLibs {
            excludes.add("*.so")
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //implementation(libs.google.api.client.android)
    //implementation(libs.google.api.services.drive)
    //implementation(libs.play.services.auth)
    //implementation(libs.okhttp)


    implementation("androidx.cardview:cardview:1.0.0")

    // Google Drive API
    implementation("com.google.api-client:google-api-client-android:1.32.1")
    implementation("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")

    // Google Authentication
    implementation("com.google.android.gms:play-services-auth:19.2.0")

    // For using NetHttpTransport
    implementation("com.google.http-client:google-http-client-gson:1.39.2")
    implementation("com.google.http-client:google-http-client:1.39.2")
}