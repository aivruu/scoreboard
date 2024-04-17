plugins {
    java
    alias(libs.plugins.indra)
    alias(libs.plugins.spotless)
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "net.kyori.indra")
    apply(plugin = "com.diffplug.spotless")

    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()

        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.triumphteam.dev/snapshots/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    
    indra {
        javaVersions {
            target(17)
            minimumToolchain(17)
        }
    }

    spotless {
        java {
            licenseHeaderFile("$rootDir/license/header.txt")
            removeUnusedImports()
            trimTrailingWhitespace()
            indentWithSpaces(2)
        }
        kotlinGradle {
            trimTrailingWhitespace()
            indentWithSpaces(2)
        }
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    }

    tasks {
        compileJava {
            dependsOn("spotlessApply")
            options.compilerArgs.add("-parameters")
        }
    }
}
