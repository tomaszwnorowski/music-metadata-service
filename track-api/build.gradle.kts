plugins {
    id("mms.build.spring-boot-library-conventions")
    id("mms.build.fixture-conventions")
}

dependencies {
    // main
    api(project(":core"))
}
