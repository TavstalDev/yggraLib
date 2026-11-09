rootProject.name = "yggraLib"

include(
    "yggra-api",
    "yggra-bukkit",
    "yggra-impl_legacy",
    "yggra-impl_v1.8.8",
    "yggra-impl_v1.12",
    "yggra-impl_v1.13",
    "yggra-impl_v1.14",
    "yggra-impl_modern",
    "yggra-impl_v1.20.4",
    "yggra-impl_v1.21",
    "yggra-impl_v1.21.5",
    "yggra-bootstrap",
    "yggra-test-plugin"
)

project(":yggra-impl_legacy").projectDir = file("implementation/yggra-impl_legacy")
project(":yggra-impl_v1.8.8").projectDir = file("implementation/yggra-impl_v1.8.8")
project(":yggra-impl_v1.12").projectDir = file("implementation/yggra-impl_v1.12")
project(":yggra-impl_v1.13").projectDir = file("implementation/yggra-impl_v1.13")
project(":yggra-impl_v1.14").projectDir = file("implementation/yggra-impl_v1.14")
project(":yggra-impl_modern").projectDir = file("implementation/yggra-impl_modern")
project(":yggra-impl_v1.20.4").projectDir = file("implementation/yggra-impl_v1.20.4")
project(":yggra-impl_v1.21").projectDir = file("implementation/yggra-impl_v1.21")
project(":yggra-impl_v1.21.5").projectDir = file("implementation/yggra-impl_v1.21.5")