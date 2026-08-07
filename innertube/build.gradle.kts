import java.io.File

plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)
}

// --- BEGIN FILE MIGRATION SCRIPT ---
// This runs during the Gradle configuration phase, before compilation starts.
// It automatically moves the new "com.music.innertube" files into "com.arturo254.innertube",
// fixes their package declarations, and removes the old, broken files.
val oldJavaDir = file("src/main/java/com/arturo254/innertube")
val newKotlinDir = file("src/main/kotlin/com/music/innertube")
val targetKotlinDir = file("src/main/kotlin/com/arturo254/innertube")

if (oldJavaDir.exists()) {
    oldJavaDir.deleteRecursively()
}

if (newKotlinDir.exists()) {
    newKotlinDir.copyRecursively(targetKotlinDir, overwrite = true)
    newKotlinDir.deleteRecursively()
    
    val musicDir = file("src/main/kotlin/com/music")
    if (musicDir.exists() && musicDir.listFiles()?.isEmpty() == true) {
        musicDir.deleteRecursively()
    }
}

if (targetKotlinDir.exists()) {
    targetKotlinDir.walkTopDown().filter { it.isFile && it.extension == "kt" }.forEach { ktFile ->
        val originalContent = ktFile.readText()
        val newContent = originalContent.replace("com.music.innertube", "com.arturo254.innertube")
        if (originalContent != newContent) {
            ktFile.writeText(newContent)
        }
    }
}
// --- END FILE MIGRATION SCRIPT ---

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.client.encoding)
    implementation(libs.brotli)
    
    // Explicitly defining these to fix unresolved references for 'schabi' (NewPipe) and 'rhino'
    implementation("com.github.mostafaalagamy:MetroExtractor:9d54d9c")
    implementation("org.mozilla:rhino:1.7.15")
    
    testImplementation(libs.junit)
}
