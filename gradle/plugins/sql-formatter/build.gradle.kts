plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.sql.formatter)
}

gradlePlugin {
    plugins {
        create("sql-formatter") {
            id = "mms.build.sql-formatter"
            implementationClass = "mms.build.SqlFormatPlugin"
        }
    }
}
