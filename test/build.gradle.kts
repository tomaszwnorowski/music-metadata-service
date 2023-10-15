plugins {
    id("mms.build.spring-boot-library-conventions")
}

dependencies {
    // main
    api(libs.spring.boot.starter.test)
    api(libs.test.springmockk)
}
