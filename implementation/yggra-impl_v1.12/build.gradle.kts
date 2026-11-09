val fastboardVersion = project.property("fastboardVersion") as String
val snakeYamlVersion = project.property("snakeYamlVersion") as String
val triumphGuiVersion = project.property("triumphGuiVersion") as String

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.12-R0.1-SNAPSHOT")
    compileOnly(project(":yggra-api"))
    compileOnly(project(":yggra-bukkit"))
    compileOnly(project(":yggra-impl_legacy"))
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly("org.yaml:snakeyaml:${snakeYamlVersion}")
    compileOnly("dev.triumphteam:triumph-gui:${triumphGuiVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}