plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly(libs.configurate)

    compileOnly(libs.scoreboard.api)
    runtimeOnly(libs.scoreboard.impl)
    runtimeOnly(libs.scoreboard.modern)
}
