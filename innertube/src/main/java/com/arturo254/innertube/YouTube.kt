package com.arturo254.innertube

import com.arturo254.innertube.models.AccountInfo
import com.arturo254.innertube.models.YTItem
import com.arturo254.innertube.models.AlbumItem
import com.arturo254.innertube.models.Artist
import com.arturo254.innertube.models.ArtistItem
import com.arturo254.innertube.models.BrowseEndpoint
import com.arturo254.innertube.models.GridRenderer
import com.arturo254.innertube.models.MediaInfo
import com.arturo254.innertube.models.MusicResponsiveListItemRenderer
import com.arturo254.innertube.models.MusicTwoRowItemRenderer
import com.arturo254.innertube.models.MusicCarouselShelfRenderer
import com.arturo254.innertube.models.MusicShelfRenderer
import com.arturo254.innertube.models.SectionListRenderer
import com.arturo254.innertube.models.PlaylistItem
import com.arturo254.innertube.models.SearchSuggestions
import com.arturo254.innertube.models.Run
import com.arturo254.innertube.models.SongItem
import com.arturo254.innertube.models.WatchEndpoint
import com.arturo254.innertube.models.WatchEndpoint.WatchEndpointMusicSupportedConfigs.WatchEndpointMusicConfig.Companion.MUSIC_VIDEO_TYPE_ATV
import com.arturo254.innertube.models.YouTubeClient
import com.arturo254.innertube.models.YouTubeClient.Companion.WEB
import com.arturo254.innertube.models.YouTubeClient.Companion.WEB_REMIX
import com.arturo254.innertube.models.YouTubeLocale
import com.arturo254.innertube.models.getContinuation
import com.arturo254.innertube.models.getItems
import com.arturo254.innertube.models.oddElements
import com.arturo254.innertube.models.response.AccountMenuResponse
import com.arturo254.innertube.models.response.BrowseResponse
import com.arturo254.innertube.models.response.CreatePlaylistResponse
import com.arturo254.innertube.models.response.EditPlaylistResponse
import com.arturo254.innertube.models.response.FeedbackResponse
import com.arturo254.innertube.models.response.GetQueueResponse
import com.arturo254.innertube.models.response.GetSearchSuggestionsResponse
import com.arturo254.innertube.models.response.GetTranscriptResponse
import com.arturo254.innertube.models.response.ImageUploadResponse
import com.arturo254.innertube.models.response.NextResponse
import com.arturo254.innertube.models.response.SearchResponse
import com.arturo254.innertube.models.comment.CommentThreadRenderer
import com.arturo254.innertube.models.comment.CommentResponse
import com.arturo254.innertube.models.comment.CommentRenderer
import com.arturo254.innertube.pages.AlbumPage
import com.arturo254.innertube.pages.ArtistItemsContinuationPage
import com.arturo254.innertube.pages.ArtistItemsPage
import com.arturo254.innertube.pages.ArtistPage
import com.arturo254.innertube.pages.ChartsPage
import com.arturo254.innertube.pages.BrowseResult
import com.arturo254.innertube.pages.ExplorePage
import com.arturo254.innertube.pages.HistoryPage
import com.arturo254.innertube.pages.HomePage
import com.arturo254.innertube.pages.LibraryContinuationPage
import com.arturo254.innertube.pages.LibraryPage
import com.arturo254.innertube.pages.MoodAndGenres
import com.arturo254.innertube.pages.NewReleaseAlbumPage
import com.arturo254.innertube.pages.NextPage
import com.arturo254.innertube.pages.NextResult
import com.arturo254.innertube.pages.PlaylistContinuationPage
import com.arturo254.innertube.pages.PlaylistPage
import com.arturo254.innertube.pages.RelatedPage
import com.arturo254.innertube.pages.SearchPage
import com.arturo254.innertube.pages.SearchResult
import com.arturo254.innertube.pages.SearchSuggestionPage
import com.arturo254.innertube.pages.SearchSummary
import com.arturo254.innertube.pages.SearchSummaryPage
import com.arturo254.innertube.pages.PageHelper
import com.arturo254.innertube.pages.NewPipeExtractor
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.net.Proxy
import kotlin.random.Random

object YouTube {
    private val innerTube = InnerTube()

    var locale: YouTubeLocale
        get() = innerTube.locale
        set(value) {
            innerTube.locale = value
        }
    var visitorData: String?
        get() = innerTube.visitorData
        set(value) {
            innerTube.visitorData = value
        }
    var dataSyncId: String?
        get() = innerTube.dataSyncId
        set(value) {
            innerTube.dataSyncId = value
        }
    var cookie: String?
        get() = innerTube.cookie
        set(value) {
            innerTube.cookie = value
        }
    var proxy: Proxy?
        get() = innerTube.proxy
        set(value) {
            innerTube.proxy = value
        }

    var proxyAuth: String?
        get() = innerTube.proxyAuth
        set(value) {
            innerTube.proxyAuth = value
        }
    var useLoginForBrowse: Boolean
        get() = innerTube.useLoginForBrowse
        set(value) {
            innerTube.useLoginForBrowse = value
        }

    var ipVersion: com.arturo254.innertube.models.IpVersion
        get() = innerTube.ipVersion
        set(value) {
            innerTube.ipVersion = value
        }

    suspend fun refreshVisitorData(): Result<String> = visitorData().onSuccess {
        visitorData = it
    }

    fun clearGuestSession() {
        visitorData = null
        dataSyncId = null
    }

    suspend fun searchSuggestions(query: String): Result<SearchSuggestions> = runCatching {
        val response = innerTube.getSearchSuggestions(WEB_REMIX, query).body<GetSearchSuggestionsResponse>()
        SearchSuggestions(
            queries = response.contents?.getOrNull(0)?.searchSuggestionsSectionRenderer?.contents?.mapNotNull { content ->
                content.searchSuggestionRenderer?.suggestion?.runs?.joinToString(separator = "") { it.text }
            }.orEmpty(),
            recommendedItems = response.contents?.getOrNull(1)?.searchSuggestionsSectionRenderer?.contents?.mapNotNull {
                it.musicResponsiveListItemRenderer?.let { renderer ->
                    SearchSuggestionPage.fromMusicResponsiveListItemRenderer(renderer)
                }
            }.orEmpty()
        )
    }

    suspend fun searchSummary(query: String): Result<SearchSummaryPage> = runCatching {
        val response = innerTube.search(WEB_REMIX, query).body<SearchResponse>()
        val contents = response.contents?.tabbedSearchResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents.orEmpty()

        val shelfSummaries = contents.mapNotNull { it ->
            if (it.musicCardShelfRenderer != null) {
                SearchSummary(
                    title = it.musicCardShelfRenderer.header?.musicCardShelfHeaderBasicRenderer?.title?.runs?.firstOrNull()?.text ?: YouTubeConstants.DEFAULT_TOP_RESULT,
                    items = listOfNotNull(SearchSummaryPage.fromMusicCardShelfRenderer(it.musicCardShelfRenderer))
                        .plus(
                            it.musicCardShelfRenderer.contents
                                ?.mapNotNull { it.musicResponsiveListItemRenderer }
                                ?.mapNotNull(SearchSummaryPage.Companion::fromMusicResponsiveListItemRenderer)
                                .orEmpty()
                        )
                        .distinctBy { it.id }
                        .ifEmpty { null } ?: return@mapNotNull null
                )
            } else if (it.musicShelfRenderer != null) {
                SearchSummary(
                    title = it.musicShelfRenderer.title?.runs?.firstOrNull()?.text ?: YouTubeConstants.DEFAULT_OTHER_RESULTS,
                    items = it.musicShelfRenderer.contents?.getItems()
                        ?.mapNotNull {
                            SearchSummaryPage.fromMusicResponsiveListItemRenderer(it)
                        }
                        ?.distinctBy { it.id }
                        ?.ifEmpty { null } ?: return@mapNotNull null
                )
            } else {
                null
            }
        }

        val flatItems = contents
            .mapNotNull { it.itemSectionRenderer }
            .flatMap { it.contents.orEmpty() }
            .mapNotNull { it.musicResponsiveListItemRenderer }
            .mapNotNull { SearchSummaryPage.fromMusicResponsiveListItemRenderer(it) }

        val groupedSummaries = mutableListOf<SearchSummary>()

        val flatSongs = flatItems.filterIsInstance<SongItem>().filter { !it.isVideoSong }
        if (flatSongs.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Songs", items = flatSongs))
        }

        val flatVideos = flatItems.filterIsInstance<SongItem>().filter { it.isVideoSong }
        if (flatVideos.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Videos", items = flatVideos))
        }

        val flatAlbums = flatItems.filterIsInstance<AlbumItem>()
        if (flatAlbums.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Albums", items = flatAlbums))
        }

        val flatArtists = flatItems.filterIsInstance<ArtistItem>()
        if (flatArtists.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Artists", items = flatArtists))
        }

        val flatPlaylists = flatItems.filterIsInstance<PlaylistItem>()
        if (flatPlaylists.isNotEmpty()) {
            groupedSummaries.add(SearchSummary(title = "Playlists", items = flatPlaylists))
        }

        SearchSummaryPage(
            summaries = shelfSummaries + groupedSummaries
        )
    }

