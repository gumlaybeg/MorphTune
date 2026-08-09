package com.arturo254.opentune.lyrics

import android.content.Context
import com.arturo254.opentune.constants.EnablePaxsenixKey
import com.arturo254.opentune.utils.dataStore
import com.arturo254.opentune.utils.get
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.abs

// --- Models ---

@Serializable
private data class SearchResult(
    val id: String,
    val songName: String? = null,
    val trackName: String? = null,
    val artistName: String? = null,
    val albumName: String? = null,
    val duration: Int? = null,
    val artwork: String? = null
) {
    val displayName: String get() = trackName ?: songName ?: ""
    val displayArtist: String get() = artistName ?: ""
}

@Serializable
private data class LyricsContent(
    val timestamp: Long,
    val endtime: Long,
    val duration: Long,
    val structure: String? = null,
    val text: List<LyricText> = emptyList(),
    val background: Boolean = false,
    val backgroundText: List<LyricText> = emptyList(),
    val oppositeTurn: Boolean = false
)

@Serializable
private data class LyricText(
    val text: String,
    val timestamp: Long,
    val endtime: Long,
    val duration: Long,
    val part: Boolean = false
)

@Serializable
private data class LyricsMetadata(
    val songwriters: List<String> = emptyList()
)

@Serializable
private data class LyricsResponse(
    val type: String? = null,
    val metadata: LyricsMetadata? = null,
    val content: List<LyricsContent> = emptyList(),
    val elrc: String? = null,
    val elrcMultiPerson: String? = null,
    val ttmlContent: String? = null,
    val plain: String? = null
)

@Serializable
private data class AppleMusicSearchResponse(
    val results: AppleMusicResults,
    val resources: AppleMusicResources? = null
)

@Serializable
private data class AppleMusicResults(
    val songs: AppleMusicSongsResult? = null
)

@Serializable
private data class AppleMusicSongsResult(
    val data: List<AppleMusicSongData> = emptyList()
)

@Serializable
private data class AppleMusicSongData(
    val id: String,
    val type: String
)

@Serializable
private data class AppleMusicResources(
    val songs: Map<String, AppleMusicSongDetail>? = null
)

@Serializable
private data class AppleMusicSongDetail(
    val attributes: AppleMusicSongAttributes
)

@Serializable
private data class AppleMusicSongAttributes(
    val name: String,
    val artistName: String,
    val albumName: String? = null,
    val artwork: AppleMusicArtwork? = null,
    val url: String? = null,
    val durationInMillis: Long? = null
)

@Serializable
private data class AppleMusicArtwork(
    val url: String
)

// --- TTML Parser ---

object TTMLParser {
    data class Line(
        val startMs: Long,
        val endMs: Long,
        val text: String,
        val words: List<Word> = emptyList(),
        val agent: String? = null,
        val isBg: Boolean = false
    )
    data class Word(
        val startMs: Long,
        val endMs: Long,
        val text: String
    )

