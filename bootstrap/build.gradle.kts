plugins {
    id("com.gradleup.shadow") version "9.3.1"
    id("maven-publish")
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
val actionBarVersion = project.property("actionBarVersion") as String
val bossBarVersion = project.property("bossBarVersion") as String
val titleVersion = project.property("titleVersion") as String
val particleVersion = project.property("particleVersion") as String

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.zaxxer:HikariCP:${hikariVersion}")
    compileOnly("com.github.ben-manes.caffeine:caffeine:${caffeineVersion}")
    compileOnly("redis.clients:jedis:${redisVersion}")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly("com.github.lidanthedev.triumph-gui:triumph-gui:${triumphGuiVersion}")


    implementation("org.bstats:bstats-bukkit:${bstatsVersion}")
    implementation("net.byteflux:libby-bukkit:${libbyVersion}")
    implementation("org.bspfsystems:yamlconfiguration:${yamlConfigVersion}")
    implementation(project(":api"))
    implementation(project(":bukkit"))
    runtimeOnly(project(":impl_legacy"))
    runtimeOnly(project(":impl_modern"))
    runtimeOnly(project(":impl_v1.8.8"))
    runtimeOnly(project(":impl_v1.12"))
    runtimeOnly(project(":impl_v1.13"))
    runtimeOnly(project(":impl_v1.14"))
    runtimeOnly(project(":impl_v1.20.4"))
    runtimeOnly(project(":impl_v1.21"))
    runtimeOnly(project(":impl_v1.21.5"))
    implementation("com.github.TavstalDev:yggraActionBar:v$actionBarVersion")
    implementation("com.github.TavstalDev:yggraBossBar:v$bossBarVersion")
    implementation("com.github.TavstalDev:yggraTitle:v$titleVersion")
    implementation("com.github.TavstalDev:yggraParticle:v$particleVersion")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
    withJavadocJar()
    withSourcesJar()
}

configurations.runtimeClasspath {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

// Disable the default JAR task
tasks.jar {
    enabled = false
}

// Configure the Shadow JAR task
var shadeGroup = "com.github.TavstalDev.yggraLib.shadow"
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
    relocate("com.github.cryptomorin", "${shadeGroup}.XSeries")
    relocate("com.github.TavstalDev.yggraActionBar", "${shadeGroup}.yggraActionBar")
    relocate("com.github.TavstalDev.yggraBossBar", "${shadeGroup}.yggraBossBar")
    relocate("com.github.TavstalDev.yggraTitle", "${shadeGroup}.yggraTitle")
    relocate("com.github.TavstalDev.yggraParticle", "${shadeGroup}.yggraParticle")
}

// Ensure the Shadow JAR task runs during the build process
tasks.build {
    dependsOn(tasks.shadowJar)
}

val javadocProjects = listOf(
    project(":api"),
    project(":bukkit")
)

tasks.javadoc {
    javadocProjects.forEach { p ->
        val sourceSets = p.extensions.getByType<SourceSetContainer>()
        source(sourceSets["main"].allJava)
        classpath += sourceSets["main"].compileClasspath
    }
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        addStringOption("Xdoclint:none", "-quiet")
    }
    isFailOnError = false
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set("yggraLib")
    javadocProjects.forEach { p ->
        from(p.extensions.getByType<SourceSetContainer>()["main"].allSource)
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Jar>("javadocJar") {
    archiveBaseName.set("yggraLib")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.TavstalDev"
            artifactId = "yggraLib"
            artifact(tasks.shadowJar)
            artifact(tasks.named("javadocJar"))
            artifact(tasks.named("sourcesJar"))
        }
    }
}