plugins {
    id("mms.build.spring-boot-library-conventions")
    id("mms.build.sql-codegen-conventions")
    id("mms.build.test-conventions")
}

dependencies {
    // main
    api(project(":track-api"))
    implementation(libs.spring.boot.starter.jooq)
    runtimeOnly(project(":db"))

    // test
    testRuntimeOnly(libs.test.testcontainers.core)
    testRuntimeOnly(libs.test.testcontainers.postgresql)

    testImplementation(testFixtures(project(":track-api")))
}

configure<mms.build.FlywayJooqCodegenExtension> {
    packageName.set("mms.track.embedded.jooq.codegen")
    modulePrefix.set("track")
}

sourceSets {
    main {
        kotlin.srcDir(tasks.named("flywayJooqCodegen"))
    }
}
