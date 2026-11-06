import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask

plugins {
    id("java-library")
    id("dev.s7a.gradle.minecraft.server") version "4.0.2"
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

val snakeYamlVersion = project.property("snakeYamlVersion") as String
val yamlConfigVersion = project.property("yamlConfigVersion") as String
val triumphGuiVersion = project.property("triumphGuiVersion") as String
val fastboardVersion = project.property("fastboardVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    //compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("org.yaml:snakeyaml:${snakeYamlVersion}")
    implementation("org.bspfsystems:yamlconfiguration:${yamlConfigVersion}")
    implementation("dev.triumphteam:triumph-gui:${triumphGuiVersion}")
    implementation("fr.mrmicky:fastboard:${fastboardVersion}")

    implementation("net.kyori:adventure-api:${adventureApiVersion}")
    implementation("net.kyori:adventure-text-serializer-legacy:${adventureApiVersion}")
    implementation("net.kyori:adventure-text-minimessage:${adventureApiVersion}")
    implementation("net.kyori:adventure-text-serializer-plain:${adventureApiVersion}")
    implementation("net.kyori:adventure-text-serializer-gson:${adventureApiVersion}")

    implementation(project(":yggra-core"))
}

configurations.runtimeClasspath {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
}

// Disable the default JAR task
tasks.jar {
    enabled = false
}

tasks.compileJava {
    dependsOn(":yggra-core:shadowJar")
}

// Configure the Shadow JAR task
tasks.shadowJar {
    dependsOn(":yggra-core:shadowJar")
    archiveClassifier.set("") // Set the classifier for the JAR
    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot" // Add custom manifest attributes
    }

    exclude("com/google/**")
    exclude("org/jspecify/**")
    exclude("org/slf4j/**")
    //exclude("net/kyori/**")
    exclude("org/bstats/**")

    relocate("org.yaml", "com.example.plugin.shadow.yaml")
    relocate("org.bspfsystems", "com.example.plugin.shadow.bspfsystems")
    relocate("dev.triumphteam", "com.example.plugin.shadow.triumphteam")
    relocate("fr.mrmicky", "com.example.plugin.shadow.mrmicky")
    relocate("net.kyori", "com.example.plugin.shadow.kyori")
}

// Ensure the Shadow JAR task runs during the build process
tasks.build {
    dependsOn(tasks.shadowJar)
}


val projectName = project.name
val buildDir = layout.buildDirectory

listOf(
    "1.8" to "1.8.8",
    "1.9" to "1.9.4",
    "1.10" to "1.10.2",
    "1.11" to "1.11.2",
    "1.12" to "1.12.2",
    "1.13" to "1.13.2",
    "1.14" to "1.14.4",
    "1.15" to "1.15.2",
    "1.16" to "1.16.5",
    "1.17" to "1.17.1",
    "1.18" to "1.18.2",
    "1.19" to "1.19.4",
    "1.20" to "1.20.4",
    "1.21" to "1.21",
    "1.21.4" to "1.21.4",
    "1.21.11" to "1.21.11",
    "26.1" to "26.1"
).forEach { (name, version) ->
    task<LaunchMinecraftServerTask>("testPlugin$name") {
        dependsOn("shadowJar")

        val serverDir = buildDir.dir("MinecraftServer$name")

        doFirst {
            copy {
                from(buildDir.file("libs/yggra-test-plugin-1.0.0.jar"))
                into(serverDir.get().dir("plugins"))
            }
        }

        serverDirectory.set(serverDir.map { it.asFile.absolutePath })
        jarUrl.set(LaunchMinecraftServerTask.JarUrl.Paper(version))
        agreeEula.set(true)
    }
}