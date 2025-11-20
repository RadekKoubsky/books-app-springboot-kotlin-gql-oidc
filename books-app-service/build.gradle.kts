import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "8.0"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(platform(project(":platform")))

    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    jooqGenerator("org.jooq:jooq-meta-extensions")
    jooqGenerator("org.postgresql:postgresql")
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api")

    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("io.mockk:mockk")
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-property")
    testImplementation("io.kotest.extensions:kotest-extensions-spring")
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
                logging = org.jooq.meta.jaxb.Logging.WARN
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
