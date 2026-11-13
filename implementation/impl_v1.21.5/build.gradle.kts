val fastboardVersion = project.property("fastboardVersion") as String

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
    compileOnly(project(":impl_modern"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}