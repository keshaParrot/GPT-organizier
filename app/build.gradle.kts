import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.keshaparrot.gptorganizier"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.keshaparrot.gptorganizier"
        minSdk = 30
        targetSdk = 34
        versionCode = 2
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas"
                    )
                )
            }
        }
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
    testImplementation(libs.mockito.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.core.testing)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.recyclerview)
    implementation(libs.material.v160)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok.v11826)
    implementation(libs.gson)


    // Google Drive API
    implementation(libs.google.api.client.android.v1321)
    implementation(libs.google.api.services.drive.vv3rev1971250)

    // Google Authentication
    implementation(libs.play.services.auth.v1920)

    // For using NetHttpTransport
    implementation(libs.google.http.client.gson)
    implementation(libs.google.http.client)

    //database room
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.rxjava3)
}