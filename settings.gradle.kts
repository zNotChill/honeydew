plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "honeydew-mono"

include("common", "host", "client")

include("host")