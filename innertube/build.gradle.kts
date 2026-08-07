plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
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
    
    // Rhino for JS Deobfuscation
    implementation("org.mozilla:rhino:1.7.15") 
    
    testImplementation(libs.junit)
}
