plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    // iOS targets
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    // Android target (JVM-based)
    jvm("android")

    sourceSets {
        commonMain.dependencies {
            // No external dependencies needed for game logic
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
