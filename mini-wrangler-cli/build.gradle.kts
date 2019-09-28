plugins {
    kotlin("jvm") version "1.3.50"
    application
}

dependencies {
    compile(project(":mini-wrangler-lib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("info.picocli:picocli:4.0.4")
}

application {
    mainClassName = "net.wolfgangwerner.miniwrangler.cli.WranglerKt"
}
