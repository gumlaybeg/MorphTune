package com.arturo254.opentune.lyrics

import android.content.Context
import android.util.LruCache
import com.arturo254.opentune.constants.DefaultLyricsProviderPriority
import com.arturo254.opentune.constants.LyricsProviderPriorityKey
import com.arturo254.opentune.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import com.arturo254.opentune.models.MediaMetadata
import com.arturo254.opentune.utils.dataStore
import com.arturo254.opentune.utils.reportException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class LyricsHelper
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {
    private val allProviders = listOf(
        LyricsPlusProvider,
        PaxsenixLyricsProvider,
        LrcLibLyricsProvider,
        KuGouLyricsProvider,
        YouTubeSubtitleLyricsProvider,
        YouTubeLyricsProvider,
    )

    private var orderedProviders: List<LyricsProvider> = allProviders

    init {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.data
                .map { it[LyricsProviderPriorityKey] ?: DefaultLyricsProviderPriority }
                .distinctUntilChanged()
                .collect { priorityString ->
                    val order = priorityString.split(",")
                    orderedProviders = order.mapNotNull { name ->
                        allProviders.find { it.name.equals(name, ignoreCase = true) }
                    } + allProviders.filter { it.name !in order }
                }
        }
    }

    private val cache = LruCache<String, List<LyricsResult>>(MAX_CACHE_SIZE)

    suspend fun getLyrics(mediaMetadata: MediaMetadata): String {
        val cached = cache.get(mediaMetadata.id)?.firstOrNull()
        if (cached != null) {
            return cached.lyrics
        }
        orderedProviders.forEach { provider ->
            if (provider.isEnabled(context)) {
                provider
                    .getLyrics(
                        mediaMetadata.id,
                        mediaMetadata.title,
                        mediaMetadata.artists.joinToString { it.name },
                        mediaMetadata.duration,
                    ).onSuccess { lyrics ->
                        return lyrics
                    }.onFailure {
                        reportException(it)
                    }
            }
        }
        return LYRICS_NOT_FOUND
    }

    suspend fun getAllLyrics(
        mediaId: String,
        songTitle: String,
        songArtists: String,
        duration: Int,
        callback: (LyricsResult) -> Unit,
    ) {
        val cacheKey = "$songArtists-$songTitle".replace(" ", "")
        cache.get(cacheKey)?.let { results ->
            results.forEach {
                callback(it)
            }
            return
        }
        val allResult = mutableListOf<LyricsResult>()
        orderedProviders.forEach { provider ->
            if (provider.isEnabled(context)) {
                provider.getAllLyrics(mediaId, songTitle, songArtists, duration) { lyrics ->
                    val result = LyricsResult(provider.name, lyrics)
                    allResult += result
                    callback(result)
                }
            }
        }
        cache.put(cacheKey, allResult)
    }

    companion object {
        private const val MAX_CACHE_SIZE = 3
    }
}

data class LyricsResult(
    val providerName: String,
    val lyrics: String,
)
