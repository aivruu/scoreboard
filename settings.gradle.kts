@file:Suppress("UnstableApiUsage")

rootProject.name = "Scoreboard"

/*
 * Inclusion settings for common subprojects.
 */
arrayOf("api", "plugin").forEach {
    val project = ":scoreboard-$it"
    include(project)
    project(project).projectDir = file(it)
}
