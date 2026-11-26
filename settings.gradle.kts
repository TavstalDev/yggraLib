rootProject.name = "yggraLib"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(
    "api",
    "bukkit",
    "impl_old",
    "impl_legacy",
    "impl_v1.8.8",
    "impl_v1.9.4",
    "impl_v1.12",
    "impl_v1.13",
    "impl_v1.14",
    "impl_modern",
    "impl_v1.20.4",
    "impl_v1.21",
    "impl_v1.21.5",
    "bootstrap",
    "test-plugin"
)

project(":impl_old").projectDir = file("implementation/impl_old")
project(":impl_legacy").projectDir = file("implementation/impl_legacy")
project(":impl_v1.8.8").projectDir = file("implementation/impl_v1.8.8")
project(":impl_v1.9.4").projectDir = file("implementation/impl_v1.9.4")
project(":impl_v1.12").projectDir = file("implementation/impl_v1.12")
project(":impl_v1.13").projectDir = file("implementation/impl_v1.13")
project(":impl_v1.14").projectDir = file("implementation/impl_v1.14")
project(":impl_modern").projectDir = file("implementation/impl_modern")
project(":impl_v1.20.4").projectDir = file("implementation/impl_v1.20.4")
project(":impl_v1.21").projectDir = file("implementation/impl_v1.21")
project(":impl_v1.21.5").projectDir = file("implementation/impl_v1.21.5")