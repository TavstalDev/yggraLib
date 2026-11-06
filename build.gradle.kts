plugins {
    java
}

allprojects {
    group = "io.github.tavstaldev.yggra"
    version = "1.0.0"
}

val javaVersion = project.property("javaVersion") as String
val jetbrainsAnnotationsVersion = project.property("jetbrainsAnnotationsVersion") as String
val lombokVersion = project.property("lombokVersion") as String
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
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://repo.alessiodp.com/releases")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:${jetbrainsAnnotationsVersion}")
        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    }

    configurations.configureEach {
        if (name.endsWith("RuntimeClasspath") || name.endsWith("TestRuntimeClasspath")) {
            attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
        }
    }
}