package com.arturo254.innertube

import okhttp3.OkHttpClient
import okhttp3.Request
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

object YouTubeExtractor {
    private val client = OkHttpClient.Builder().build()

    private val initLock = Any()

    private var cachedPlayerJs: String? = null
    private var deobfuscateJsCode: String? = null
    private var deobfuscateFuncName: String? = null
    private var transformNJsCode: String? = null
    private var transformNFuncName: String? = null
    private var currentResolvedUrl: String? = null

    private var sigScope: Scriptable? = null
    private var sigFunction: Function? = null
    private var nScope: Scriptable? = null
    private var nFunction: Function? = null
    private val rhinoLock = Any()

    var cacheDir: File? = null

    val isReady: Boolean
        get() = deobfuscateJsCode != null && transformNJsCode != null

    fun getSignatureTimestamp(): Int {
        return synchronized(initLock) {
            try {
                val playerJs = getPlayerJs()
                val match = Regex("""signatureTimestamp:(\d+)""").find(playerJs)
                match?.groupValues?.get(1)?.toInt() ?: 0
            } catch (e: Exception) {
                0
            }
        }
    }

    fun ensureInitialized() {
        synchronized(initLock) {
            if (isReady) return
            try {
                val js = getPlayerJs()
                if (js.isNotEmpty()) {
                    runCatching { prepareSignatureDeobfuscator(js) }
                    runCatching { prepareThrottlingDeobfuscator(js) }
                }
                if (isReady) {
                    runCatching { ensureRhinoCompiled() }
                }
            } catch (e: Exception) {
                println("[YouTubeExtractor] ensureInitialized failed: ${e.message}")
            }
        }
    }

    private fun loadCache(resolvedPlayerJsUrl: String): Boolean {
        val dir = cacheDir ?: return false
        try {
            val cachedUrlFile = File(dir, "yt_player_url.txt")
            if (!cachedUrlFile.exists()) return false
            val cachedUrl = cachedUrlFile.readText().trim()
            if (cachedUrl != resolvedPlayerJsUrl) {
                println("[YouTubeExtractor] Cache is stale: cached=$cachedUrl, resolved=$resolvedPlayerJsUrl")
                return false
            }

            val sigJsFile = File(dir, "yt_sig_js.txt")
            val sigFuncFile = File(dir, "yt_sig_func.txt")
            val nJsFile = File(dir, "yt_n_js.txt")
            val nFuncFile = File(dir, "yt_n_func.txt")

            if (sigJsFile.exists() && sigFuncFile.exists() && nJsFile.exists() && nFuncFile.exists()) {
                deobfuscateJsCode = sigJsFile.readText()
                deobfuscateFuncName = sigFuncFile.readText().trim()
                transformNJsCode = nJsFile.readText()
                transformNFuncName = nFuncFile.readText().trim()
                println("[YouTubeExtractor] Loaded decipher snippets from disk cache successfully")
                return true
            }
        } catch (e: Exception) {
            println("[YouTubeExtractor] Loading disk cache failed: ${e.message}")
        }
        return false
    }

    private fun saveCache(resolvedPlayerJsUrl: String) {
        val dir = cacheDir ?: return
        try {
            File(dir, "yt_player_url.txt").writeText(resolvedPlayerJsUrl)
            File(dir, "yt_player_cache_time.txt").writeText(System.currentTimeMillis().toString())
            deobfuscateJsCode?.let { File(dir, "yt_sig_js.txt").writeText(it) }
            deobfuscateFuncName?.let { File(dir, "yt_sig_func.txt").writeText(it) }
            transformNJsCode?.let { File(dir, "yt_n_js.txt").writeText(it) }
            transformNFuncName?.let { File(dir, "yt_n_func.txt").writeText(it) }
            println("[YouTubeExtractor] Saved decipher snippets to disk cache successfully")
        } catch (e: Exception) {
            println("[YouTubeExtractor] Saving disk cache failed: ${e.message}")
        }
    }

    private fun saveCacheIfComplete() {
        val resolvedUrl = currentResolvedUrl ?: return
        if (deobfuscateJsCode != null && transformNJsCode != null) {
            saveCache(resolvedUrl)
        }
    }

    private fun fetchUrl(url: String): String {
        println("[YouTubeExtractor] Fetching URL: $url")
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("[YouTubeExtractor] HTTP error ${response.code} for URL: $url")
                throw java.io.IOException("HTTP error: ${response.code}")
            }
            val body = response.body?.string() ?: ""
            println("[YouTubeExtractor] Successfully fetched URL: $url (length=${body.length})")
            return body
        }
    }

    private fun getPlayerJs(): String {
        cachedPlayerJs?.let { return it }

        val dir = cacheDir
        if (dir != null) {
            try {
                val timeFile = File(dir, "yt_player_cache_time.txt")
                if (timeFile.exists()) {
                    val lastSaved = timeFile.readText().trim().toLongOrNull() ?: 0L
                    val age = System.currentTimeMillis() - lastSaved
                    if (age in 0 until (24L * 3600 * 1000)) {
                        val sigJsFile = File(dir, "yt_sig_js.txt")
                        val sigFuncFile = File(dir, "yt_sig_func.txt")
                        val nJsFile = File(dir, "yt_n_js.txt")
                        val nFuncFile = File(dir, "yt_n_func.txt")
                        if (sigJsFile.exists() && sigFuncFile.exists() && nJsFile.exists() && nFuncFile.exists()) {
                            deobfuscateJsCode = sigJsFile.readText()
                            deobfuscateFuncName = sigFuncFile.readText().trim()
                            transformNJsCode = nJsFile.readText()
                            transformNFuncName = nFuncFile.readText().trim()
                            println("[YouTubeExtractor] Cache hit — loaded decipher snippets instantly (age=${age / 1000}s). No network call.")
                            cachedPlayerJs = "" 
                            return ""
                        }
                    }
                }
            } catch (e: Exception) {
                println("[YouTubeExtractor] Failed to read fresh cache: ${e.message}")
            }
        }

        println("[YouTubeExtractor] Cache miss — resolving YouTube player JS URL...")
        val iframeApi = fetchUrl("https://www.youtube.com/iframe_api")
        val hashMatch = Regex("""player\/([a-z0-9]{8})\/""").find(iframeApi)
        val playerJsUrl = if (hashMatch != null) {
            val url = "https://www.youtube.com/s/player/${hashMatch.groupValues[1]}/player_ias.vflset/en_
