val snakeYamlVersion = project.property("snakeYamlVersion") as String
val yamlConfigVersion = project.property("yamlConfigVersion") as String
val fastboardVersion = project.property("fastboardVersion") as String
val xseriesVersion = project.property("xseriesVersion") as String

dependencies {
    compileOnly("org.yaml:snakeyaml:${snakeYamlVersion}")
    compileOnly("org.bspfsystems:yamlconfiguration:${yamlConfigVersion}")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly("com.github.cryptomorin:XSeries:${xseriesVersion}")
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}