    fun parseTTML(ttml: String): List<Line> {
        val lines = mutableListOf<Line>()
        val pRegex = Regex("""<p\b([^>]*)>(.*?)</p>""", RegexOption.DOT_MATCHES_ALL)
        val spanRegex = Regex("""<span\b([^>]*)>(.*?)</span>""", RegexOption.DOT_MATCHES_ALL)
        val attrRegex = Regex("""(\w+(?::\w+)?)=["']([^"']*)["']""")

        for (pMatch in pRegex.findAll(ttml)) {
            val pAttrs = attrRegex.findAll(pMatch.groupValues[1]).associate { it.groupValues[1] to it.groupValues[2] }
            val pContent = pMatch.groupValues[2]

            val beginStr = pAttrs["begin"] ?: pAttrs["ttm:begin"] ?: "0"
            val endStr = pAttrs["end"] ?: pAttrs["ttm:end"] ?: "0"
            val agent = pAttrs["ttm:agent"] ?: pAttrs["agent"]
            val role = pAttrs["ttm:role"] ?: pAttrs["role"]
            val isBg = role == "x-bg" || pAttrs["tts:fontStyle"] == "italic"

            val startMs = parseTimeMs(beginStr)
            val endMs = parseTimeMs(endStr)

            val words = mutableListOf<Word>()
            val spanMatches = spanRegex.findAll(pContent).toList()

            if (spanMatches.isNotEmpty()) {
                for (sMatch in spanMatches) {
                    val sAttrs = attrRegex.findAll(sMatch.groupValues[1]).associate { it.groupValues[1] to it.groupValues[2] }
                    val wText = sMatch.groupValues[2].replace(Regex("""<[^>]+>"""), "").trim()
                    if (wText.isNotEmpty()) {
                        val wBegin = sAttrs["begin"] ?: beginStr
                        val wEnd = sAttrs["end"] ?: endStr
                        words.add(Word(parseTimeMs(wBegin), parseTimeMs(wEnd), wText))
                    }
                }
            }

            val cleanText = pContent.replace(Regex("""<[^>]+>"""), " ").replace(Regex("""\s+"""), " ").trim()
            if (cleanText.isNotEmpty()) {
                lines.add(Line(startMs, endMs, cleanText, words, agent, isBg))
            }
        }
        return lines
    }

    fun toLRC(lines: List<Line>): String {
        val sb = StringBuilder()
        for (line in lines) {
            val min = line.startMs / 60000
            val sec = (line.startMs % 60000) / 1000
            val ms = (line.startMs % 1000) / 10
            val timeStr = String.format(Locale.US, "[%02d:%02d.%02d]", min, sec, ms)

            val agentTag = when {
                line.isBg -> "{bg}"
                line.agent != null -> "{agent:${line.agent}}"
                else -> ""
            }

            sb.append(timeStr).append(agentTag).append(line.text).append("\n")

            if (line.words.isNotEmpty()) {
                val wordsStr = line.words.joinToString("|") {
                    "${it.text}:${it.startMs / 1000.0}:${it.endMs / 1000.0}"
                }
                sb.append("<").append(wordsStr).append(">\n")
            }
        }
        return sb.toString()
    }

