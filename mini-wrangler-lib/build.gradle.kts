import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion

plugins {
    kotlin("jvm") version "1.3.50"
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.simpleflatmapper:sfm-csv:7.0.3")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")
    implementation("org.jetbrains.kotlin:kotlin-script-util:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")

    testCompile("org.assertj:assertj-core:3.11.1")
    testImplementation("com.github.javafaker:javafaker:1.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

tasks.jar {}

tasks.test {
    useJUnitPlatform {
        excludeTags("measurement")
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register<Test>("measurePerformance") {
    useJUnitPlatform {
        includeTags("measurement")
    }
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
    maxHeapSize = "1g"
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
