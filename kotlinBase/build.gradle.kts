import org.gradle.kotlin.dsl.api

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.module.kotlin"
    compileSdk = 35

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("xml", "network_security", "@xml/network_release")
        }

        debug {
            resValue("xml", "network_security", "@xml/network")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    //android x
    api("androidx.appcompat:appcompat:1.7.1")
    api("androidx.appcompat:appcompat-resources:1.7.1")
    api("androidx.activity:activity-ktx:1.10.0")
    api("androidx.fragment:fragment-ktx:1.8.5")

    //material设计
    api("com.google.android.material:material:1.12.0")
    api("androidx.recyclerview:recyclerview:1.3.2")

    //viewpager2
    api("androidx.viewpager2:viewpager2:1.1.0")

    // sdk初始化核心库
    api("androidx.startup:startup-runtime:1.2.0")

    api("androidx.window:window:1.3.0")

    // 单元测试相关
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val kotlinVersion = "2.0.0"
    //kotlin核心库配置，对应根目录的build.gradle版本
    api("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    api("androidx.core:core-ktx:1.13.1")

    val coroutinesVersion = "1.6.4"
    // 兼容Kotlin 1.8+ Java 8+ Gradle: 7.4+
    // Kotlin 协程 - 统一版本，移除 native-mt（1.6.0+ 已统一）
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutinesVersion}")

    //----------------------------------生命周期全家桶 start-----------------------------
    val lifecycleVersion = "2.8.7"
    // ViewModel
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // LiveData
    api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // Lifecycles only (without ViewModel or LiveData)
    api("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    // Saved state module for ViewModel
    api("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    // optional - helpers for implementing LifecycleOwner in a Service
    api("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    api("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    // optional - ReactiveStreams support for LiveData
    api("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycleVersion")

    //----------------------------------生命周期全家桶 end-------------------------------

    //腾讯替代sp的库（https://github.com/Tencent/MMKV/wiki/home_cn）
    //不要随意更新
    api("com.tencent:mmkv-static:1.3.4")
    //保险load so库
    implementation("com.getkeepsafe.relinker:relinker:1.4.5")

    //gson 更新到2.11.0就出现 TypeToken type argument must not contain a type variable; 暂时不要往上，影响多个app
    api("com.google.code.gson:gson:2.11.0")

    //图片加载(https://github.com/bumptech/glide)
    api("com.github.bumptech.glide:glide:5.0.5")
//    ksp("com.github.bumptech.glide:ksp:4.16.0")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.YarikaiJie"
            artifactId = "ZedModule"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
