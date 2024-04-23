@file:Suppress("UnstableApiUsage")

rootProject.name = "ProBoard"

/*
 * Inclusion settings for common subprojects.
 */
arrayOf("api", "plugin").forEach {
    val project = ":proboard-$it"
    include(project)
    project(project).projectDir = file(it)
}
