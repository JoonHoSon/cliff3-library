import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("java")
    id("org.jetbrains.dokka") version "1.9.10"
    alias(libs.plugins.kotlinPlugin)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

// 프로젝트 전역 속성 정의
allprojects {
    group = "net.cliff3"
    version = "0.3.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}


// 하위 프로젝트 전역 속성 정의
subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")

    dependencies {
        implementation(rootProject.libs.bundles.logback.bundle) // logback-core, logback-classic
        implementation(rootProject.libs.slf4j.api)
        implementation(rootProject.libs.commons.lang3)
        implementation(rootProject.libs.commons.io)
        testImplementation(rootProject.libs.junit.jupiter)
        testImplementation(rootProject.libs.spring.test)
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

// common
project(":common") {
    tasks.withType<Jar> {
        archiveBaseName.set("cliff3-common")
    }
}

project(":web-common") {
    tasks.withType<Jar> {
        archiveBaseName.set("cliff3-web-common")
    }

    dependencies {
        compileOnly(rootProject.libs.bundles.web.bundle)
        implementation(rootProject.libs.lucy.xss)
        implementation(rootProject.libs.aspectj.weaver)
        implementation(rootProject.libs.spring.test)
        testCompileOnly(rootProject.libs.spring.aop)
        implementation(project(":common"))
    }
}