    private fun parseTimeMs(time: String): Long {
        if (time.isBlank()) return 0L
        return try {
            if (time.endsWith("s")) {
                (time.dropLast(1).toDouble() * 1000).toLong()
            } else if (time.contains(":")) {
                val parts = time.split(":")
                if (parts.size == 3) {
                    val h = parts[0].toLong()
                    val m = parts[1].toLong()
                    val s = parts[2].toDouble()
                    ((h * 3600 + m * 60 + s) * 1000).toLong()
                } else if (parts.size == 2) {
                    val m = parts[0].toLong()
                    val s = parts[1].toDouble()
                    ((m * 60 + s) * 1000).toLong()
                } else 0L
            } else {
                time.toLongOrNull() ?: (time.toDoubleOrNull()?.times(1000))?.toLong() ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
}

// --- Paxsenix API Engine ---

object Paxsenix {
    private const val APPLE_MUSIC_API_BASE = "https://amp-api.music.apple.com/v1/catalog/us"

    private val httpClient by lazy {
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 10000
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }

            defaultRequest {
                url("https://lyrics.paxsenix.org")
                header("User-Agent", "OpenTune/1.0")
            }

            expectSuccess = true
        }
    }

    private val appleJson = Json { ignoreUnknownKeys = true }

    @Volatile
    private var appleTokenManager: AppleTokenManager? = null
    private val tokenManager: AppleTokenManager
        get() = appleTokenManager ?: synchronized(this) {
            appleTokenManager ?: AppleTokenManager(httpClient).also { appleTokenManager = it }
        }

    private val titleCleanupPatterns = listOf(
        Regex("""\s*\(.*?(official|video|audio|lyrics|lyric|visualizer|hd|hq|4k|remaster|remix|live|acoustic|version|edit|extended|radio|clean|explicit).*?\)""", RegexOption.IGNORE_CASE),
        Regex("""\s*\[.*?(official|video|audio|lyrics|lyric|visualizer|hd|hq|4k|remaster|remix|live|acoustic|version|edit|extended|radio|clean|explicit).*?\]""", RegexOption.IGNORE_CASE),
        Regex("""\s*【.*?】"""),
        Regex("""\s*\|.*$"""),
        Regex("""\s*-\s*(official|video|audio|lyrics|lyric|visualizer).*$""", RegexOption.IGNORE_CASE),
        Regex("""\s*\(feat\..*?\)""", RegexOption.IGNORE_CASE),
        Regex("""\s*\(ft\..*?\)""", RegexOption.IGNORE_CASE),
        Regex("""\s*feat\..*$""", RegexOption.IGNORE_CASE),
        Regex("""\s*ft\..*$""", RegexOption.IGNORE_CASE),
        Regex("""\s*\([^)]*\d{4}[^)]*\)""", RegexOption.IGNORE_CASE),
    )

    private val artistSeparators = listOf(" & ", " and ", ", ", " x ", " X ", " feat. ", " feat ", " ft. ", " ft ", " featuring ", " with ")

    private fun cleanTitle(title: String): String {
        var cleaned = title.trim()
        for (pattern in titleCleanupPatterns) {
            cleaned = cleaned.replace(pattern, "")
        }
        return cleaned.trim()
    }

    private fun cleanArtist(artist: String): String {
        var cleaned = artist.trim()
        for (separator in artistSeparators) {
            if (cleaned.contains(separator, ignoreCase = true)) {
                cleaned = cleaned.split(separator, ignoreCase = true, limit = 2)[0]
                break
            }
        }
        return cleaned.trim()
    }

    private suspend fun search(query: String): List<SearchResult> = runCatching {
        Timber.d("Searching Apple Music for: $query")
        val token = tokenManager.getToken()
        searchWithToken(token, query)
    }.getOrElse { e ->
        if (e is ClientRequestException && e.response.status.value == 401) {
            tokenManager.clearToken()
            return@getOrElse runCatching {
                val newToken = tokenManager.getToken()
                searchWithToken(newToken, query)
            }.getOrElse { e2 ->
                Timber.e(e2, "Search retry error: ${e2.message}")
                emptyList()
            }
        }
        Timber.e(e, "Search error: ${e.message}")
        emptyList()
    }

    private suspend fun searchWithToken(token: String, query: String): List<SearchResult> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")

        val response = httpClient.get("$APPLE_MUSIC_API_BASE/search?term=$encodedQuery&types=songs&limit=25&l=en-US&platform=web&format[resources]=map&include[songs]=artists&extend=artistUrl") {
            header("Authorization", "Bearer $token")
            header("Origin", "https://music.apple.com")
            header("Referer", "https://music.apple.com/")
            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0")
            header("Accept", "application/json")
            header("Accept-Language", "en-US,en;q=0.5")
            header("x-apple-renewal", "true")
        }

        val body = try {
            appleJson.decodeFromString<AppleMusicSearchResponse>(response.bodyAsText())
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse Apple Music search response")
            return emptyList()
        }

        val songs = body.results.songs?.data ?: return emptyList()

        return songs.mapNotNull { songData ->
            val detail = body.resources?.songs?.get(songData.id) ?: return@mapNotNull null
            val attr = detail.attributes
            SearchResult(
                id = songData.id,
                trackName = attr.name,
                artistName = attr.artistName,
                albumName = attr.albumName,
                duration = attr.durationInMillis?.toInt()?.div(1000),
                artwork = attr.artwork?.url?.replace("{w}", "100")?.replace("{h}", "100")?.replace("{f}", "png")
            )
        }
    }

