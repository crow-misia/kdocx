import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.publish.maven.MavenPom
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

val artifactName = "kdocx"
val artifactGroup = "io.github.zncmn"
group = artifactGroup
version = "1.0.0"

val pomUrl = "https://github.com/crow-misia/kdocx"
val pomScmUrl = "https://github.com/crow-misia/kdocx"
val pomIssueUrl = "https://github.com/crow-misia/kdocx/issues"
val pomDesc = "Word Document Template Engine written in Kotlin"
val githubRepo = "crow_misia/kdocx"
val pomLicenseName = "The Apache Software License, Version 2.0"
val pomLicenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
val pomLicenseDist = "repo"

dependencies {
    api(kotlin("stdlib"))
    api("org.apache.poi:poi-ooxml:4.1.0")
    api("org.apache.commons:commons-jexl3:3.1")

    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.17")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

fun MavenPom.addDependencies() = withXml {
    asNode().appendNode("dependencies").let { depNode ->
        configurations.compile.get().allDependencies.forEach {
            depNode.appendNode("dependency").apply {
                appendNode("groupId", it.group)
                appendNode("artifactId", it.name)
                appendNode("version", it.version)
            }
        }
    }
}

val publicationName = "core"
publishing {
    publications {
        create<MavenPublication>(publicationName) {
            groupId = artifactGroup
            artifactId = artifactName
            artifact(sourcesJar)
            from(components["kotlin"])
            pom.withXml {
                asNode().apply {
                    appendNode("description", pomDesc)
                    appendNode("name", artifactName)
                    appendNode("url", pomUrl)
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", pomLicenseName)
                        appendNode("url", pomLicenseUrl)
                        appendNode("distribution", pomLicenseDist)
                    }
                    appendNode("scm").apply {
                        appendNode("url", pomScmUrl)
                    }
                }
            }
        }
    }
}

fun findProperty(s: String) = project.findProperty(s) as String?
bintray {
    user = findProperty("bintray_user")
    key = findProperty("bintray_apikey")
    publish = true
    setPublications(publicationName)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = artifactName
        setLicenses("Apache-2.0")
        setLabels("kotlin")
        issueTrackerUrl = pomIssueUrl
        vcsUrl = pomScmUrl
        githubRepo = githubRepo
        description = pomDesc
        desc = description
    })
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.6"
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            events("passed", "skipped", "failed")
        }
    }
}

