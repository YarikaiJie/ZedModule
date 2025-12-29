pluginManagement {
    repositories {
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/central") } // 替代maven2
        maven { setUrl("https://maven.aliyun.com/repository/public") } // central仓和jcenter仓的聚合仓
        maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public") }
        maven { setUrl("https://maven.aliyun.com/repository/jcenter") } // 替代jcenter
        maven { setUrl("https://maven.aliyun.com/repository/google") } // 替代google
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") } // gradle plugin
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
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/central") } // 替代maven2
        maven { setUrl("https://maven.aliyun.com/repository/public") } // central仓和jcenter仓的聚合仓
        maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public") }
        maven { setUrl("https://maven.aliyun.com/repository/jcenter") } // 替代jcenter
        maven { setUrl("https://maven.aliyun.com/repository/google") } // 替代google
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") } // gradle plugin
        google()
        mavenCentral()
    }
}

rootProject.name = "ZedModule"
include(":app")
include(":kotlinBase")
