import org.jetbrains.dokka.Platform

plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("org.jetbrains.dokka") version "1.4.32"
    id("maven-publish")
    signing
}
repositories {
    mavenCentral()
    jcenter()
}

val libVersion: String by project
val coroutinesVersion: String by project
val serializationVersion: String by project
val kluentVersion: String by project
val junitVersion: String by project

group = "io.taff"
version = "$libVersion${if (isReleaseBuild()) "" else "-SNAPSHOT"}"

kotlin {
    explicitApi()
}

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    test {
        useJUnitPlatform()
    }
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
    dokkaHtml {
        outputDirectory.set(buildDir.resolve("javadoc"))
        dokkaSourceSets {
            configureEach {
                jdkVersion.set(8)
                reportUndocumented.set(true)
                platform.set(Platform.jvm)
            }
        }
    }
}


val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    classifier = "javadoc"
    from(tasks.dokkaHtml)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tpasipanodya/deferred-json-builder")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)

            pom {
                name.set("Deferred JSON Builder")
                description.set("Deferred JSON Builder is a coroutine based pattern to split the fields and field value computation execution for generating JSON objects")
                url.set("https://github.com/tpasipanodya/deferred-json-builder")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/tpasipanodya/deferred-json-builder/blob/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("pasitaf")
                        name.set("Tafadzwa Pasipanodya")
                        email.set("tmpasipanodya@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/tpasipanodya/deferred-json-builder.git")
                    developerConnection.set("scm:git:https://github.com/tpasipanodya/deferred-json-builder.git")
                    url.set("https://github.com/tpasipanodya/deferred-json-builder/")
                    tag.set("HEAD")
                }
            }
        }
    }
}

fun isReleaseBuild() = System.getenv("IS_RELEASE_BUILD")?.toBoolean() == true
