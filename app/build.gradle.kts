plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application

    kotlin("jvm")
}

dependencies {
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.23.1")

    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.mockk:mockk:1.13.8")
}

application {
    // Define the Fully Qualified Name for the application main class
    mainClass.set("patterns.AppKt")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}