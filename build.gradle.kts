import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    val kvisionVersion: String by System.getProperties()
    id("io.kvision") version kvisionVersion
}

version = "1.0.0-SNAPSHOT"
group = "com.example"

repositories {
    mavenCentral()
    jcenter()
    mavenLocal()
}

// Versions
val kotlinVersion: String by System.getProperties()
val kvisionVersion: String by System.getProperties()
val slf4jVersion: String by project

val webDir = file("src/frontendMain/web")
val mainClassName = "com.example.MainKt"

kotlin {
    js("frontend") {
        browser {
            commonWebpackConfig {
                outputFileName = "main.bundle.js"
            }
            runTask {
                sourceMaps = false
                devServer = KotlinWebpackConfig.DevServer(
                    open = false,
                    port = 3000,
                    proxy = mutableMapOf(
                        "/kv/*" to "http://localhost:8080",
                        "/kvws/*" to mapOf("target" to "ws://localhost:8080", "ws" to true)
                    ),
                    static = mutableListOf("$buildDir/processedResources/frontend/main")
                )
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }
    js("worker") {
        browser{
            commonWebpackConfig {
                outputPath = file("build/processedResources/frontend/main/")
                outputFileName = "worker.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val frontendMain by getting {
            resources.srcDir(webDir)
            dependencies {
                implementation("io.kvision:kvision:$kvisionVersion")
                implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
                implementation("io.kvision:kvision-bootstrap-css:$kvisionVersion")
                implementation("io.kvision:kvision-i18n:$kvisionVersion")
            }
            kotlin.srcDir("build/generated-src/frontend")
        }
        val workerMain by getting {
            dependencies {
                implementation("io.kvision:kvision:$kvisionVersion")
            }
        }
        val frontendTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("io.kvision:kvision-testutils:$kvisionVersion")
            }
        }
    }
}

tasks {
    register("workerDist") {
        dependsOn("workerBrowserProductionWebpack")
        inputs.dir("src/workerMain/kotlin")
        outputs.files("$buildDir/processedResources/frontend/main/worker.js")
        doLast {
            exec {
                executable = getNodeJsBinaryExecutable(rootProject)
                workingDir = file("${rootProject.buildDir}/js/packages/${rootProject.name}-worker")
                args(
                    "${rootProject.buildDir}/js/node_modules/webpack/bin/webpack.js",
                    "--config",
                    "${rootProject.buildDir}/js/packages/${rootProject.name}-worker/webpack.config.js"
                )
            }
        }
    }
}

fun getNodeJsBinaryExecutable(rootProject: Project): String {
    val nodeDir = org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.apply(rootProject).nodeJsSetupTaskProvider.get().destination
    val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")
    val nodeBinDir = if (isWindows) nodeDir else nodeDir.resolve("bin")
    val command = org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.apply(rootProject).nodeCommand
    val finalCommand = if (isWindows && command == "node") "node.exe" else command
    return nodeBinDir.resolve(finalCommand).absolutePath
}
