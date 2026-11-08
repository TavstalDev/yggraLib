rootProject.name = "yggraLib"

include(
    "yggra-api",
    "yggra-bukkit",
    "yggra-impl_1.8.8",
    "yggra-impl_modern",
    "yggra-impl_1.20.4",
    "yggra-impl_1.21",
    "yggra-impl_1.21.5",
    "yggra-bootstrap",
    "yggra-test-plugin"
)

project(":yggra-impl_1.8.8").projectDir = file("implementation/yggra-impl_1.8.8")
project(":yggra-impl_modern").projectDir = file("implementation/yggra-impl_modern")
project(":yggra-impl_1.20.4").projectDir = file("implementation/yggra-impl_1.20.4")
project(":yggra-impl_1.21").projectDir = file("implementation/yggra-impl_1.21")
project(":yggra-impl_1.21.5").projectDir = file("implementation/yggra-impl_1.21.5")