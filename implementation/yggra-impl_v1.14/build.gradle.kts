val fastboardVersion = project.property("fastboardVersion") as String
val snakeYamlVersion = project.property("snakeYamlVersion") as String
val triumphGuiVersion = project.property("triumphGuiVersion") as String

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.14-R0.1-SNAPSHOT")
    compileOnly(project(":yggra-api"))
    compileOnly(project(":yggra-bukkit"))
    compileOnly(project(":yggra-impl_legacy"))
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("org.yaml:snakeyaml:${snakeYamlVersion}")
    compileOnly("dev.triumphteam:triumph-gui:${triumphGuiVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}