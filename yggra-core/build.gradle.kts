val paperApiVersion = project.property("paperApiVersion") as String
val adventureApiVersion = project.property("adventureApiVersion") as String
val hikariVersion = project.property("hikariVersion") as String
val caffeineVersion = project.property("caffeineVersion") as String
val spiGuiVersion = project.property("spiGuiVersion") as String

dependencies {

    compileOnly("io.papermc.paper:paper-api:${paperApiVersion}")

    implementation("net.kyori:adventure-api:${adventureApiVersion}")
    implementation("net.kyori:adventure-text-minimessage:${adventureApiVersion}")

    implementation("com.zaxxer:HikariCP:${hikariVersion}")
}