    suspend fun getLyrics(
        title: String,
        artist: String,
        duration: Int,
        album: String? = null,
    ): Result<String> = runCatching {
        val cleanedTitle = cleanTitle(title)
        val cleanedArtist = cleanArtist(artist)

        val searchQueries = buildList {
            add("$cleanedTitle $cleanedArtist")
            add(cleanedTitle)
            if (!album.isNullOrBlank()) {
                add("$cleanedTitle $cleanedArtist $album")
            }
        }

        var allResults: List<Pair<SearchResult, Double>> = emptyList()

        for (query in searchQueries) {
            if (allResults.isEmpty()) {
                val searchResults = search(query)
                if (searchResults.isNotEmpty()) {
                    allResults = scoreAndFilterResults(searchResults, title, artist, duration)
                }
            }
        }

        if (allResults.isEmpty()) {
            throw IllegalStateException("No tracks found on Paxsenix")
        }

        var bestLyrics: String? = null
        var bestQuality = 0

        for ((result, score) in allResults.take(10)) {
            val lrc = fetchLyricsForTrack(result.id).getOrNull() ?: continue
            if (lrc.isEmpty()) continue

            val quality = getQuality(lrc)

            if (quality > bestQuality) {
                bestQuality = quality
                bestLyrics = lrc
            }

            if (bestQuality == 3) break
        }

        bestLyrics?.let {
            return Result.success(it)
        }

        return Result.failure(IllegalStateException("No lyrics available from Paxsenix"))
    }

    private fun getQuality(lrc: String): Int {
        if (lrc.isBlank()) return 0
        val hasWordTimings = (lrc.contains("<") && lrc.contains(">") && (lrc.contains("|") || lrc.contains(":"))) ||
                lrc.contains(Regex("<\\d{1,2}:\\d{2}\\.\\d{2,3}>"))

        if (hasWordTimings) return 3

        val hasLineTimings = lrc.contains(Regex("\\[\\d\\d:\\d\\d\\.\\d{2,3}\\]")) ||
                lrc.contains(Regex("^\\[bg:.*\\]", RegexOption.MULTILINE))

        if (hasLineTimings) return 2
        return 1
    }

    private fun scoreAndFilterResults(
        results: List<SearchResult>,
        title: String,
        artist: String,
        duration: Int
    ): List<Pair<SearchResult, Double>> {
        val durationMs = duration * 1000
        val cleanupRegex = Regex("""\s*\(.*?\)|\s*\[.*?\]""")

        val cleanedTitle = title.replace(cleanupRegex, "").lowercase().trim()
        val cleanedArtist = cleanArtist(artist).lowercase()

        val targetIsMixed = title.contains("mixed", ignoreCase = true)
        val targetIsRemix = title.contains("remix", ignoreCase = true)

        return results.map { result ->
            var score = 0.0

            val resultTitle = result.displayName
            val resultArtist = result.displayArtist

            result.duration?.let { d ->
                val diff = abs(d - durationMs)
                when {
                    diff <= 2000 -> score += 100
                    diff <= 5000 -> score += 50
                    diff <= 10000 -> score += 10
                    else -> score -= 50
                }
            }

            val resultTitleCleaned = resultTitle.replace(cleanupRegex, "").lowercase().trim()

            when {
                resultTitleCleaned == cleanedTitle -> score += 80
                resultTitleCleaned.contains(cleanedTitle) || cleanedTitle.contains(resultTitleCleaned) -> score += 40
            }

            val resultIsMixed = resultTitle.contains("mixed", ignoreCase = true)
            val resultIsRemix = resultTitle.contains("remix", ignoreCase = true)

            if (resultIsMixed && !targetIsMixed) score -= 60
            if (resultIsRemix && !targetIsRemix) score -= 40

            val resultArtistLower = resultArtist.lowercase()
            val targetArtistPrimary = cleanedArtist

            when {
                resultArtistLower.contains(targetArtistPrimary) -> score += 50
                else -> {
                    val artistWords = targetArtistPrimary.split(Regex("\\s+")).filter { it.length > 2 }
                    if (artistWords.any { resultArtistLower.contains(it) }) {
                        score += 25
                    }
                }
            }

            result to score
        }.sortedByDescending { it.second }.filter { it.second > 0 }.take(10)
    }

