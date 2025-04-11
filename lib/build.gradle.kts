
plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.jspecify:jspecify:1.0.0")
    implementation(libs.jackson.core)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
