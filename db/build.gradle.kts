plugins {
    id("mms.build.spring-boot-library-conventions")
}

dependencies {
    // main
    runtimeOnly(libs.database.flyway.core)
    runtimeOnly(libs.database.postgresql.driver)
}
