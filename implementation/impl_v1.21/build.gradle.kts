val fastboardVersion = project.property("fastboardVersion") as String
val actionBarVersion = project.property("actionBarVersion") as String
val bossBarVersion = project.property("bossBarVersion") as String
val titleVersion = project.property("titleVersion") as String

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
    compileOnly(project(":impl_modern"))
    compileOnly(files("../../libs/yggraActionBar-$actionBarVersion.jar"))
    compileOnly(files("../../libs/yggraBossBar-$bossBarVersion.jar"))
    compileOnly(files("../../libs/yggraTitle-$titleVersion.jar"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}