val fastboardVersion = project.property("fastboardVersion") as String
val snakeYamlVersion = project.property("snakeYamlVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String
val triumphGuiVersion = project.property("triumphGuiVersion") as String
val actionBarVersion = project.property("actionBarVersion") as String
val bossBarVersion = project.property("bossBarVersion") as String
val titleVersion = project.property("titleVersion") as String
val particleVersion = project.property("particleVersion") as String

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.13-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
    compileOnly(project(":impl_legacy"))
    compileOnly(files("../../libs/yggraActionBar-$actionBarVersion.jar"))
    compileOnly(files("../../libs/yggraBossBar-$bossBarVersion.jar"))
    compileOnly(files("../../libs/yggraTitle-$titleVersion.jar"))
    compileOnly(files("../../libs/yggraParticle-$particleVersion.jar"))
    compileOnly("fr.mrmicky:fastboard:${fastboardVersion}")
    compileOnly("org.yaml:snakeyaml:${snakeYamlVersion}")
    compileOnly("com.github.lidanthedev.triumph-gui:triumph-gui:${triumphGuiVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}