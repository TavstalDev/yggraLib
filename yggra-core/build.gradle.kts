plugins {
    id("com.gradleup.shadow") version "9.3.1"
}

val paperApiVersion = project.property("paperApiVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String
val hikariVersion = project.property("hikariVersion") as String
val caffeineVersion = project.property("caffeineVersion") as String
val redisVersion = project.property("redisVersion") as String
val spiGuiVersion = project.property("spiGuiVersion") as String
val bstatsVersion = project.property("bstatsVersion") as String
val fastboardVersion = project.property("fastboardVersion") as String

dependencies {

    compileOnly("io.papermc.paper:paper-api:${paperApiVersion}")
    compileOnly("net.kyori:adventure-api:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-minimessage:${adventureApiVersion}")
    compileOnly("com.zaxxer:HikariCP:${hikariVersion}")
    compileOnly("com.github.ben-manes.caffeine:caffeine:${caffeineVersion}")
    compileOnly("redis.clients:jedis:${redisVersion}")

    compileOnly("com.samjakob:SpiGUI:${spiGuiVersion}")
    compileOnly("org.bstats:bstats-bukkit:${bstatsVersion}")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")

    implementation(project(":yggra-items-api"))
    implementation(project(":yggra-items-impl_1.20.4"))
    implementation(project(":yggra-items-impl_1.21"))
    implementation(project(":yggra-items-impl_1.21.4"))
    implementation(project(":yggra-items-impl_1.21.5"))
}

// Disable the default JAR task
tasks.jar {
    enabled = false
}

// Configure the Shadow JAR task
tasks.shadowJar {
    archiveClassifier.set("") // Set the classifier for the JAR
    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot" // Add custom manifest attributes
    }

    exclude("com/google/**")
    exclude("org/jspecify/**")
    exclude("org/slf4j/**")
}

// Ensure the Shadow JAR task runs during the build process
tasks.build {
    dependsOn(tasks.shadowJar)
}