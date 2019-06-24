import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.6"
    }
}