    private suspend fun fetchLyricsForTrack(id: String): Result<String> = runCatching {
        val response = httpClient.get("/apple-music/lyrics") {
            parameter("id", id)
        }.body<LyricsResponse>()

        if (!response.ttmlContent.isNullOrBlank()) {
            val lrc = convertTTMLToAppFormat(response.ttmlContent)
            if (lrc.isNotEmpty()) {
                return@runCatching lrc
            }
        }

        if (!response.elrcMultiPerson.isNullOrBlank()) {
            return@runCatching response.elrcMultiPerson
        }
        if (!response.elrc.isNullOrBlank()) {
            return@runCatching response.elrc
        }

        if (!response.plain.isNullOrBlank()) {
            return@runCatching response.plain
        }

        if (response.content.isEmpty()) {
            throw IllegalStateException("No lyrics found")
        }

        val hasWordLevel = response.type == "Syllable"

        if (!hasWordLevel) {
            val plain = response.content
                .map { line -> line.text.joinToString(" ") { it.text } }
                .filter { it.isNotBlank() }
                .joinToString("\n")
            return@runCatching plain
        }

        val lrc = buildString {
            response.content.forEach { line ->
                val timeMs = line.timestamp
                val minutes = timeMs / 1000 / 60
                val seconds = (timeMs / 1000) % 60
                val centiseconds = (timeMs % 1000) / 10

                val agent = when {
                    line.background -> "{bg}"
                    line.oppositeTurn -> "{agent:v2}"
                    else -> "{agent:v1}"
                }

                val lineText = line.text.joinToString(" ") { it.text }

                if (lineText.isNotBlank()) {
                    appendLine(String.format(Locale.US, "[%02d:%02d.%02d]%s%s", minutes, seconds, centiseconds, agent, lineText))

                    if (line.text.isNotEmpty()) {
                        val wordsData = line.text.joinToString("|") { word ->
                            "${word.text}:${word.timestamp.toDouble() / 1000}:${word.endtime.toDouble() / 1000}"
                        }
                        if (wordsData.isNotEmpty()) {
                            appendLine("<$wordsData>")
                        }
                    }
                }
            }
        }

        return@runCatching lrc
    }

    private fun convertTTMLToAppFormat(ttml: String): String {
        return try {
            val parsedLines = TTMLParser.parseTTML(ttml)
            TTMLParser.toLRC(parsedLines)
        } catch (e: Exception) {
            Timber.e(e, "TTML conversion failed: ${e.message}")
            ""
        }
    }

    private class AppleTokenManager(private val httpClient: HttpClient) {
        private var cachedToken: String? = null
        private val mutex = Mutex()

        suspend fun getToken(): String = mutex.withLock {
            cachedToken?.let { return it }

            try {
                val mainPageResponse = httpClient.get("https://beta.music.apple.com")
                val mainPageBody = mainPageResponse.bodyAsText()

                val indexJsRegex = Regex("""/assets/index~[^/]+\.js""")
                val indexJsMatch = indexJsRegex.find(mainPageBody)
                    ?: throw Exception("Could not find index JS URL")

                val indexJsUri = indexJsMatch.value
                val indexJsResponse = httpClient.get("https://beta.music.apple.com$indexJsUri")
                val indexJsBody = indexJsResponse.bodyAsText()

                val tokenRegex = Regex("""eyJ[A-Za-z0-9\-_=]+\.[A-Za-z0-9\-_=]+\.[A-Za-z0-9\-_=]+""")
                val tokenMatch = tokenRegex.find(indexJsBody)
                    ?: throw Exception("Could not find token")

                val token = tokenMatch.value
                cachedToken = token
                return token
            } catch (e: Exception) {
                throw Exception("Error fetching Apple Music token: ${e.message}", e)
            }
        }

        fun clearToken() {
            cachedToken = null
        }
    }
}

// --- Provider Implementation ---

object PaxsenixLyricsProvider : LyricsProvider {
    override val name = "Paxsenix"

    override fun isEnabled(context: Context): Boolean =
        context.dataStore[EnablePaxsenixKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
    ): Result<String> = Paxsenix.getLyrics(title, artist, duration)
}
