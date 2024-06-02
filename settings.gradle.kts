plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "jsbsp-server"

include("jsbsp-boot")
include("jsbsp-auth")
include("jsbsp-card")
include("jsbsp-common")
include("jsbsp-cx369")
include("jsbsp-schedule")
include("jsbsp-util")
