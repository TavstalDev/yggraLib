plugins {
    id("com.gradleup.shadow") version "9.3.1"
}

val snakeYamlVersion = project.property("snakeYamlVersion") as String
val yamlConfigVersion = project.property("yamlConfigVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String
val hikariVersion = project.property("hikariVersion") as String
val caffeineVersion = project.property("caffeineVersion") as String
val redisVersion = project.property("redisVersion") as String
val triumphGuiVersion = project.property("triumphGuiVersion") as String
val bstatsVersion = project.property("bstatsVersion") as String
val fastboardVersion = project.property("fastboardVersion") as String
val libbyVersion = project.property("libbyVersion") as String

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.zaxxer:HikariCP:${hikariVersion}")
    compileOnly("com.github.ben-manes.caffeine:caffeine:${caffeineVersion}")
    compileOnly("redis.clients:jedis:${redisVersion}")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly("dev.triumphteam:triumph-gui:${triumphGuiVersion}")


    implementation("org.bstats:bstats-bukkit:${bstatsVersion}")
    implementation("net.byteflux:libby-bukkit:${libbyVersion}")
    implementation("org.bspfsystems:yamlconfiguration:${yamlConfigVersion}")
    implementation(project(":yggra-api"))
    implementation(project(":yggra-bukkit"))
    runtimeOnly(project(":yggra-impl_1.8.8"))
    runtimeOnly(project(":yggra-impl_1.20.4"))
    runtimeOnly(project(":yggra-impl_1.21"))
    runtimeOnly(project(":yggra-impl_1.21.5"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

configurations.runtimeClasspath {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
}

// Disable the default JAR task
tasks.jar {
    enabled = false
}

// Configure the Shadow JAR task
var shadeGroup = "io.github.tavstaldev.yggraLib.shadow"
tasks.shadowJar {
    archiveBaseName.set("yggraLib")
    archiveClassifier.set("") // Set the classifier for the JAR
    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot" // Add custom manifest attributes
    }

    exclude("com/google/**")
    exclude("org/jspecify/**")
    exclude("org/slf4j/**")
    exclude("net/kyori/**")
    exclude("dev/triumphteam/**")
    exclude("org/yaml/**")

    relocate("org.bstats", "${shadeGroup}.bstats")
    relocate("net.byteflux", "${shadeGroup}.libby")
    relocate("org.yaml", "${shadeGroup}.yaml") // Required by yamlconfiguration
    relocate("org.bspfsystems", "${shadeGroup}.bspfsystems")
}

// Ensure the Shadow JAR task runs during the build process
tasks.build {
    dependsOn(tasks.shadowJar)
}