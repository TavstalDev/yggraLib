val fastboardVersion = project.property("fastboardVersion") as String
val actionBarVersion = project.property("actionBarVersion") as String
val bossBarVersion = project.property("bossBarVersion") as String
val titleVersion = project.property("titleVersion") as String
val particleVersion = project.property("particleVersion") as String

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
    compileOnly(project(":impl_modern"))
    compileOnly("com.github.TavstalDev:yggraActionBar:v$actionBarVersion")
    compileOnly("com.github.TavstalDev:yggraBossBar:v$bossBarVersion")
    compileOnly("com.github.TavstalDev:yggraTitle:v$titleVersion")
    compileOnly("com.github.TavstalDev:yggraParticle:v$particleVersion")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}