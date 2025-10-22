import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.changelog.ChangelogSectionUrlBuilder
import org.jetbrains.changelog.date

plugins {
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("dev.yumi.gradle.licenser") version "2.1.1"
    id("com.modrinth.minotaur") version "2.+"
    id("org.jetbrains.changelog") version "2.4.+"
    java
}

val mcVersions = property("supported_versions")!!
val targetVersion = mcVersions.toString().split(";")[0]
val fabricKotlinVersion = property("fabric_kotlin_version")!!

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

license {
    // Add a license header rule, at least one must be present.
    rule(file("codeformat/HEADER"))

    // Exclude/include certain file types, defaults are provided to easily deal with Java/Kotlin projects.
    include("**/*.java") // Include Java files into the file resolution.
    include("**/*.kt") // Include Java files into the file resolution.
    exclude("**/*.properties") // Exclude properties files from the file resolution.
}

changelog {
    version = property("mod_version")!! as String
    path = file("CHANGELOG.md").canonicalPath
    header = provider { "[${version.get()}] - ${date()}" }
    headerParserRegex = """(\d+\.\d+\.\d+(?:-[\w\d]+)?)""".toRegex()
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "[Unreleased]"
    groups = listOf("Added", "Changed", "Removed", "Fixed")
    lineSeparator = "\n"
    combinePreReleases = true
    sectionUrlBuilder = ChangelogSectionUrlBuilder { repositoryUrl, currentVersion, previousVersion, isUnreleased -> "foo" }
    outputFile = file("release-note.txt")
}

val lampVersion = property("lamp_version")

dependencies {
    minecraft("com.mojang:minecraft:$targetVersion")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    // lamp
    modImplementation("io.github.revxrsal:lamp.common:$lampVersion")?.let { include(it) }
    modImplementation("io.github.revxrsal:lamp.fabric:$lampVersion")?.let { include(it) }
    modImplementation("io.github.revxrsal:lamp.brigadier:$lampVersion")?.let { include(it) }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
            filter<ReplaceTokens>("tokens" to mapOf(
                "supported_versions" to mcVersions.toString().split(";").joinToString("\",\""),
                "version" to project.version,
                "fabric_kotlin_version" to fabricKotlinVersion
            ))
        }
    }

    jar {
        from("LICENSE")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        // select the repositories you want to publish to
        repositories {
            // uncomment to publish to the local maven
            // mavenLocal()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}