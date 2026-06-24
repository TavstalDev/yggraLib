val paperApiVersion = project.property("paperApiVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String
val hikariVersion = project.property("hikariVersion") as String
val caffeineVersion = project.property("caffeineVersion") as String
val redisVersion = project.property("redisVersion") as String
val spiGuiVersion = project.property("spiGuiVersion") as String
val bstatsVersion = project.property("bstatsVersion") as String
val fastboardVersion = project.property("fastboardVersion") as String

dependencies {

    compileOnly("io.papermc.paper:paper-api:${paperApiVersion}")

    implementation("net.kyori:adventure-api:${adventureApiVersion}")
    implementation("net.kyori:adventure-text-minimessage:${adventureApiVersion}")

    implementation("com.zaxxer:HikariCP:${hikariVersion}")
    implementation("com.github.ben-manes.caffeine:caffeine:${caffeineVersion}")
    implementation("redis.clients:jedis:${redisVersion}")
    implementation("com.samjakob:SpiGUI:${spiGuiVersion}")
    implementation("org.bstats:bstats-bukkit:${bstatsVersion}")
    implementation("fr.mrmicky:fastboard:${fastboardVersion}")

}