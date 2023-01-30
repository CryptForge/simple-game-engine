plugins {
    id("java")
}

group = "me.cryptforge.engine.ui"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject)
    implementation("org.joml:joml:1.10.5")
}