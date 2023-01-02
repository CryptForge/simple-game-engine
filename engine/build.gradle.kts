plugins {
    id("java")
    id("maven-publish")
}

group = "me.cryptforge"
version = "1.0"

val lwjglVersion = "3.3.1"
val windowsNatives = "natives-windows"
val linuxNatives = "natives-linux"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.joml:joml:1.10.5")
    compileOnly("org.jetbrains:annotations:23.1.0")

    // LWJGL
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-nfd")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")

    // Windows natives
    runtimeOnly("org.lwjgl", "lwjgl", classifier = windowsNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = windowsNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = windowsNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = windowsNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = windowsNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = windowsNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = windowsNatives)

    // Linux natives
    runtimeOnly("org.lwjgl", "lwjgl", classifier = linuxNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = linuxNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = linuxNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = linuxNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = linuxNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = linuxNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = linuxNatives)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.cryptforge"
            artifactId = "simple-game-engine"
            version = this.version

            from(components["java"])
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}