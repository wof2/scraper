plugins {
    java
    application
}

group = "com.scraper"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "com.scraper.App"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("us.codecraft:webmagic-core:1.0.3")
    implementation("us.codecraft:webmagic-extension:1.0.3")
    implementation("org.jsoup:jsoup:1.22.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
}

tasks.test {
    useJUnitPlatform()
}
