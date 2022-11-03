plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "me.cryptforge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":engine"))
    implementation("org.joml:joml:1.10.5")
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "me.cryptforge.game.Main"
        )
    }
}