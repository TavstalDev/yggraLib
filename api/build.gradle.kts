val snakeYamlVersion = project.property("snakeYamlVersion") as String
val yamlConfigVersion = project.property("yamlConfigVersion") as String
val hikariVersion = project.property("hikariVersion") as String
val caffeineVersion = project.property("caffeineVersion") as String
val redisVersion = project.property("redisVersion") as String

dependencies {
    compileOnly("org.yaml:snakeyaml:${snakeYamlVersion}")
    compileOnly("org.bspfsystems:yamlconfiguration:${yamlConfigVersion}")
    compileOnly("com.zaxxer:HikariCP:${hikariVersion}")
    compileOnly("com.github.ben-manes.caffeine:caffeine:${caffeineVersion}")
    compileOnly("redis.clients:jedis:${redisVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
    withJavadocJar()
    withSourcesJar()
}