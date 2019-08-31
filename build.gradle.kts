plugins {
    kotlin("jvm") version "1.3.50"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")

    testCompile("org.assertj:assertj-core:3.11.1")
    testImplementation("com.github.javafaker:javafaker:1.0.1")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
}