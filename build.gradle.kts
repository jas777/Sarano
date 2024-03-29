import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    `maven-publish`
}

//sourceSets {
//    main {
//        resources {
//            srcDirs("src/main/resources")
//        }
//    }
//}

group = "com.sarano"
version = project.property("version") as String

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://jitpack.io") }
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jas777/Sarano")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

val exposedVersion: String by project

dependencies {

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.4.10")

    // implementation("net.dv8tion:JDA:4.2.0_240")
    api("net.dv8tion:JDA:5.0.0-alpha.13") {
        exclude(module = "opus-java")
    }

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")

    api("org.jetbrains.exposed:exposed-core:$exposedVersion")
    api("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    api("com.jagrosh:jda-utilities:3.0.5")

    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.7")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("io.netty:netty-all:4.1.63.Final")

    implementation("me.grison:jtoml:1.0.0")

    implementation(kotlin("stdlib-jdk8"))

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "14"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "14"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "14"
}

java {
    withSourcesJar()
    withJavadocJar()
}