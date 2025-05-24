plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    java
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:20.0.2")
    implementation("org.openjfx:javafx-fxml:20.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

javafx {
    version = "20.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("SymulacjaGraficzna")
}
jacoco {
    toolVersion = "0.8.11"
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // po testach wygeneruj raport
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // raport wymaga testów
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}