val snakeYamlVersion = project.property("snakeYamlVersion") as String
val yamlConfigVersion = project.property("yamlConfigVersion") as String
val fastboardVersion = project.property("fastboardVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String
val triumphGuiVersion = project.property("triumphGuiVersion") as String
val actionBarVersion = project.property("actionBarVersion") as String
val bossBarVersion = project.property("bossBarVersion") as String
val titleVersion = project.property("titleVersion") as String
val particleVersion = project.property("particleVersion") as String
val xseriesVersion = project.property("xseriesVersion") as String

dependencies {
    compileOnly("org.yaml:snakeyaml:${snakeYamlVersion}")
    compileOnly("org.bspfsystems:yamlconfiguration:${yamlConfigVersion}")
    compileOnly("com.destroystokyo.paper:paper-api:1.13-R0.1-SNAPSHOT")
    compileOnly("com.github.cryptomorin:XSeries:${xseriesVersion}")
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
    compileOnly(project(":impl_old"))
    compileOnly(project(":impl_legacy"))
    compileOnly("com.github.TavstalDev:yggraActionBar:v$actionBarVersion")
    compileOnly("com.github.TavstalDev:yggraBossBar:v$bossBarVersion")
    compileOnly("com.github.TavstalDev:yggraTitle:v$titleVersion")
    compileOnly("com.github.TavstalDev:yggraParticle:v$particleVersion")
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly("com.github.lidanthedev.triumph-gui:triumph-gui:${triumphGuiVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}