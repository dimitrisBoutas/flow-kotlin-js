
plugins {
    kotlin("multiplatform") version "1.8.21"
    id("dev.petuska.npm.publish") version "3.3.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser()
        useEsModules()
        binaries.library()
        generateTypeScriptDefinitions()
    }
    sourceSets {
        val jsMain by getting {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.7.1")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-js:1.0.0-pre.550")

            }
        }
    }


    npmPublish {
        registries {
            register("npmjs") {
                uri.set("https://registry.npmjs.org")
                authToken.set("obfuscated")
            }
        }
    }
}
