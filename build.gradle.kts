import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.4"
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

group = "com.nikitolproject"
version = "1.1.0"
description = "A plugin that notifies players when a streamer goes live on Twitch."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20231013")
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version
    )
    inputs.properties(props)
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}


tasks {
    assemble {
        dependsOn(reobfJar)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("okhttp3", "${project.group}.twitchwatcher.libs.okhttp3")
        relocate("okio", "${project.group}.twitchwatcher.libs.okio")
        relocate("org.json", "${project.group}.twitchwatcher.libs.json")
    }
}
