val fastboardVersion = project.property("fastboardVersion") as String

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly(project(":yggra-api"))
    compileOnly(project(":yggra-bukkit"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}