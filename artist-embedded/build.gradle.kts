plugins {
    id("mms.build.spring-boot-library-conventions")
    id("mms.build.sql-codegen-conventions")
    id("mms.build.test-conventions")
}

dependencies {
    // main
    api(project(":artist-api"))
    implementation(libs.spring.boot.starter.jooq)
    runtimeOnly(project(":db"))

    // test
    testRuntimeOnly(libs.test.testcontainers.core)
    testRuntimeOnly(libs.test.testcontainers.postgresql)

    testImplementation(testFixtures(project(":artist-api")))
}

configure<mms.build.FlywayJooqCodegenExtension> {
    packageName.set("mms.artist.embedded.jooq.codegen")
    modulePrefix.set("artist")
}

sourceSets {
    main {
        kotlin.srcDir(tasks.named("flywayJooqCodegen"))
    }
}
