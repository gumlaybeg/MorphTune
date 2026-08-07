plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arturo254.innertube"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.client.encoding)
    implementation(libs.brotli)
    
    // Uses the alias from your existing libs.versions.toml
    implementation(libs.newpipe.extractor) 
    // Rhino for JS Deobfuscation (since it wasn't in your TOML)
    implementation("org.mozilla:rhino:1.7.15") 
    
    testImplementation(libs.junit)

    coreLibraryDesugaring(libs.desugaring)
}
