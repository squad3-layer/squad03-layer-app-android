pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven {
            val properties = java.util.Properties()
            val propertiesFile = rootProject.projectDir.resolve("local.properties")
            if (propertiesFile.exists()) {
                properties.load(propertiesFile.inputStream())
            }
            val myToken = properties.getProperty("github.token") ?: ""

            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/squad3-layer/ds-squad03-layer")
            credentials {
                username = "domleondev"
                password = myToken
            }
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Layer-App"
include(":feature:Notifications")
include(":feature:Authentication")
include(":feature:News")
include(":navigation")
include(":Main")
include(":core:services")
