
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

// Sole requirement for application plugin. Still doesn't work (see below)
application {
    mainClass.set("en.talond.fileScrapingSocket.MainKt")
}

// Needed to prevent "Main Class Not Found Exception". A complete mystery
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "en.talond.fileScrapingSocket.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
