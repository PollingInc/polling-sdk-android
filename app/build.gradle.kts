plugins {
    //id("com.android.application") //for testing app
    id("com.android.library") //for building
}

android {
    namespace = "com.polling.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34  // Consider updating based on deprecation warning

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        //sourceCompatibility = JavaVersion.VERSION_1_8
        //targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "34.0.0"
}

dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.code.gson:gson:2.10")
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val outputDir = layout.buildDirectory.dir("outputs/aar").get().asFile
        val originalFileName = "app-release.aar"
        val newFileName = "polling-release.aar"

        tasks.register<DefaultTask>("renameReleaseAar") {
            doLast {
                val originalFile = File(outputDir, originalFileName)
                val newFile = File(outputDir, newFileName)

                if (originalFile.exists()) {
                    originalFile.renameTo(newFile)
                    println("Renamed AAR file to $newFileName")
                } else {
                    println("AAR file not found: $originalFile")
                }
            }
        }
    }
}

tasks.whenTaskAdded {
    if (name == "assembleRelease") {
        doLast {
            val outputDir = layout.buildDirectory.dir("outputs/aar").get().asFile
            val originalFile = File(outputDir, "app-release.aar")
            val newFile = File(outputDir, "polling-release.aar")

            if (originalFile.exists()) {
                if (originalFile.renameTo(newFile)) {
                    println("Renamed AAR file to polling-release.aar")
                } else {
                    println("Failed to rename AAR file.")
                }
            } else {
                println("AAR file not found: $originalFile")
            }
        }
    }
}
