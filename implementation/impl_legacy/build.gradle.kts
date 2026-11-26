val fastboardVersion = project.property("fastboardVersion") as String
val snakeYamlVersion = project.property("snakeYamlVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String
val triumphGuiVersion = project.property("triumphGuiVersion") as String
val xseriesVersion = project.property("xseriesVersion") as String

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.12-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
    compileOnly(project(":impl_old"))
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly("com.github.cryptomorin:XSeries:${xseriesVersion}")
    compileOnly("net.kyori:adventure-api:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-legacy:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-minimessage:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-plain:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("org.yaml:snakeyaml:${snakeYamlVersion}")
    compileOnly("com.github.lidanthedev.triumph-gui:triumph-gui:${triumphGuiVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}