plugins {
    id("mms.build.spring-boot-library-conventions")
    id("mms.build.sql-codegen-conventions")
    id("mms.build.test-conventions")
}

dependencies {
    // main
    api(project(":engine-api"))
    api(project(":artist-api"))
    implementation(libs.spring.boot.starter.jooq)

    // needed only in runtime but it fixes Intellij highlight issue
    implementation(project(":artist-embedded"))

    runtimeOnly(project(":db"))

    // test
    testRuntimeOnly(libs.test.testcontainers.core)
    testRuntimeOnly(libs.test.testcontainers.postgresql)

    testImplementation(testFixtures(project(":artist-api")))
}

configure<mms.build.FlywayJooqCodegenExtension> {
    packageName.set("mms.engine.embedded.jooq.codegen")
    modulePrefix.set("engine")
}

sourceSets {
    main {
        kotlin.srcDir(tasks.named("flywayJooqCodegen"))
    }
}
