plugins {
    kotlin("jvm") version "1.3.50"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testCompile("org.assertj:assertj-core:3.11.1")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
}