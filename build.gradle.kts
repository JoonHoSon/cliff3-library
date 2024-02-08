import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("java")
//    kotlin("jvm") version "1.7.20"
//    kotlin("plugin.spring") version "1.6.21" apply false
    alias(libs.plugins.kotlinPlugin)
}

// 프로젝트 전역 속성 정의
allprojects {
    group = "net.cliff3"
    version = "0.3.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging.setShowStandardStreams(true) // gradle test console log
    }
}


// 하위 프로젝트 전역 속성 정의
subprojects {
    group = "net.cliff3"
    version = "0.3.0-SNAPSHOT"

    apply(plugin = "kotlin")

    dependencies {
        implementation(rootProject.libs.bundles.logback.bundle) // logback-core, logback-classic
        implementation(rootProject.libs.slf4j.api)
        implementation(rootProject.libs.commons.lang3)
        testImplementation(rootProject.libs.junit.jupiter)
        testImplementation(rootProject.libs.spring.test)
    }
}

// common
project(":common") {
    tasks.withType<Jar> {
        archiveBaseName.set("cliff3-common-%S".format(rootProject.version))
    }
}
