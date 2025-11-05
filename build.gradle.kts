plugins {
    id("net.neoforged.moddev") version "2.0.107"
}


// Toolchain versions
val minecraftVersion: String = "1.21.1"
val neoForgeVersion: String = "21.1.197"
val parchmentVersion: String = "2024.11.17"
val parchmentMinecraftVersion: String = "1.21.1"

// Dependency versions
val emiVersion: String = "1.1.22+1.21.1"
val jeiVersion: String = "19.25.0.321"
val patchouliVersion: String = "1.21.1-92-NEOFORGE"
val tfcVersion: String = "4.0.7-beta"

val modId: String = "decay_2012"
val modVersion: String = "3.0.0"
val modJavaVersion: String = "21"
val modIsInCI: Boolean = !modVersion.contains("-indev")
val modDataOutput: String = "src/generated/resources"


val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "modId" to modId,
        "modVersion" to modVersion,
        "minecraftVersionRange" to "[$minecraftVersion]",
        "neoForgeVersionRange" to "[$neoForgeVersion,)",
        "patchouliVersionRange" to "[$patchouliVersion,)",
        "jeiVersionRange" to "[$jeiVersion,)"
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

neoForge {
    version = neoForgeVersion // this is here because declaring a neoForge version enables 'additionalRuntimeClasspath'
}

base {
    archivesName.set("Decay_2012-NeoForge-$minecraftVersion")
    group = "com.eerussianguy.decay_2012"
    version = modVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
}

repositories {
    mavenCentral()
    mavenLocal()
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com/") }
        filter { includeGroup("dev.emi") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.blamejared.com/") }
        filter { includeGroup("mezz.jei") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.blamejared.com") }
        filter { includeGroup("vazkii.patchouli") }
    }
    exclusiveContent {
        forRepository { maven("https://www.cursemaven.com") }
        filter { includeGroup("curse.maven") }
    }
}

sourceSets {
    main {
        resources {
            srcDir(modDataOutput)
            srcDir(generateModMetadata)
        }
    }
    create("data")
}

neoForge {
    addModdingDependenciesTo(sourceSets["data"])
    validateAccessTransformers = true

    parchment {
        minecraftVersion.set(parchmentMinecraftVersion)
        mappingsVersion.set(parchmentVersion)
    }

    runs {
        configureEach {
            // Only JBR allows enhanced class redefinition, so ignore the option for any other JDKs
            jvmArguments.addAll("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition", "-ea")
        }
        register("client") {
            client()
            gameDirectory = file("run/client")
        }
        register("server") {
            server()
            gameDirectory = file("run/server")
            programArgument("--nogui")
        }
        register("data") {
            data()
            sourceSet = sourceSets["data"]
            programArguments.addAll("--all", "--mod", modId, "--output", file(modDataOutput).absolutePath, "--existing",  file("src/main/resources").absolutePath)
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["data"])
        }
    }

    ideSyncTask(generateModMetadata)
}

dependencies {
    // EMI
    runtimeOnly("dev.emi:emi-neoforge:${emiVersion}")

    // JEI
    runtimeOnly("mezz.jei:jei-${minecraftVersion}-neoforge:${jeiVersion}")

    // Patchouli
    // We need to compile against the full JAR, not just the API, because we do some egregious hacks.
    implementation("vazkii.patchouli:Patchouli:$patchouliVersion")
    "dataImplementation"("vazkii.patchouli:Patchouli:$patchouliVersion")

    // TFC
    implementation(group = "curse.maven", name = "terrafirmacraft-302973", version = "7072352")
    "dataImplementation"(group = "curse.maven", name = "terrafirmacraft-302973", version = "7072352")

    // ModernFix - useful at runtime for significant memory savings in TFC in dev (see i.e. wall block shape caches)
    runtimeOnly(group = "curse.maven", name = "modernfix-790626", version = "6766126")
}


tasks {
    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
        }
    }

    named("neoForgeIdeSync") {
        dependsOn(generateModMetadata)
    }
}

