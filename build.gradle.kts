import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "8.0"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
}

group = "com.rkoubsky"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Database
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.flywaydb:flyway-core:10.21.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.8.0")

    // jOOQ
    jooqGenerator("org.jooq:jooq-meta-extensions:3.19.27")
    jooqGenerator("org.postgresql:postgresql:42.7.4")
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")

    // Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.jupiter")
        exclude(group = "org.mockito")
    }
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

jooq {
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)
            jooqConfiguration.apply {
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"

                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties.addAll(
                            listOf(
                                org.jooq.meta.jaxb.Property().apply {
                                    key = "scripts"
                                    value = "src/main/resources/db/migration/**"
                                },
                                org.jooq.meta.jaxb.Property().apply {
                                    key = "sort"
                                    value = "semantic"
                                },
                                org.jooq.meta.jaxb.Property().apply {
                                    key = "unqualifiedSchema"
                                    value = "none"
                                },
                                org.jooq.meta.jaxb.Property().apply {
                                    key = "defaultNameCase"
                                    value = "lower"
                                },
                                org.jooq.meta.jaxb.Property().apply {
                                    key = "dialect"
                                    value = "POSTGRES"
                                },
                                org.jooq.meta.jaxb.Property().apply {
                                    key = "logLevel"
                                    value = "WARN"
                                }
                            )
                        )
                    }

                    generate.apply {
                        isRecords = true
                        isImmutablePojos = true
                    }

                    target.apply {
                        packageName = "com.rkoubsky.books.jooq"
                        directory = "build/generated-src/jooq/main"
                    }
                }
            }
        }
    }
}

// Suppress jOOQ code generation logging
tasks.named("generateJooq") {
    logging.captureStandardOutput(LogLevel.WARN)
    logging.captureStandardError(LogLevel.WARN)
}
