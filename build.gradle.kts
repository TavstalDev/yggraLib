plugins {
    java
    id("com.gradleup.shadow") version "9.3.1"
}

allprojects {
    group = "io.github.tavstaldev.yggra"
    version = "1.0.0"
}

val javaVersion = project.property("javaVersion") as String
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()

        maven("https://repo.papermc.io/repository/maven-public/")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(javaVersion)
            sourceCompatibility = JavaVersion.toVersion(javaVersion)
            targetCompatibility = JavaVersion.toVersion(javaVersion)
        }
    }
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

    dependencies {
        // Only merge bStats into the final jar, no other dependencies
        exclude { it.moduleGroup != "org.bstats" }
    }
    exclude("com/google/**")
    exclude("org/jspecify/**")

    // Relocate packages to avoid conflicts
    relocate("com.github.benmanes.caffeine", project.group.toString() + ".shadow.caffeine")
    relocate("com.samjakob.spigui", project.group.toString() + ".shadow.spigui")
    relocate("org.bstats", project.group.toString() + ".shadow.bstats")
}

// Ensure the Shadow JAR task runs during the build process
tasks.build {
    dependsOn(tasks.shadowJar)
}