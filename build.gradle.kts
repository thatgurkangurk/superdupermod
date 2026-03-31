import me.modmuss50.mpp.ReleaseType
import net.fabricmc.loom.task.FabricModJsonV1Task
import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.changelog.ChangelogSectionUrlBuilder
import org.jetbrains.changelog.date

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.yumiGradleLicenser)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.jetbrains.changelog)
    alias(libs.plugins.modPublishPlugin)
    java
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    maven("https://maven.terraformersmc.com/") { name = "Terraformers" } // mod menu
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
    maven("https://maven.nucleoid.xyz") // player data api
    maven {
        name = "pauli.fyi"
        url = uri("https://repo.pauli.fyi/releases")
    }
}

license {
    rule(file("codeformat/HEADER"))

    include("**/*.java")
    include("**/*.kt")
    exclude("**/*.properties")
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
    sectionUrlBuilder = ChangelogSectionUrlBuilder { _, _, _, _ -> "foo" }
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

    file.set(tasks.jar.get().archiveFile)
    modLoaders.add("fabric")

    modrinth {
        announcementTitle.set(project.version.toString())
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectId.set("s2RXyQ1L")
        minecraftVersions.add(libs.versions.minecraft.get())

        embeds("fabric-permissions-api")

        requires(
            "fabric-language-kotlin",
            "fabric-api",
            "silk"
        )
    }

    github {
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
        repository.set("thatgurkangurk/superdupermod")
        commitish.set("main")
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("superdupermod") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runs {
        named("server") {
            server()

            runDir("run/server")
        }
    }

    runs {
        named("client") {
            client()
            ideConfigGenerated(true)
            programArg("--username=Dev")
        }
    }
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

dependencies {
    minecraft(libs.minecraft)

    implementation(libs.bundles.fabric)

    // fabric permissions api
    implementation(libs.fabricPermissionsApi)

    // silk
    implementation(libs.bundles.silk)

    // mod menu
    runtimeOnly("com.terraformersmc:modmenu:18.0.0-alpha.8")

    implementation(libs.playerDataApi)
    include(libs.playerDataApi)
}

tasks.register<FabricModJsonV1Task>("generateModJson") {
    outputFile = file(layout.buildDirectory.file("resources/main/fabric.mod.json"))

    json {
        modId = "superdupermod"
        version = project.version.toString()

        name = "Super Duper Mod"
        description = "a super duper amazing mod"
        author {
            name = "Gurkan"
        }
        licenses.add("MPL-2.0")
        icon("./assets/icon.png")
        environment = "*"


        entrypoint("main", "me.gurkz.superdupermod.SuperDuperMod", "kotlin")
        entrypoint("client", "me.gurkz.superdupermod.client.SuperDuperClient", "kotlin")
        entrypoint("fabric-datagen", "me.gurkz.superdupermod.client.SuperDuperDataGenerator", "kotlin")

        mixin("superdupermod.mixins.json")

        depends("fabricloader", ">=${libs.versions.fabric.loader.get()}")
        depends("fabric-api", ">=${libs.versions.fabric.api.get()}")
        depends("fabric-language-kotlin", ">=${libs.versions.fabric.kotlin.get()}")
        depends("fabric-permissions-api-v0", "*")

        depends("minecraft", "~${libs.versions.minecraft.get()}")

        libs.bundles.silk.get().forEach { dep ->
            depends(
                dep.module.name,
                ">=${libs.versions.silk.get()}"
            )
        }
    }
}

tasks {
    processResources {
        dependsOn("generateModJson")
        inputs.property("version", project.version)
    }

    jar {
        from("LICENSE")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        javaParameters = true
    }
}

java {
    withSourcesJar()
}