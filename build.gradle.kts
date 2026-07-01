plugins {
    java
}

allprojects {
    group = "io.github.tavstaldev.yggra"
    version = "1.0.0"
}

val javaVersion = project.property("javaVersion") as String
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
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(javaVersion)
            sourceCompatibility = JavaVersion.toVersion(javaVersion)
            targetCompatibility = JavaVersion.toVersion(javaVersion)
        }
    }
}