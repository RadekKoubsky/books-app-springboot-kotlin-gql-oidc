plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.5.7"))

    constraints {
        // Spring Boot starters (managed by spring-boot-dependencies)
        api("org.springframework.boot:spring-boot-starter-graphql")
        api("org.springframework.boot:spring-boot-starter-web")
        api("org.springframework.boot:spring-boot-starter-actuator")
        api("org.springframework.boot:spring-boot-starter-jooq")

        // Kotlin
        api("com.fasterxml.jackson.module:jackson-module-kotlin")
        api("org.jetbrains.kotlin:kotlin-reflect")

        // Database
        api("org.postgresql:postgresql:42.7.4")
        api("org.flywaydb:flyway-core:10.21.0")
        api("org.flywaydb:flyway-database-postgresql:11.8.0")

        // jOOQ
        api("org.jooq:jooq-meta-extensions:3.19.27")
        api("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")

        // Monitoring
        api("io.micrometer:micrometer-registry-prometheus")

        // Testing
        api("io.mockk:mockk:1.13.10")
        api("io.kotest:kotest-runner-junit5:5.8.0")
        api("io.kotest:kotest-assertions-core:5.8.0")
        api("io.kotest:kotest-property:5.8.0")
        api("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    }
}
