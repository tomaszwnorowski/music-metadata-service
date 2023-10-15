plugins {
    id("mms.build.flyway-jooq-codegen")
}

configure<mms.build.FlywayJooqCodegenExtension> {
    migrationsDir.set(project(":db").layout.projectDirectory.dir("src/main/resources/db/migration"))
}
