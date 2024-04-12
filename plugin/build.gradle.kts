plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.blossom)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("Plugin")
    }
    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }
}

blossom {
  val tokenRoute = "src/main/java/me/qeklydev/scoreboard/Constants.java"
  replaceToken("{version}", project.version, tokenRoute)
}

dependencies {
    implementation(project(":scoreboard-api"))

    compileOnly(libs.placeholders.modern)
    compileOnly(libs.placeholders.legacy)

    compileOnly(libs.command)
}
