import me.modmuss50.mpp.ReleaseType
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.changelog.ChangelogSectionUrlBuilder
import org.jetbrains.changelog.date
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
    id("maven-publish")
    alias(libs.plugins.yumiGradleLicenser)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.jetbrains.changelog)
    alias(libs.plugins.modPublishPlugin)
    java
}

val mcVersions = property("supported_versions")!!
val targetVersion = mcVersions.toString().split(";")[0]

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    maven("https://maven.parchmentmc.org/") { name = "ParchmentMC" } // parchment mappings (https://parchmentmc.org/docs/getting-started)
    maven("https://maven.fzzyhmstrs.me/") { name = "FzzyMaven" } // fzzy config (https://github.com/fzzyhmstrs/fconfig)
    maven("https://maven.terraformersmc.com/") { name = "Terraformers" } // mod menu
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") { name = "DevAuth" } // dev auth (https://github.com/DJtheRedstoner/DevAuth)
    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://api.modrinth.com/maven")
                name = "Modrinth"
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
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

fun getChangelog(version: String): String {
    return changelog.renderItem(
        changelog.get(version).withSummary(false),
        Changelog.OutputType.MARKDOWN
    )
}

publishMods {
    changelog = providers.provider { getChangelog(project.version.toString()) }
    type = ReleaseType.STABLE

    file.set(tasks.remapJar.get().archiveFile)
    modLoaders.add("fabric")

    modrinth {
        announcementTitle.set(project.version.toString())
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectId.set("s2RXyQ1L")
        minecraftVersions.add(targetVersion)
    }

    github {
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
        repository.set("thatgurkangurk/superdupermod")
        commitish.set("main")
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment)
    })

    modImplementation(libs.bundles.fabric)

    // lamp
    libs.bundles.lamp.get().forEach {
        modImplementation(it)
        include(it)
    }

    // fzzy config
    modImplementation(libs.fzzyConfig)

    // fabric permissions api
    modImplementation(libs.fabricPermissionsApi)

    // mod menu
    modRuntimeOnly("com.terraformersmc:modmenu:16.0.0-rc.1")

    // mods that i want for when im testing
    modRuntimeOnly("maven.modrinth:sodium:mc1.21.9-0.7.0-fabric")
    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.1")
    modRuntimeOnly("maven.modrinth:jade:20.0.5+fabric")
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
            filter<ReplaceTokens>("tokens" to mapOf(
                "loader_version" to libs.versions.fabric.loader.get(),
                "supported_versions" to mcVersions.toString().split(";").joinToString("\",\""),
                "version" to project.version,
                "fabric_kotlin_version" to libs.versions.fabric.kotlin.get(),
                "fzzy_config_version" to libs.versions.fzzyConfig.get()
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

tasks.withType<JavaCompile> {
    // Preserve parameter names in the bytecode
    options.compilerArgs.add("-parameters")
}

// optional: if you're using Kotlin
tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        javaParameters = true
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