    suspend fun search(query: String, filter: SearchFilter): Result<SearchResult> = runCatching {
        val response = innerTube.search(WEB_REMIX, query, filter.value).body<SearchResponse>()
        val musicShelfRenderer = response.contents?.tabbedSearchResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents
            ?.mapNotNull { it.musicShelfRenderer }
            ?.firstOrNull()
        SearchResult(
            items = musicShelfRenderer?.contents?.getItems()?.mapNotNull {
                SearchPage.toYTItem(it)
            }.orEmpty(),
            continuation = musicShelfRenderer?.continuations?.getContinuation()
        )
    }

    suspend fun searchContinuation(continuation: String): Result<SearchResult> = runCatching {
        val response = innerTube.search(WEB_REMIX, continuation = continuation).body<SearchResponse>()
        val items = response.continuationContents?.musicShelfContinuation?.contents
            ?.mapNotNull {
                SearchPage.toYTItem(it.musicResponsiveListItemRenderer)
            } ?: emptyList()
        SearchResult(
            items = items,
            continuation = if (items.isEmpty()) null else response.continuationContents?.musicShelfContinuation?.continuations?.getContinuation()
        )
    }

    suspend fun album(browseId: String, withSongs: Boolean = true): Result<AlbumPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, browseId, setLogin = true).body<BrowseResponse>()

        fun mapRuns(runs: List<Run>?): List<Run>? = runs?.map { run ->
            Run(
                text = run.text,
                navigationEndpoint = run.navigationEndpoint
            )
        }

        val descriptionRuns = sequence {
            response.contents?.twoColumnBrowseResultsRenderer?.tabs?.forEach { tab ->
                tab?.tabRenderer?.content?.sectionListRenderer?.contents?.forEach { content ->
                    content.musicDescriptionShelfRenderer?.description?.runs?.let { yield(it) }
                }
            }
            response.contents?.singleColumnBrowseResultsRenderer?.tabs?.forEach { tab ->
                tab.tabRenderer?.content?.sectionListRenderer?.contents?.forEach { content ->
                    content.musicDescriptionShelfRenderer?.description?.runs?.let { yield(it) }
                }
            }
            response.header?.musicDetailHeaderRenderer?.description?.runs?.let { yield(it) }
            response.header?.musicImmersiveHeaderRenderer?.description?.runs?.let { yield(it) }
            response.header?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicDetailHeaderRenderer?.description?.runs?.let { yield(it) }
            response.header?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicResponsiveHeaderRenderer?.description?.musicDescriptionShelfRenderer?.description?.runs?.let { yield(it) }
            
            response.contents?.twoColumnBrowseResultsRenderer?.tabs?.forEach { tab ->
                tab?.tabRenderer?.content?.sectionListRenderer?.contents?.forEach { content ->
                    content.musicResponsiveHeaderRenderer?.description?.musicDescriptionShelfRenderer?.description?.runs?.let { yield(it) }
                }
            }
            response.contents?.singleColumnBrowseResultsRenderer?.tabs?.forEach { tab ->
                tab.tabRenderer?.content?.sectionListRenderer?.contents?.forEach { content ->
                    content.musicResponsiveHeaderRenderer?.description?.musicDescriptionShelfRenderer?.description?.runs?.let { yield(it) }
                }
            }
        }.firstOrNull()?.let(::mapRuns)

        val description = descriptionRuns?.joinToString(separator = "") { it.text }

        if (browseId.contains("FEmusic_library_privately_owned_release_detail")) {
            val playlistId =
                response.header?.musicDetailHeaderRenderer?.menu?.menuRenderer?.topLevelButtons?.firstOrNull()?.buttonRenderer?.navigationEndpoint?.watchPlaylistEndpoint?.playlistId!!
            val albumItem = AlbumItem(
                browseId = browseId,
                playlistId = playlistId,
                title = response.header.musicDetailHeaderRenderer.title.runs?.firstOrNull()?.text!!,
                artists = response.header.musicDetailHeaderRenderer.subtitle.runs?.filter { it.navigationEndpoint != null }?.map {
                    Artist(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId
                    )
                },
                year = response.header.musicDetailHeaderRenderer.subtitle.runs?.lastOrNull()?.text?.toIntOrNull(),
                thumbnail = response.header.musicDetailHeaderRenderer.thumbnail.croppedSquareThumbnailRenderer?.thumbnail?.thumbnails?.lastOrNull()!!.url,
                explicit = false,
            )
            return@runCatching AlbumPage(
                album = albumItem,
                songs = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.musicShelfRenderer?.contents?.getItems()?.mapNotNull {
                    AlbumPage.getSong(response, it, albumItem)
                }!!.toMutableList(),
                otherVersions = emptyList(),
                description = description,
                descriptionRuns = descriptionRuns
            )
        } else {
            val playlistId =
                response.microformat?.microformatDataRenderer?.urlCanonical?.substringAfterLast('=')!!
            val albumItem = AlbumItem(
                browseId = browseId,
                playlistId = playlistId,
                title = response.contents?.twoColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.musicResponsiveHeaderRenderer?.title?.runs?.firstOrNull()?.text!!,
                artists = response.contents.twoColumnBrowseResultsRenderer.tabs.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.musicResponsiveHeaderRenderer?.straplineTextOne?.runs?.oddElements()
                    ?.map {
                        Artist(
                            name = it.text,
                            id = it.navigationEndpoint?.browseEndpoint?.browseId
                        )
                    }!!,
                year = response.contents.twoColumnBrowseResultsRenderer.tabs.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.musicResponsiveHeaderRenderer?.subtitle?.runs?.lastOrNull()?.text?.toIntOrNull(),
                thumbnail = response.contents.twoColumnBrowseResultsRenderer.tabs.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.musicResponsiveHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.thumbnail?.thumbnails?.lastOrNull()?.url!!,
                explicit = false,
            )
            return@runCatching AlbumPage(
                album = albumItem,
                songs = if (withSongs) albumSongs(
                    playlistId, albumItem
                ).getOrThrow() else emptyList(),
                otherVersions = response.contents.twoColumnBrowseResultsRenderer.secondaryContents?.sectionListRenderer?.contents
                    ?.find { it.musicCarouselShelfRenderer?.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.any { it.text.contains("versions", ignoreCase = true) } == true }
                    ?.musicCarouselShelfRenderer?.contents
                    ?.mapNotNull { it.musicTwoRowItemRenderer }
                    ?.mapNotNull(NewReleaseAlbumPage::fromMusicTwoRowItemRenderer)
                    .orEmpty(),
                releasesForYou = response.contents.twoColumnBrowseResultsRenderer.secondaryContents?.sectionListRenderer?.contents
                    ?.find { it.musicCarouselShelfRenderer?.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.any { it.text.contains("releases", ignoreCase = true) || it.text.contains("more from", ignoreCase = true) } == true }
                    ?.musicCarouselShelfRenderer?.contents
                    ?.mapNotNull { it.musicTwoRowItemRenderer }
                    ?.mapNotNull(NewReleaseAlbumPage::fromMusicTwoRowItemRenderer)
                    .orEmpty(),
                description = description,
                descriptionRuns = descriptionRuns
            )
        }
    }

    suspend fun albumSongs(playlistId: String, album: AlbumItem? = null): Result<List<SongItem>> = runCatching {
        var response = innerTube.browse(WEB_REMIX, "VL$playlistId").body<BrowseResponse>()
        val songs = response.contents?.twoColumnBrowseResultsRenderer
            ?.secondaryContents?.sectionListRenderer
            ?.contents?.firstOrNull()
            ?.musicPlaylistShelfRenderer?.contents?.getItems()
            ?.mapNotNull {
                AlbumPage.getSong(response, it, album)
            }!!
            .toMutableList()
        var continuation = response.contents.twoColumnBrowseResultsRenderer.secondaryContents.sectionListRenderer
            .contents.firstOrNull()?.musicPlaylistShelfRenderer?.continuations?.getContinuation()
        val seenContinuations = mutableSetOf<String>()
        var requestCount = 0
        val maxRequests = 50 
        
        while (continuation != null && requestCount < maxRequests) {
            if (continuation in seenContinuations) {
                break
            }
            seenContinuations.add(continuation)
            requestCount++
            
            response = innerTube.browse(
                client = WEB_REMIX,
                continuation = continuation,
            ).body<BrowseResponse>()
            songs += response.onResponseReceivedActions?.firstOrNull()?.appendContinuationItemsAction?.continuationItems?.getItems()?.mapNotNull {
                AlbumPage.getSong(response, it, album)
            }.orEmpty()
            continuation = response.continuationContents?.musicPlaylistShelfContinuation?.continuations?.getContinuation()
        }
        songs
    }

    suspend fun artist(browseId: String): Result<ArtistPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, browseId).body<BrowseResponse>()

        fun mapRuns(runs: List<Run>?): List<Run>? = runs?.map { run ->
            Run(
                text = run.text,
                navigationEndpoint = run.navigationEndpoint
            )
        }

        val descriptionRuns = response.contents?.sectionListRenderer?.contents
            ?.firstOrNull { it.musicDescriptionShelfRenderer != null }
            ?.musicDescriptionShelfRenderer?.description?.runs
            ?.let(::mapRuns)
            ?: response.header?.musicImmersiveHeaderRenderer?.description?.runs?.let(::mapRuns)

        ArtistPage(
            artist = ArtistItem(
                id = browseId,
                title = response.header?.musicImmersiveHeaderRenderer?.title?.runs?.firstOrNull()?.text
                    ?: response.header?.musicVisualHeaderRenderer?.title?.runs?.firstOrNull()?.text
                    ?: response.header?.musicHeaderRenderer?.title?.runs?.firstOrNull()?.text!!,
                thumbnail = response.header?.musicImmersiveHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                    ?: response.header?.musicVisualHeaderRenderer?.foregroundThumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                    ?: response.header?.musicDetailHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: "",
                channelId = response.header?.musicImmersiveHeaderRenderer?.subscriptionButton?.subscribeButtonRenderer?.channelId,
                playEndpoint = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
                    ?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.musicShelfRenderer
                    ?.contents?.firstOrNull()?.musicResponsiveListItemRenderer?.overlay?.musicItemThumbnailOverlayRenderer
                    ?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint,
                shuffleEndpoint = response.header?.musicImmersiveHeaderRenderer?.playButton?.buttonRenderer?.navigationEndpoint?.watchEndpoint
                    ?: response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer
                        ?.contents?.firstOrNull()?.musicShelfRenderer?.contents?.firstOrNull()?.musicResponsiveListItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint,
                radioEndpoint = response.header?.musicImmersiveHeaderRenderer?.startRadioButton?.buttonRenderer?.navigationEndpoint?.watchEndpoint,
                subscriberCountText = response.header?.musicImmersiveHeaderRenderer?.subscriptionButton?.subscribeButtonRenderer?.subscriberCountText?.runs?.firstOrNull()?.text
            ),
            sections = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
                ?.tabRenderer?.content?.sectionListRenderer?.contents
                ?.mapNotNull(ArtistPage::fromSectionListRendererContent)!!,
            description = descriptionRuns?.joinToString(separator = "") { it.text }
        )
    }

    suspend fun artistItems(endpoint: BrowseEndpoint): Result<ArtistItemsPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, endpoint.browseId, endpoint.params).body<BrowseResponse>()
        val sectionContent = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()
        
        val gridRenderer = sectionContent?.gridRenderer
        val musicCarouselShelfRenderer = sectionContent?.musicCarouselShelfRenderer
        val musicPlaylistShelfRenderer = sectionContent?.musicPlaylistShelfRenderer
        val musicShelfRenderer = sectionContent?.musicShelfRenderer
        
        when {
            gridRenderer != null -> {
                ArtistItemsPage(
                    title = gridRenderer.header?.gridHeaderRenderer?.title?.runs?.firstOrNull()?.text.orEmpty(),
                    items = gridRenderer.items.mapNotNull {
                        it.musicTwoRowItemRenderer?.let { renderer ->
                            ArtistItemsPage.fromMusicTwoRowItemRenderer(renderer)
                        }
                    },
                    continuation = gridRenderer.continuations?.getContinuation()
                )
            }
            musicCarouselShelfRenderer != null -> {
                ArtistItemsPage(
                    title = musicCarouselShelfRenderer.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.firstOrNull()?.text.orEmpty(),
                    items = musicCarouselShelfRenderer.contents.mapNotNull { content ->
                        content.musicTwoRowItemRenderer?.let { renderer ->
                            ArtistItemsPage.fromMusicTwoRowItemRenderer(renderer)
                        } ?: content.musicResponsiveListItemRenderer?.let { renderer ->
                            ArtistItemsPage.fromMusicResponsiveListItemRenderer(renderer)
                        }
                    },
                    continuation = null
                )
            }
            musicShelfRenderer != null -> {
                ArtistItemsPage(
                    title = musicShelfRenderer.title?.runs?.firstOrNull()?.text 
                        ?: response.header?.musicHeaderRenderer?.title?.runs?.firstOrNull()?.text 
                        ?: "",
                    items = musicShelfRenderer.contents?.getItems()?.mapNotNull {
                        ArtistItemsPage.fromMusicResponsiveListItemRenderer(it)
                    } ?: emptyList(),
                    continuation = musicShelfRenderer.continuations?.getContinuation()
                )
            }
            else -> {
                ArtistItemsPage(
                    title = response.header?.musicHeaderRenderer?.title?.runs?.firstOrNull()?.text ?: "",
                    items = musicPlaylistShelfRenderer?.contents?.getItems()?.mapNotNull {
                        ArtistItemsPage.fromMusicResponsiveListItemRenderer(it)
                    } ?: emptyList(),
                    continuation = musicPlaylistShelfRenderer?.continuations?.getContinuation()
                )
            }
        }
    }

    suspend fun artistItemsContinuation(continuation: String): Result<ArtistItemsContinuationPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, continuation = continuation).body<BrowseResponse>()

        when {
            response.continuationContents?.gridContinuation != null -> {
                val gridContinuation = response.continuationContents.gridContinuation
                val items = gridContinuation.items.mapNotNull {
                    it.musicTwoRowItemRenderer?.let { renderer ->
                        ArtistItemsPage.fromMusicTwoRowItemRenderer(renderer)
                    }
                }
                ArtistItemsContinuationPage(
                    items = items,
                    continuation = if (items.isEmpty()) null else gridContinuation.continuations?.getContinuation()
                )
            }

            response.continuationContents?.musicPlaylistShelfContinuation != null -> {
                val musicPlaylistShelfContinuation = response.continuationContents.musicPlaylistShelfContinuation
                val items = musicPlaylistShelfContinuation.contents.getItems().mapNotNull {
                    ArtistItemsPage.fromMusicResponsiveListItemRenderer(it)
                }
                ArtistItemsContinuationPage(
                    items = items,
                    continuation = if (items.isEmpty()) null else musicPlaylistShelfContinuation.continuations?.getContinuation()
                )
            }

            else -> {
                val continuationItems = response.onResponseReceivedActions?.firstOrNull()
                    ?.appendContinuationItemsAction?.continuationItems
                val items = continuationItems?.getItems()?.mapNotNull {
                    ArtistItemsPage.fromMusicResponsiveListItemRenderer(it)
                } ?: emptyList()
                ArtistItemsContinuationPage(
                    items = items,
                    continuation = if (items.isEmpty()) null else response.onResponseReceivedActions.firstOrNull()?.appendContinuationItemsAction?.continuationItems?.getContinuation()
                )
            }
        }
    }

    suspend fun playlist(playlistId: String): Result<PlaylistPage> = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            browseId = "VL$playlistId",
            setLogin = true
        ).body<BrowseResponse>()
        val base = response.contents?.twoColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()
        val header = base?.musicResponsiveHeaderRenderer ?: base?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicResponsiveHeaderRenderer

        val editable = base?.musicEditablePlaylistDetailHeaderRenderer != null
        val secondarySectionList = response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer

        var related = secondarySectionList?.contents?.let { parseRelatedItems(it.drop(1)) }

        if (related.isNullOrEmpty()) {
            secondarySectionList?.continuations?.getContinuation()?.let { continuationToken ->
                val continuationResponse = innerTube.browse(
                    client = WEB_REMIX,
                    continuation = continuationToken,
                    setLogin = true
                ).body<BrowseResponse>()

                continuationResponse.continuationContents?.sectionListContinuation?.contents?.let {
                    val parsed = parseRelatedItems(it)
                    if (parsed.isNotEmpty()) {
                        related = parsed
                    }
                }
            }
        }

        val title = header?.title?.runs?.firstOrNull()?.text
            ?: throw IllegalStateException("Playlist title not found for id=$playlistId (header=${header != null})")

        val thumbnailUrl = header.thumbnail?.musicThumbnailRenderer?.thumbnail?.thumbnails?.lastOrNull()?.url
            ?: ""

        val shuffleEndpoint = header.buttons?.lastOrNull()?.menuRenderer?.items?.firstOrNull()
            ?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint

        val sectionListContents = response.contents?.twoColumnBrowseResultsRenderer
            ?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents

        val secondarySectionListContents = response.contents?.twoColumnBrowseResultsRenderer
            ?.secondaryContents?.sectionListRenderer?.contents

        val descFromShelf = sectionListContents?.firstNotNullOfOrNull {
            it.musicDescriptionShelfRenderer?.description?.runs?.joinToString("") { run -> run.text }
        }
        val descFromSecondaryShelf = secondarySectionListContents?.firstNotNullOfOrNull {
            it.musicDescriptionShelfRenderer?.description?.runs?.joinToString("") { run -> run.text }
        }
        val descFromHeader = header?.description?.runs?.joinToString("") { it.text }
        val descFromEditable = base?.musicEditablePlaylistDetailHeaderRenderer
            ?.header?.musicDetailHeaderRenderer
            ?.description?.runs?.joinToString("") { it.text }
        val descFromTopLevel = response.header?.musicDetailHeaderRenderer
            ?.description?.runs?.joinToString("") { it.text }
        val descFromMicroformat = response.microformat?.microformatDataRenderer?.description

        val description: String? = descFromShelf ?: descFromSecondaryShelf ?: descFromHeader ?: descFromEditable ?: descFromTopLevel ?: descFromMicroformat


        PlaylistPage(
            playlist = PlaylistItem(
                id = playlistId,
                title = title,
                author = header.straplineTextOne?.runs?.firstOrNull()?.let {
                    Artist(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId
                    )
                },
                songCountText = header.secondSubtitle?.runs?.firstOrNull()?.text,
                thumbnail = thumbnailUrl,
                playEndpoint = null,
                shuffleEndpoint = shuffleEndpoint,
                radioEndpoint = header.buttons?.getOrNull(2)?.menuRenderer?.items?.find {
                    it.menuNavigationItemRenderer?.icon?.iconType == "MIX"
                }?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint,
                isEditable = editable,
                description = description,
            ),
            songs = response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer
                ?.contents?.firstOrNull()?.musicPlaylistShelfRenderer?.contents?.getItems()?.mapNotNull {
                    PlaylistPage.fromMusicResponsiveListItemRenderer(it)
                } ?: emptyList(),
            songsContinuation = response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer
                ?.contents?.firstOrNull()?.musicPlaylistShelfRenderer?.continuations?.getContinuation()
                ?: response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer
                    ?.contents?.firstOrNull()?.musicPlaylistShelfRenderer?.continuations?.getContinuation(),
            continuation = response.contents?.twoColumnBrowseResultsRenderer?.secondaryContents?.sectionListRenderer
                ?.continuations?.getContinuation(),
            related = related?.ifEmpty { null }
        )
    }

    private fun parseRelatedItems(contents: List<SectionListRenderer.Content>): List<YTItem> {
        return contents.mapNotNull { content ->
            content.musicCarouselShelfRenderer?.let { renderer ->
                renderer.contents.mapNotNull { it.musicTwoRowItemRenderer }.mapNotNull { RelatedPage.fromMusicTwoRowItemRenderer(it) }
            } ?: content.musicShelfRenderer?.let { renderer ->
                renderer.contents?.getItems()?.mapNotNull { SearchSummaryPage.fromMusicResponsiveListItemRenderer(it) }
            }
        }.flatten()
    }

    suspend fun playlistContinuation(continuation: String): Result<PlaylistContinuationPage> = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            continuation = continuation,
            setLogin = true
        ).body<BrowseResponse>()

        val mainContents: List<MusicShelfRenderer.Content> = response.continuationContents?.sectionListContinuation?.contents
            ?.mapNotNull { content: SectionListRenderer.Content -> content.musicPlaylistShelfRenderer?.contents }
            ?.flatten()
            ?: emptyList()

        val shelfContents: List<MusicShelfRenderer.Content> =
            response.continuationContents?.musicPlaylistShelfContinuation?.contents ?: emptyList()

        val appendedContents: List<MusicShelfRenderer.Content> = response.onResponseReceivedActions
            ?.firstOrNull()
            ?.appendContinuationItemsAction
            ?.continuationItems
            .orEmpty()

        val allContents = mainContents + shelfContents + appendedContents

        val songs = allContents
            .mapNotNull { content: MusicShelfRenderer.Content -> content.musicResponsiveListItemRenderer }
            .mapNotNull { renderer -> PlaylistPage.fromMusicResponsiveListItemRenderer(renderer) }

        val nextContinuation = if (songs.isEmpty()) null else {
            response.continuationContents
                ?.sectionListContinuation
                ?.continuations
                ?.getContinuation()
                ?: response.continuationContents
                    ?.musicPlaylistShelfContinuation
                    ?.continuations
                    ?.getContinuation()
                ?: response.continuationContents
                    ?.musicShelfContinuation
                    ?.continuations
                    ?.getContinuation()
                ?: response.onResponseReceivedActions
                    ?.firstOrNull()
                    ?.appendContinuationItemsAction
                    ?.continuationItems
                    ?.getContinuation()
        }

        PlaylistContinuationPage(
            songs = songs,
            continuation = nextContinuation
        )
    }

    suspend fun home(continuation: String? = null, params: String? = null): Result<HomePage> = runCatching {
        if (continuation != null) {
            return@runCatching homeContinuation(continuation).getOrThrow()
        }

        val response = innerTube.browse(WEB_REMIX, browseId = "FEmusic_home", params = params).body<BrowseResponse>()
        val continuation = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.continuations?.getContinuation()
        val sectionListRender = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer
        val sections = sectionListRender?.contents!!
            .mapNotNull { it.musicCarouselShelfRenderer }
            .mapNotNull {
                HomePage.Section.fromMusicCarouselShelfRenderer(it)
            }.toMutableList()
        val chips = sectionListRender.header?.chipCloudRenderer?.chips?.mapNotNull { HomePage.Chip.fromChipCloudChipRenderer(it.chipCloudChipRenderer) }
        HomePage(chips, sections, continuation)
    }

    private suspend fun homeContinuation(continuation: String): Result<HomePage> = runCatching {
        val response =
            innerTube.browse(WEB_REMIX, continuation = continuation).body<BrowseResponse>()
        val continuation =
            response.continuationContents?.sectionListContinuation?.continuations?.getContinuation()
        HomePage(
            null,
            response.continuationContents?.sectionListContinuation?.contents
            ?.mapNotNull { it.musicCarouselShelfRenderer }
            ?.mapNotNull {
                HomePage.Section.fromMusicCarouselShelfRenderer(it)
            }.orEmpty(), continuation
        )
    }

    suspend fun explore(): Result<ExplorePage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, browseId = "FEmusic_explore").body<BrowseResponse>()
        ExplorePage(
            newReleaseAlbums = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.find {
                it.musicCarouselShelfRenderer?.header?.musicCarouselShelfBasicHeaderRenderer?.moreContentButton?.buttonRenderer?.navigationEndpoint?.browseEndpoint?.browseId == "FEmusic_new_releases_albums"
            }?.musicCarouselShelfRenderer?.contents
                ?.mapNotNull { it.musicTwoRowItemRenderer }
                ?.mapNotNull(NewReleaseAlbumPage::fromMusicTwoRowItemRenderer).orEmpty(),
            moodAndGenres = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.find {
                it.musicCarouselShelfRenderer?.header?.musicCarouselShelfBasicHeaderRenderer?.moreContentButton?.buttonRenderer?.navigationEndpoint?.browseEndpoint?.browseId == "FEmusic_moods_and_genres"
            }?.musicCarouselShelfRenderer?.contents
                ?.mapNotNull { it.musicNavigationButtonRenderer }
                ?.mapNotNull(MoodAndGenres.Companion::fromMusicNavigationButtonRenderer)
                .orEmpty()
        )
    }

    suspend fun newReleaseAlbums(): Result<List<AlbumItem>> = runCatching {
        val response = innerTube.browse(WEB_REMIX, browseId = "FEmusic_new_releases_albums").body<BrowseResponse>()
        response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.firstOrNull()?.gridRenderer?.items
            ?.mapNotNull { it.musicTwoRowItemRenderer }
            ?.mapNotNull(NewReleaseAlbumPage::fromMusicTwoRowItemRenderer)
            .orEmpty()
    }

    suspend fun moodAndGenres(): Result<List<MoodAndGenres>> = runCatching {
        val response = innerTube.browse(WEB_REMIX, browseId = "FEmusic_moods_and_genres").body<BrowseResponse>()
        response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents!!
            .mapNotNull(MoodAndGenres.Companion::fromSectionListRendererContent)
    }

    suspend fun browse(browseId: String, params: String?): Result<BrowseResult> = runCatching {
        val response = innerTube.browse(WEB_REMIX, browseId = browseId, params = params).body<BrowseResponse>()
        BrowseResult(
            title = response.header?.musicHeaderRenderer?.title?.runs?.firstOrNull()?.text,
            items = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents?.mapNotNull { content ->
                when {
                    content.gridRenderer != null -> {
                        BrowseResult.Item(
                            title = content.gridRenderer.header?.gridHeaderRenderer?.title?.runs?.firstOrNull()?.text,
                            items = content.gridRenderer.items
                                .mapNotNull(GridRenderer.Item::musicTwoRowItemRenderer)
                                .mapNotNull(RelatedPage.Companion::fromMusicTwoRowItemRenderer)
                        )
                    }

                    content.musicCarouselShelfRenderer != null -> {
                        BrowseResult.Item(
                            title = content.musicCarouselShelfRenderer.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.firstOrNull()?.text,
                            items = content.musicCarouselShelfRenderer.contents
                                .mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
                                .mapNotNull(RelatedPage.Companion::fromMusicTwoRowItemRenderer)
                        )
                    }

                    else -> null
                }
            }.orEmpty()
        )
    }

    suspend fun library(browseId: String, tabIndex: Int = 0): Result<LibraryPage> {
        return runCatching {
            val response = innerTube.browse(
                client = WEB_REMIX,
                browseId = browseId,
                setLogin = true
            ).body<BrowseResponse>()

            val tabs = response.contents?.singleColumnBrowseResultsRenderer?.tabs
            val contents = if (tabs != null && tabs.size >= tabIndex) {
                tabs[tabIndex].tabRenderer.content?.sectionListRenderer?.contents?.firstOrNull()
            } else {
                null
            }

            when {
                contents?.gridRenderer != null -> {
                    val gridItems = contents.gridRenderer.items
                    val twoRowItems = gridItems.mapNotNull(GridRenderer.Item::musicTwoRowItemRenderer)
                    val parsedItems = twoRowItems.mapNotNull { LibraryPage.fromMusicTwoRowItemRenderer(it) }
                    LibraryPage(
                        items = parsedItems,
                        continuation = contents.gridRenderer.continuations?.getContinuation()
                    )
                }

                else -> { // contents?.musicShelfRenderer != null
                    val shelfContents = contents?.musicShelfRenderer?.contents
                    if (shelfContents == null) {
                        return@runCatching LibraryPage(items = emptyList(), continuation = null)
                    }
                    val listItemRenderers = shelfContents.mapNotNull(MusicShelfRenderer.Content::musicResponsiveListItemRenderer)
                    val parsedItems = listItemRenderers.mapNotNull { renderer ->
                        LibraryPage.fromMusicResponsiveListItemRenderer(renderer)
                    }
                    LibraryPage(
                        items = parsedItems,
                        continuation = contents.musicShelfRenderer.continuations?.getContinuation()
                    )
                }
            }
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    suspend fun libraryContinuation(continuation: String) = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            continuation = continuation,
            setLogin = true
        ).body<BrowseResponse>()

        val contents = response.continuationContents

        when {
            contents?.gridContinuation != null -> {
                LibraryContinuationPage(
                    items = contents.gridContinuation.items
                        .mapNotNull (GridRenderer.Item::musicTwoRowItemRenderer)
                        .mapNotNull { LibraryPage.fromMusicTwoRowItemRenderer(it) },
                    continuation = contents.gridContinuation.continuations?.getContinuation()
                )
            }

            else -> { // contents?.musicShelfContinuation != null
                LibraryContinuationPage(
                    items = contents?.musicShelfContinuation?.contents!!
                        .mapNotNull (MusicShelfRenderer.Content::musicResponsiveListItemRenderer)
                        .mapNotNull { LibraryPage.fromMusicResponsiveListItemRenderer(it) },
                    continuation = contents.musicShelfContinuation.continuations?.getContinuation()
                )
            }
        }
    }

    suspend fun libraryRecentActivity(): Result<LibraryPage> = runCatching {
        val continuation = LibraryFilter.FILTER_RECENT_ACTIVITY.value

        val response = innerTube.browse(
            client = WEB_REMIX,
            continuation = continuation,
            setLogin = true
        ).body<BrowseResponse>()

        val gridItems = response.continuationContents?.sectionListContinuation?.contents?.firstOrNull()
            ?.gridRenderer?.items
        
        if (gridItems == null) {
            return@runCatching LibraryPage(
                items = emptyList(),
                continuation = null
            )
        }
        
        val items = gridItems.mapNotNull {
            it.musicTwoRowItemRenderer?.let { renderer ->
                LibraryPage.fromMusicTwoRowItemRenderer(renderer)
            }
        }.toMutableList()

        items.forEachIndexed { index, item ->
            if (item is ArtistItem) {
                artist(item.id).getOrNull()?.artist?.let { fetchedArtist ->
                    items[index] = fetchedArtist.copy(thumbnail = item.thumbnail)
                }
            }
        }

        LibraryPage(
            items = items,
            continuation = null
        )
    }

    suspend fun getChartsPage(continuation: String? = null): Result<ChartsPage> = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            browseId = "FEmusic_charts",
            params = "ggMGCgQIgAQ%3D",
            continuation = continuation
        ).body<BrowseResponse>()

        val sections = mutableListOf<ChartsPage.ChartSection>()
    
        response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
            ?.tabRenderer?.content?.sectionListRenderer?.contents?.forEach { content ->
            
                content.musicCarouselShelfRenderer?.let { renderer ->
                    val title = renderer.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.firstOrNull()?.text
                        ?: return@forEach
                
                    val items = renderer.contents.mapNotNull { item ->
                        when {
                            item.musicResponsiveListItemRenderer != null -> 
                                convertToChartItem(item.musicResponsiveListItemRenderer)
                            item.musicTwoRowItemRenderer != null -> 
                                convertMusicTwoRowItem(item.musicTwoRowItemRenderer)
                            else -> null
                        }
                    }.filterNotNull()
                
                    if (items.isNotEmpty()) {
                        sections.add(
                            ChartsPage.ChartSection(
                                title = title,
                                items = items,
                                chartType = determineChartType(title)
                            )
                        )
                    }
                }
            
                content.gridRenderer?.let { renderer ->
                    val title = renderer.header?.gridHeaderRenderer?.title?.runs?.firstOrNull()?.text
                        ?: return@let
                
                    val items = renderer.items.mapNotNull { item ->
                        item.musicTwoRowItemRenderer?.let { renderer ->
                            convertMusicTwoRowItem(renderer)
                        }
                    }.filterNotNull()
                
                    if (items.isNotEmpty()) {
                        sections.add(
                            ChartsPage.ChartSection(
                                title = title,
                                items = items,
                                chartType = ChartsPage.ChartType.NEW_RELEASES
                            )
                        )
                    }
                }
            }

        ChartsPage(
            sections = sections,
            continuation = response.continuationContents?.sectionListContinuation?.continuations?.getContinuation()
        )
    }

    private fun determineChartType(title: String): ChartsPage.ChartType {
        return when {
            title.contains("Trending", ignoreCase = true) -> ChartsPage.ChartType.TRENDING
            title.contains("Top", ignoreCase = true) -> ChartsPage.ChartType.TOP
            else -> ChartsPage.ChartType.GENRE
        }
    }

    private fun convertToChartItem(renderer: MusicResponsiveListItemRenderer): YTItem? {
        return try {
            when {
                renderer.flexColumns.size >= 3 && renderer.playlistItemData?.videoId != null -> {
                    val firstColumn = renderer.flexColumns.getOrNull(0)
                        ?.musicResponsiveListItemFlexColumnRenderer
                        ?.text ?: return null
                
                    val secondColumn = renderer.flexColumns.getOrNull(1)
                        ?.musicResponsiveListItemFlexColumnRenderer
                        ?.text ?: return null

                    val titleRun = firstColumn.runs?.firstOrNull() ?: return null
                    val title = titleRun.text.takeIf { it.isNotBlank() } ?: return null

                    val artists = secondColumn.runs?.mapNotNull { run ->
                        run.text.takeIf { it.isNotBlank() }?.let { name ->
                            Artist(
                                name = name,
                                id = run.navigationEndpoint?.browseEndpoint?.browseId
                            )
                        }
                    } ?: emptyList()

                    val thirdColumn = renderer.flexColumns.getOrNull(2)
                        ?.musicResponsiveListItemFlexColumnRenderer
                        ?.text

                    SongItem(
                        id = renderer.playlistItemData.videoId,
                        title = title,
                        artists = artists,
                        thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        musicVideoType = null,
                        explicit = renderer.badges?.any { 
                            it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE" 
                        } == true,
                        chartPosition = thirdColumn?.runs?.firstOrNull()?.text?.toIntOrNull(),
                        chartChange = thirdColumn?.runs?.getOrNull(1)?.text
                    )
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun convertMusicTwoRowItem(renderer: MusicTwoRowItemRenderer): YTItem? {
        return try {
            when {
                renderer.isSong -> {
                    val subtitle = renderer.subtitle?.runs ?: return null
                    SongItem(
                        id = renderer.navigationEndpoint.watchEndpoint?.videoId ?: return null,
                        title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                        artists = subtitle.mapNotNull {
                            it.navigationEndpoint?.browseEndpoint?.browseId?.let { id ->
                                Artist(name = it.text, id = id)
                            }
                        },
                        thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        musicVideoType = null,
                        explicit = renderer.subtitleBadges?.any {
                            it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                        } == true
                    )
                }
                renderer.isAlbum -> {
                    AlbumItem(
                        browseId = renderer.navigationEndpoint.browseEndpoint?.browseId ?: return null,
                        playlistId = renderer.thumbnailOverlay?.musicItemThumbnailOverlayRenderer?.content
                            ?.musicPlayButtonRenderer?.playNavigationEndpoint
                            ?.watchPlaylistEndpoint?.playlistId ?: return null,
                        title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                        artists = renderer.subtitle?.runs?.oddElements()?.drop(1)?.mapNotNull {
                            it.navigationEndpoint?.browseEndpoint?.browseId?.let { id ->
                                Artist(name = it.text, id = id)
                            }
                        },
                        year = renderer.subtitle?.runs?.lastOrNull()?.text?.toIntOrNull(),
                        thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        explicit = renderer.subtitleBadges?.any {
                            it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                        } == true
                    )
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun musicHistory() = runCatching {
        val response = innerTube.browse(
            client = WEB_REMIX,
            browseId = "FEmusic_history",
            setLogin = true
        ).body<BrowseResponse>()

        HistoryPage(
            sections = response.contents?.singleColumnBrowseResultsRenderer?.tabs?.firstOrNull()
                ?.tabRenderer?.content?.sectionListRenderer?.contents
                ?.mapNotNull {
                    it.musicShelfRenderer?.let { musicShelfRenderer ->
                        HistoryPage.fromMusicShelfRenderer(musicShelfRenderer)
                    }
                }
        )
    }

    suspend fun likeVideo(videoId: String, like: Boolean) = runCatching {
        if (like)
            innerTube.likeVideo(WEB_REMIX, videoId)
        else
            innerTube.unlikeVideo(WEB_REMIX, videoId)
    }

    suspend fun likePlaylist(playlistId: String, like: Boolean) = runCatching {
        if (like)
            innerTube.likePlaylist(WEB_REMIX, playlistId)
        else
            innerTube.unlikePlaylist(WEB_REMIX, playlistId)
    }

    suspend fun subscribeChannel(channelId: String, subscribe: Boolean) = runCatching {
        if (subscribe)
            innerTube.subscribeChannel(WEB_REMIX, channelId)
        else
            innerTube.unsubscribeChannel(WEB_REMIX, channelId)
    }

    suspend fun getChannelId(browseId: String): String {
        artist(browseId).onSuccess {
            return it.artist.channelId ?: ""
        }
        return ""
    }

    suspend fun addToPlaylist(playlistId: String, videoId: String) = runCatching {
        innerTube.addToPlaylist(WEB_REMIX, playlistId, videoId)
    }

    suspend fun addPlaylistToPlaylist(playlistId: String, addPlaylistId: String) = runCatching {
        innerTube.addPlaylistToPlaylist(WEB_REMIX, playlistId, addPlaylistId)
    }

    suspend fun removeFromPlaylist(playlistId: String, videoId: String, setVideoId: String) = runCatching {
        innerTube.removeFromPlaylist(WEB_REMIX, playlistId, videoId, setVideoId)
    }

    suspend fun moveSongPlaylist(playlistId: String, setVideoId: String, successorSetVideoId: String?) = runCatching {
        innerTube.moveSongPlaylist(WEB_REMIX, playlistId, setVideoId, successorSetVideoId)
    }

    fun createPlaylist(title: String) = runBlocking {
        innerTube.createPlaylist(WEB_REMIX, title).body<CreatePlaylistResponse>().playlistId
    }

    suspend fun renamePlaylist(playlistId: String, name: String) = runCatching {
        innerTube.renamePlaylist(WEB_REMIX, playlistId, name)
    }

    suspend fun uploadCustomThumbnailLink(playlistId: String, image: ByteArray) = runCatching {
        val uploadUrl = innerTube.getUploadCustomThumbnailLink(WEB_REMIX, image.size).headers["x-guploader-uploadid"]
        val blobReq = innerTube.uploadCustomThumbnail(
            WEB_REMIX,
            uploadUrl!!,
            image
        )
        val blobId = Json.decodeFromString<ImageUploadResponse>(blobReq.bodyAsText()).encryptedBlobId
        innerTube.setThumbnailPlaylist(WEB_REMIX, playlistId, blobId).body<EditPlaylistResponse>().newHeader?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicResponsiveHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
    }

    suspend fun removeThumbnailPlaylist(playlistId: String) = runCatching {
        innerTube.removeThumbnailPlaylist(WEB_REMIX, playlistId).body<EditPlaylistResponse>().newHeader?.musicEditablePlaylistDetailHeaderRenderer?.header?.musicResponsiveHeaderRenderer?.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
    }

    suspend fun deletePlaylist(playlistId: String) = runCatching {
        innerTube.deletePlaylist(WEB_REMIX, playlistId)
    }

    suspend fun player(videoId: String, playlistId: String? = null, client: YouTubeClient, signatureTimestamp: Int? = null, poToken: String? = null): Result<com.arturo254.innertube.models.response.PlayerResponse> = runCatching {
        innerTube.player(client, videoId, playlistId, signatureTimestamp, poToken).body<com.arturo254.innertube.models.response.PlayerResponse>()
    }

    suspend fun registerPlayback(playlistId: String? = null, playbackTracking: String) = runCatching {
        val cpn = (1..16).map {
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_"[Random.Default.nextInt(
                0,
                64
            )]
        }.joinToString("")

        val playbackUrl = playbackTracking.replace(
            "https://s.youtube.com",
            "https://music.youtube.com",
        )

        innerTube.registerPlayback(
            url = playbackUrl,
            playlistId = playlistId,
            cpn = cpn
        )
    }

    suspend fun next(endpoint: WatchEndpoint, continuation: String? = null): Result<NextResult> = runCatching {
        val response = innerTube.next(
            WEB_REMIX,
            endpoint.videoId,
            endpoint.playlistId,
            endpoint.playlistSetVideoId,
            endpoint.index,
            endpoint.params,
            continuation).body<NextResponse>()
        val playlistPanelRenderer = response.continuationContents?.playlistPanelContinuation
            ?: response.contents.singleColumnMusicWatchNextResultsRenderer?.tabbedRenderer
                ?.watchNextTabbedResultsRenderer?.tabs?.get(0)?.tabRenderer?.content?.musicQueueRenderer
                ?.content?.playlistPanelRenderer!!
        val title = response.contents.singleColumnMusicWatchNextResultsRenderer?.tabbedRenderer
            ?.watchNextTabbedResultsRenderer?.tabs?.get(0)?.tabRenderer?.content?.musicQueueRenderer
            ?.header?.musicQueueHeaderRenderer?.subtitle?.runs?.firstOrNull()?.text
        val items = playlistPanelRenderer.contents.mapNotNull { content ->
            content.playlistPanelVideoRenderer
                ?.let(NextPage::fromPlaylistPanelVideoRenderer)
                ?.let { it to content.playlistPanelVideoRenderer.selected }
        }
        val songs = items.map { it.first }
        val currentIndex = items.indexOfFirst { it.second }.takeIf { it != -1 }

        // load automix items
        playlistPanelRenderer.contents.lastOrNull()?.automixPreviewVideoRenderer?.content?.automixPlaylistVideoRenderer?.navigationEndpoint?.watchPlaylistEndpoint?.let { watchPlaylistEndpoint ->
            return@runCatching next(watchPlaylistEndpoint).getOrThrow().let { result ->
                result.copy(
                    title = title,
                    items = songs + result.items,
                    lyricsEndpoint = response.contents.singleColumnMusicWatchNextResultsRenderer?.tabbedRenderer?.watchNextTabbedResultsRenderer?.tabs?.getOrNull(1)?.tabRenderer?.endpoint?.browseEndpoint,
                    relatedEndpoint = response.contents.singleColumnMusicWatchNextResultsRenderer?.tabbedRenderer?.watchNextTabbedResultsRenderer?.tabs?.getOrNull(2)?.tabRenderer?.endpoint?.browseEndpoint,
                    currentIndex = currentIndex,
                    endpoint = watchPlaylistEndpoint
                )
            }
        }
        NextResult(
            title = title,
            items = songs,
            currentIndex = currentIndex,
            lyricsEndpoint = response.contents.singleColumnMusicWatchNextResultsRenderer?.tabbedRenderer?.watchNextTabbedResultsRenderer?.tabs?.getOrNull(1)?.tabRenderer?.endpoint?.browseEndpoint,
            relatedEndpoint = response.contents.singleColumnMusicWatchNextResultsRenderer?.tabbedRenderer?.watchNextTabbedResultsRenderer?.tabs?.getOrNull(2)?.tabRenderer?.endpoint?.browseEndpoint,
            continuation = playlistPanelRenderer.continuations?.getContinuation(),
            endpoint = endpoint
        )
    }

    suspend fun lyrics(endpoint: BrowseEndpoint): Result<String?> = runCatching {
        val response = innerTube.browse(WEB_REMIX, endpoint.browseId, endpoint.params).body<BrowseResponse>()
        response.contents?.sectionListRenderer?.contents
            ?.firstOrNull { it.musicDescriptionShelfRenderer != null }
            ?.musicDescriptionShelfRenderer?.description?.runs
            ?.joinToString(separator = "") { it.text }
    }

    suspend fun related(endpoint: BrowseEndpoint): Result<RelatedPage> = runCatching {
        val response = innerTube.browse(WEB_REMIX, endpoint.browseId).body<BrowseResponse>()
        val songs = mutableListOf<SongItem>()
        val albums = mutableListOf<AlbumItem>()
        val artists = mutableListOf<ArtistItem>()
        val playlists = mutableListOf<PlaylistItem>()

        fun addItem(item: YTItem, renderer: MusicResponsiveListItemRenderer?) {
            when (item) {
                is SongItem -> {
                    val isAudioTrack = renderer?.overlay
                        ?.musicItemThumbnailOverlayRenderer?.content
                        ?.musicPlayButtonRenderer?.playNavigationEndpoint
                        ?.watchEndpoint?.watchEndpointMusicSupportedConfigs
                        ?.watchEndpointMusicConfig?.musicVideoType == MUSIC_VIDEO_TYPE_ATV
                    if (isAudioTrack) {
                        songs.add(item)
                    }
                }
                is AlbumItem -> albums.add(item)
                is ArtistItem -> artists.add(item)
                is PlaylistItem -> playlists.add(item)
            }
        }

        response.contents?.sectionListRenderer?.contents?.forEach { sectionContent ->
            sectionContent.musicCarouselShelfRenderer?.contents?.forEach { content ->
                val item = content.musicResponsiveListItemRenderer?.let(RelatedPage.Companion::fromMusicResponsiveListItemRenderer)
                    ?: content.musicTwoRowItemRenderer?.let(RelatedPage.Companion::fromMusicTwoRowItemRenderer)
                if (item != null) {
                    addItem(item, content.musicResponsiveListItemRenderer)
                }
            }

            sectionContent.musicShelfRenderer?.contents?.forEach { content ->
                val item = content.musicResponsiveListItemRenderer?.let(RelatedPage.Companion::fromMusicResponsiveListItemRenderer)
                if (item != null) {
                    addItem(item, content.musicResponsiveListItemRenderer)
                }
            }

            sectionContent.itemSectionRenderer?.contents?.forEach { content ->
                val item = content.musicResponsiveListItemRenderer?.let(RelatedPage.Companion::fromMusicResponsiveListItemRenderer)
                if (item != null) {
                    addItem(item, content.musicResponsiveListItemRenderer)
                }
            }
        }
        RelatedPage(songs, albums, artists, playlists)
    }

    suspend fun queue(videoIds: List<String>? = null, playlistId: String? = null): Result<List<SongItem>> = runCatching {
        if (videoIds != null) {
            assert(videoIds.size <= MAX_GET_QUEUE_SIZE)
        }
        innerTube.getQueue(WEB_REMIX, videoIds, playlistId).body<GetQueueResponse>().queueDatas
            .mapNotNull {
                it.content.playlistPanelVideoRenderer?.let { renderer ->
                    NextPage.fromPlaylistPanelVideoRenderer(renderer)
                }
            }
    }

    suspend fun transcript(videoId: String): Result<String> = runCatching {
        val response = innerTube.getTranscript(WEB, videoId).body<GetTranscriptResponse>()
        response.actions?.firstOrNull()?.updateEngagementPanelAction?.content?.transcriptRenderer?.body?.transcriptBodyRenderer?.cueGroups?.joinToString(separator = "\n") { group ->
            val time = group.transcriptCueGroupRenderer.cues[0].transcriptCueRenderer.startOffsetMs
            val text = group.transcriptCueGroupRenderer.cues[0].transcriptCueRenderer.cue.simpleText
                .trim('♪')
                .trim(' ')
            "[%02d:%02d.%03d]$text".format(time / 60000, (time / 1000) % 60, time % 1000)
        }!!
    }

    suspend fun visitorData(): Result<String> = runCatching {
        Json.parseToJsonElement(innerTube.getSwJsData().bodyAsText().substring(5))
            .jsonArray[0]
            .jsonArray[2]
            .jsonArray.first {
                (it as? JsonPrimitive)?.contentOrNull?.let { candidate ->
                    VISITOR_DATA_REGEX.containsMatchIn(candidate)
                } ?: false
            }
            .jsonPrimitive.content
    }

    suspend fun accountInfo(): Result<AccountInfo> = runCatching {
        innerTube.accountMenu(WEB_REMIX).body<AccountMenuResponse>()
            .actions[0].openPopupAction.popup.multiPageMenuRenderer
            .header?.activeAccountHeaderRenderer
            ?.toAccountInfo()!!
    }

    suspend fun feedback(tokens: List<String>): Result<Boolean> = runCatching {
        innerTube.feedback(WEB_REMIX, tokens).body<FeedbackResponse>().feedbackResponses.all { it.isProcessed }
    }

    suspend fun addSongToLibrary(videoId: String): Result<Boolean> = runCatching {
        val nextResult = next(WatchEndpoint(videoId = videoId)).getOrThrow()
        val song = nextResult.items.find { it.id == videoId }
            ?: throw Exception("Song not found in next response")
        
        val addToken = song.libraryAddToken
            ?: throw Exception("Add to library token not available")
        
        feedback(listOf(addToken)).getOrThrow()
    }

    suspend fun removeSongFromLibrary(videoId: String): Result<Boolean> = runCatching {
        val nextResult = next(WatchEndpoint(videoId = videoId)).getOrThrow()
        val song = nextResult.items.find { it.id == videoId }
            ?: throw Exception("Song not found in next response")
        
        val removeToken = song.libraryRemoveToken
            ?: throw Exception("Remove from library token not available")
        
        feedback(listOf(removeToken)).getOrThrow()
    }

    suspend fun toggleSongLibrary(videoId: String, addToLibrary: Boolean): Result<Boolean> = runCatching {
        if (addToLibrary) {
            addSongToLibrary(videoId).getOrThrow()
        } else {
            removeSongFromLibrary(videoId).getOrThrow()
        }
    }

    suspend fun getMediaInfo(videoId: String): Result<MediaInfo> = runCatching {
        return innerTube.getMediaInfo(videoId)
    }

    @JvmInline
    value class SearchFilter(val value: String) {
        companion object {
            val FILTER_SONG = SearchFilter("EgWKAQIIAWoKEAkQBRAKEAMQBA%3D%3D")
            val FILTER_VIDEO = SearchFilter("EgWKAQIQAWoKEAkQChAFEAMQBA%3D%3D")
            val FILTER_ALBUM = SearchFilter("EgWKAQIYAWoKEAkQChAFEAMQBA%3D%3D")
            val FILTER_ARTIST = SearchFilter("EgWKAQIgAWoKEAkQChAFEAMQBA%3D%3D")
            val FILTER_FEATURED_PLAYLIST = SearchFilter("EgeKAQQoADgBagwQDhAKEAMQBRAJEAQ%3D")
            val FILTER_COMMUNITY_PLAYLIST = SearchFilter("EgeKAQQoAEABagoQAxAEEAoQCRAF")
        }
    }

    @JvmInline
    value class LibraryFilter(val value: String) {
        companion object {
            val FILTER_RECENT_ACTIVITY = LibraryFilter("4qmFsgIrEhdGRW11c2ljX2xpYnJhcnlfbGFuZGluZxoQZ2dNR0tnUUlCaEFCb0FZQg%3D%3D")
            val FILTER_RECENTLY_PLAYED = LibraryFilter("4qmFsgIrEhdGRW11c2ljX2xpYnJhcnlfbGFuZGluZxoQZ2dNR0tnUUlCUkFCb0FZQg%3D%3D")
            val FILTER_PLAYLISTS_ALPHABETICAL = LibraryFilter("4qmFsgIrEhdGRW11c2ljX2xpa2VkX3BsYXlsaXN0cxoQZ2dNR0tnUUlBUkFBb0FZQg%3D%3D")
            val FILTER_PLAYLISTS_RECENTLY_SAVED = LibraryFilter("4qmFsgIrEhdGRW11c2ljX2xpa2VkX3BsYXlsaXN0cxoQZ2dNR0tnUUlBQkFCb0FZQg%3D%3D")
        }
    }

    const val MAX_GET_QUEUE_SIZE = 1000

    private val VISITOR_DATA_REGEX = Regex("^Cg[t|s]")

    fun getNewPipeStreamUrls(videoId: String): List<Pair<Int, String>> {
        return NewPipeExtractor.newPipePlayer(videoId)
    }

    suspend fun newPipePlayer(
        videoId: String,
        tempRes: com.arturo254.innertube.models.response.PlayerResponse,
    ): com.arturo254.innertube.models.response.PlayerResponse? {
        if (tempRes.playabilityStatus.status != "OK") {
            return null
        }

        val streamsList = getNewPipeStreamUrls(videoId)
        if (streamsList.isEmpty()) return null

        val decodedSigResponse = tempRes.copy(
            streamingData = tempRes.streamingData?.copy(
                formats = tempRes.streamingData.formats?.map { format ->
                    format.copy(
                        url = streamsList.find { it.first == format.itag }?.second ?: format.url,
                    )
                },
                adaptiveFormats = tempRes.streamingData.adaptiveFormats.map { adaptiveFormat ->
                    adaptiveFormat.copy(
                        url = streamsList.find { it.first == adaptiveFormat.itag }?.second ?: adaptiveFormat.url,
                    )
                },
            ),
        )

        val urlList = (
            decodedSigResponse.streamingData?.adaptiveFormats?.mapNotNull { it.url }?.toMutableList() ?: mutableListOf()
        ).apply {
            decodedSigResponse.streamingData?.formats?.mapNotNull { it.url }?.let { addAll(it) }
        }

        return if (urlList.isNotEmpty()) {
            decodedSigResponse
        } else {
            null
        }
    }

    suspend fun comments(videoId: String): Result<Pair<List<CommentThreadRenderer>, String?>> = runCatching {
        val response = innerTube.next(YouTubeClient.WEB, videoId, null, null, null, null, null).body<NextResponse>()
        
        val commentsPanel = response.engagementPanels?.firstOrNull { 
            it.engagementPanelSectionListRenderer?.panelIdentifier == "engagement-panel-comments-section"
        }
        val tokenFromEngagementPanels = commentsPanel?.engagementPanelSectionListRenderer?.content?.sectionListRenderer?.contents
            ?.mapNotNull { it.itemSectionRenderer }
            ?.flatMap { it.contents.orEmpty() }
            ?.mapNotNull { it?.continuationItemRenderer }
            ?.firstOrNull()?.continuationEndpoint?.continuationCommand?.token


        val contentList = response.contents.twoColumnWatchNextResults?.results?.results?.content

        val token =
            contentList?.mapNotNull { it?.continuationItemRenderer }
                ?.firstOrNull()
                ?.continuationEndpoint?.continuationCommand?.token
                ?: contentList?.mapNotNull { it?.itemSectionRenderer }
                    ?.flatMap { it.contents.orEmpty() }
                    ?.mapNotNull { it?.continuationItemRenderer }
                    ?.firstOrNull()
                    ?.continuationEndpoint?.continuationCommand?.token
                ?: tokenFromEngagementPanels
                ?: throw Exception("No comment continuation token found for videoId=$videoId")


        commentContinuation(token).getOrThrow()
    }

    suspend fun commentContinuation(continuationToken: String): Result<Pair<List<CommentThreadRenderer>, String?>> = runCatching {
        val response = innerTube.next(YouTubeClient.WEB, null, null, null, null, null, continuationToken).body<CommentResponse>()
        val endpoints = response.onResponseReceivedEndpoints.orEmpty()
        val continuationItems = endpoints.flatMap { endpoint ->
            endpoint.reloadContinuationItemsCommand?.continuationItems.orEmpty() +
            endpoint.appendContinuationItemsAction?.continuationItems.orEmpty()
        }

        val legacyComments = continuationItems.mapNotNull { it.commentThreadRenderer }
            .filter { it.comment?.commentRenderer != null || it.commentViewModel?.commentViewModel != null }

        val legacyCommentsMap = legacyComments.associateBy { thread ->
            thread.comment?.commentRenderer?.commentId 
                ?: thread.commentViewModel?.commentViewModel?.commentId
                ?: "legacy-${thread.hashCode()}"
        }

        val mutations = response.frameworkUpdates?.entityBatchUpdate?.mutations.orEmpty()
        val toolbarMap = mutations.mapNotNull { it.payload?.engagementToolbarStateEntityPayload }.associateBy { it.key }
        val surfaceMap = mutations.mapNotNull { it.payload?.engagementToolbarSurfaceEntityPayload }.associateBy { it.key }

        val commentsFromFramework = mutations.mapNotNull { mutation ->
            mutation.payload?.commentEntityPayload?.let { payload ->
                val toolbarKey = payload.properties?.toolbarStateKey
                val surfaceKey = payload.properties?.toolbarSurfaceKey
                val toolbarState = toolbarMap[toolbarKey]
                val surface = surfaceMap[surfaceKey]
                val likeCount = payload.toolbar?.likeCountNotliked
                    ?: surface?.toolbar?.likeCountNotliked
                    ?: "0"
                val replyCount = payload.toolbar?.replyCount
                    ?: surface?.toolbar?.replyCount
                    ?: "0"
                    
                val commentId = payload.properties?.commentId ?: "framework-${mutation.hashCode()}"
                val legacyMatch = legacyCommentsMap[commentId]

                CommentThreadRenderer(
                    comment = CommentThreadRenderer.CommentWrapper(
                        commentRenderer = CommentRenderer(
                            authorText = com.arturo254.innertube.models.Runs(
                                runs = listOf(com.arturo254.innertube.models.Run(text = payload.author?.displayName ?: "Unknown", navigationEndpoint = null))
                            ),
                            authorThumbnail = com.arturo254.innertube.models.Thumbnails(
                                thumbnails = listOf(com.arturo254.innertube.models.Thumbnail(url = payload.author?.avatarThumbnailUrl ?: "", width = 0, height = 0))
                            ),
                            contentText = com.arturo254.innertube.models.Runs(
                                runs = listOf(com.arturo254.innertube.models.Run(text = payload.properties?.content?.content ?: "", navigationEndpoint = null))
                            ),
                            publishedTimeText = com.arturo254.innertube.models.Runs(
                                runs = listOf(com.arturo254.innertube.models.Run(text = payload.properties?.publishedTime ?: "", navigationEndpoint = null))
                            ),
                            commentId = commentId,
                            voteCount = com.arturo254.innertube.models.Runs(
                                runs = listOf(com.arturo254.innertube.models.Run(text = likeCount, navigationEndpoint = null))
                            ),
                            voteStatus = when (toolbarState?.likeState) {
                                "TOOLBAR_LIKE_STATE_LIKE" -> "UPVOTE"
                                "TOOLBAR_LIKE_STATE_INDIFFERENT" -> "INDIFFERENT"
                                else -> "INDIFFERENT"
                            },
                            replyCount = replyCount.toIntOrNull() ?: 0
                        )
                    ),
                    replies = legacyMatch?.replies
                )
            }
        }

        val nextToken = continuationItems.mapNotNull { item ->
            item.continuationItemRenderer?.let { renderer ->
                renderer.continuationEndpoint?.continuationCommand?.token
                    ?: renderer.button?.buttonRenderer?.command?.continuationCommand?.token
                    ?: renderer.button?.buttonRenderer?.navigationEndpoint?.continuationCommand?.token
            }
        }.firstOrNull()
            ?: endpoints.mapNotNull { endpoint ->
                endpoint.appendContinuationItemsAction?.continuationItems?.mapNotNull { it.continuationItemRenderer?.continuationEndpoint?.continuationCommand?.token }
            }.flatten().firstOrNull()
        
        val frameworkCommentsMap = commentsFromFramework.associateBy { it.comment?.commentRenderer?.commentId }
        val allIds = (legacyCommentsMap.keys + frameworkCommentsMap.keys).filterNotNull().distinct()

        val comments = allIds.mapNotNull { id ->
            val legacy = legacyCommentsMap[id]
            val modern = frameworkCommentsMap[id]

            if (modern != null) {
                modern
            } else {
                legacy
            }
        }.distinctBy { it.comment?.commentRenderer?.commentId ?: it.hashCode() }
        
        Pair(comments, nextToken)
    }

    suspend fun commentReplies(replyToken: String): Result<Pair<List<CommentRenderer>, String?>> = runCatching {
        val response = innerTube.next(YouTubeClient.WEB, null, null, null, null, null, replyToken).body<CommentResponse>()
        
        val endpoints = response.onResponseReceivedEndpoints.orEmpty()
        val continuationItems = endpoints.flatMap { endpoint ->
            endpoint.reloadContinuationItemsCommand?.continuationItems.orEmpty() +
            endpoint.appendContinuationItemsAction?.continuationItems.orEmpty()
        }

        val legacyReplies = continuationItems.mapNotNull { it.commentRenderer ?: it.commentThreadRenderer?.comment?.commentRenderer }

        val mutations = response.frameworkUpdates?.entityBatchUpdate?.mutations.orEmpty()
        
        val toolbarMap = mutations.mapNotNull { it.payload?.engagementToolbarStateEntityPayload }.associateBy { it.key }
        val surfaceMap = mutations.mapNotNull { it.payload?.engagementToolbarSurfaceEntityPayload }.associateBy { it.key }

        val frameworkReplies = mutations.mapNotNull { mutation ->
            mutation.payload?.commentEntityPayload?.let { payload ->
                val toolbarKey = payload.properties?.toolbarStateKey
                val surfaceKey = payload.properties?.toolbarSurfaceKey
                val toolbarState = toolbarMap[toolbarKey]
                val surface = surfaceMap[surfaceKey]
                val likeCount = payload.toolbar?.likeCountNotliked
                    ?: surface?.toolbar?.likeCountNotliked
                    ?: "0"

                CommentRenderer(
                    authorText = com.arturo254.innertube.models.Runs(
                        runs = listOf(com.arturo254.innertube.models.Run(text = payload.author?.displayName ?: "Unknown", navigationEndpoint = null))
                    ),
                    authorThumbnail = com.arturo254.innertube.models.Thumbnails(
                        thumbnails = listOf(com.arturo254.innertube.models.Thumbnail(url = payload.author?.avatarThumbnailUrl ?: "", width = 0, height = 0))
                    ),
                    contentText = com.arturo254.innertube.models.Runs(
                        runs = listOf(com.arturo254.innertube.models.Run(text = payload.properties?.content?.content ?: "", navigationEndpoint = null))
                    ),
                    publishedTimeText = com.arturo254.innertube.models.Runs(
                        runs = listOf(com.arturo254.innertube.models.Run(text = payload.properties?.publishedTime ?: "", navigationEndpoint = null))
                    ),
                    commentId = payload.properties?.commentId ?: "reply-${mutation.hashCode()}",
                    voteCount = com.arturo254.innertube.models.Runs(
                        runs = listOf(com.arturo254.innertube.models.Run(text = likeCount, navigationEndpoint = null))
                    ),
                    voteStatus = when (toolbarState?.likeState) {
                        "TOOLBAR_LIKE_STATE_LIKE" -> "UPVOTE"
                        "TOOLBAR_LIKE_STATE_INDIFFERENT" -> "INDIFFERENT"
                        else -> "INDIFFERENT"
                    }
                )
            }
        }

        val allRepliesMap = legacyReplies.associateBy { it.commentId }
        val allFrameworkRepliesMap = frameworkReplies.associateBy { it.commentId }
        val allIds = (allRepliesMap.keys + allFrameworkRepliesMap.keys).filterNotNull().distinct()

        val mergedReplies = allIds.mapNotNull { id ->
            val legacy = allRepliesMap[id]
            val modern = allFrameworkRepliesMap[id]

            if (legacy != null && modern != null) {
                legacy.copy(
                    voteCount = modern.voteCount ?: legacy.voteCount,
                    voteStatus = modern.voteStatus ?: legacy.voteStatus,
                    replyCount = modern.replyCount ?: legacy.replyCount
                )
            } else {
                modern ?: legacy
            }
        }.distinctBy { it.commentId ?: it.hashCode() }
        
        val nextToken = continuationItems.mapNotNull { item ->
            item.continuationItemRenderer?.let { renderer ->
                renderer.continuationEndpoint?.continuationCommand?.token
                    ?: renderer.button?.buttonRenderer?.command?.continuationCommand?.token
                    ?: renderer.button?.buttonRenderer?.navigationEndpoint?.continuationCommand?.token
            }
        }.firstOrNull()
            ?: endpoints.mapNotNull { endpoint ->
                endpoint.appendContinuationItemsAction?.continuationItems?.mapNotNull { it.continuationItemRenderer?.continuationEndpoint?.continuationCommand?.token }
            }.flatten().firstOrNull()

        Pair(mergedReplies, nextToken)
    }
}
