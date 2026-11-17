val fastboardVersion = project.property("fastboardVersion") as String

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.252-stable")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}