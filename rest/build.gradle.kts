plugins {
    id("mms.build.spring-boot-library-conventions")
    id("mms.build.test-conventions")
}

dependencies {
    // main
    api(project(":core"))
    api(libs.spring.boot.starter.web)

    implementation(project(":artist-api"))
    runtimeOnly(project(":artist-embedded"))
    implementation(project(":track-api"))
    runtimeOnly(project(":track-embedded"))
    implementation(project(":engine-api"))
    runtimeOnly(project(":engine-embedded"))

    runtimeOnly(libs.json.jackson.kotlin)
    runtimeOnly(libs.json.jackson.datatype.jsr310)

    // test
    testImplementation(testFixtures(project(":artist-api")))
    testImplementation(testFixtures(project(":track-api")))
}
