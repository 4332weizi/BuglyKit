import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    `java-library`
}

group = "com.github.bugly"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    api("io.reactivex.rxjava3:rxjava:3.1.3")
    api("io.reactivex.rxjava3:rxkotlin:3.0.1")
    api("com.squareup.okhttp3:okhttp:4.9.3")
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}