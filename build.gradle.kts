import me.modmuss50.mpp.ReleaseType
import net.fabricmc.loom.task.FabricModJsonV1Task
import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.changelog.ChangelogSectionUrlBuilder
import org.jetbrains.changelog.date
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

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
    maven("https://maven.nucleoid.xyz") // player data api
    maven {
        name = "pauli.fyi"
        url = uri("https://repo.pauli.fyi/releases")
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

    file.set(tasks.remapJar.get().archiveFile)
    modLoaders.add("fabric")

    modrinth {
        announcementTitle.set(project.version.toString())
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectId.set("s2RXyQ1L")
        minecraftVersions.add(targetVersion)

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

    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment)
    })

    modImplementation(libs.bundles.fabric)

    // fabric permissions api
    modImplementation(libs.fabricPermissionsApi)

    // silk
    modImplementation(libs.bundles.silk)

    // mod menu
    modRuntimeOnly("com.terraformersmc:modmenu:17.0.0")

    modImplementation(libs.playerDataApi)
    include(libs.playerDataApi)

    // mods that i want for when im testing
    //modRuntimeOnly("maven.modrinth:sodium:mc1.21.9-0.7.0-fabric")
    //modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.1")
    //modRuntimeOnly("maven.modrinth:jade:20.0.5+fabric")
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

        depends("minecraft", mcVersions.toString().split(";"))

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