val snakeYamlVersion = project.property("snakeYamlVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String
val triumphGuiVersion = project.property("triumphGuiVersion") as String

dependencies {
    compileOnly(project(":yggra-api"))

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-legacy:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-minimessage:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-plain:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${adventureApiVersion}")
    compileOnly("org.yaml:snakeyaml:${snakeYamlVersion}")
    compileOnly("dev.triumphteam:triumph-gui:${triumphGuiVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}