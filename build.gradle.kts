import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
}

//sourceSets {
//    main {
//        resources {
//            srcDirs("src/main/resources")
//        }
//    }
//}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    implementation("net.dv8tion:JDA:4.2.0_240")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")

    implementation("org.jetbrains.exposed:exposed-core:0.29.1")

    implementation("me.grison:jtoml:1.0.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}