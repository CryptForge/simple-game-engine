import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

group = "me.cryptforge.engine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject)
    implementation(project("::ui"))
    implementation("org.joml:joml:1.10.5")
}

application {
    mainClass.set("me.cryptforge.demo.Main")
}

tasks.withType<ShadowJar> {
    manifest.attributes(
        "Main-Class" to "me.cryptforge.demo.Main"
    )
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}