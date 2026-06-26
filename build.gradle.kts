plugins {
    kotlin("jvm") version "2.4.0"
    id("com.gradleup.shadow") version "9.4.2"
    id("dev.detekt") version "2.0.0-alpha.3"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.bstats:bstats-bukkit:3.2.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testImplementation("org.mockito:mockito-core:5.17.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.17.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.13.4")
    testImplementation("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
}

detekt {
    config.setFrom(files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcollection-literals")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val copyPlugin by tasks.registering(Copy::class) {
    description = "Copies the plugin jar to the plugins folder"
    dependsOn(tasks.shadowJar)
    val pluginDir = System.getenv("PLUGIN_DIR") ?: "${rootProject.projectDir}/plugins"
    from("build/libs")
    into(pluginDir)
    include("*-all.jar")
}

tasks {
    shadowJar {
        relocate("org.bstats", "${project.group}.bstats")
    }

    compileJava {
        options.release.set(8)
    }

    compileKotlin {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }

    compileTestKotlin {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }

    build {
        dependsOn(shadowJar)
        finalizedBy(copyPlugin)
    }

    test {
        useJUnitPlatform()
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
