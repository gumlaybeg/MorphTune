package com.arturo254.innertube.pages

import com.arturo254.innertube.YouTubeConstants
import com.arturo254.innertube.models.Album
import com.arturo254.innertube.models.AlbumItem
import com.arturo254.innertube.models.Artist
import com.arturo254.innertube.models.ArtistItem
import com.arturo254.innertube.models.BrowseEndpoint.BrowseEndpointContextSupportedConfigs.BrowseEndpointContextMusicConfig.Companion.MUSIC_PAGE_TYPE_ALBUM
import com.arturo254.innertube.models.BrowseEndpoint.BrowseEndpointContextSupportedConfigs.BrowseEndpointContextMusicConfig.Companion.MUSIC_PAGE_TYPE_ARTIST
import com.arturo254.innertube.models.BrowseEndpoint.BrowseEndpointContextSupportedConfigs.BrowseEndpointContextMusicConfig.Companion.MUSIC_PAGE_TYPE_USER_CHANNEL
import com.arturo254.innertube.models.MusicCardShelfRenderer
import com.arturo254.innertube.models.MusicResponsiveListItemRenderer
import com.arturo254.innertube.models.PlaylistItem
import com.arturo254.innertube.models.SongItem
import com.arturo254.innertube.models.YTItem
import com.arturo254.innertube.models.filterExplicit
import com.arturo254.innertube.models.getItems
import com.arturo254.innertube.models.oddElements
import com.arturo254.innertube.models.splitBySeparator
import com.arturo254.innertube.utils.parseTime

data class SearchSummary(
    val title: String,
    val items: List<YTItem>,
)

data class SearchSummaryPage(
    val summaries: List<SearchSummary>,
) {
    fun filterExplicit(enabled: Boolean) =
        if (enabled) {
            SearchSummaryPage(
                summaries.mapNotNull { s ->
                    SearchSummary(
                        title = s.title,
                        items =
                            s.items.filterExplicit().ifEmpty {
                                return@mapNotNull null
                            },
                    )
                },
            )
        } else {
            this
        }

    companion object {
        fun fromMusicCardShelfRenderer(renderer: MusicCardShelfRenderer): YTItem? {
            val subtitle = renderer.subtitle.runs?.splitBySeparator() ?: emptyList()
            
            var album: Album? = null
            val artists = mutableListOf<Artist>()
            var views: String? = null
            var duration: Int? = null
            
            subtitle.forEach { runs ->
                val text = runs.joinToString("") { it.text }.trim()
                if (text.isEmpty()) return@forEach

                var hasEndpoint = false
                runs.forEach { run ->
                    val pageType = run.navigationEndpoint?.browseEndpoint?.browseEndpointContextSupportedConfigs?.browseEndpointContextMusicConfig?.pageType
                    if (pageType == MUSIC_PAGE_TYPE_ALBUM || pageType == "MUSIC_PAGE_TYPE_ALBUM") {
                        album = Album(name = run.text, id = run.navigationEndpoint.browseEndpoint.browseId!!)
                        hasEndpoint = true
                    } else if (pageType == MUSIC_PAGE_TYPE_ARTIST || pageType == "MUSIC_PAGE_TYPE_ARTIST" || pageType == MUSIC_PAGE_TYPE_USER_CHANNEL || pageType == "MUSIC_PAGE_TYPE_USER_CHANNEL") {
                        artists.add(Artist(name = run.text, id = run.navigationEndpoint.browseEndpoint.browseId!!))
                        hasEndpoint = true
                    }
                }

                if (!hasEndpoint) {
                    if (text.parseTime() != null) {
                        duration = text.parseTime()
                    } else if (text.any { it.isDigit() } && text.contains(Regex("view|play", RegexOption.IGNORE_CASE))) {
                        views = text
                    } else if (text.equals("Song", true) || text.equals("Video", true) || text.equals("Explicit", true)) {
                        // ignore
                    } else if (artists.isEmpty() && !text.matches(Regex("^\\d{4}$"))) {
                        runs.oddElements().forEach { run ->
                            artists.add(Artist(name = run.text, id = run.navigationEndpoint?.browseEndpoint?.browseId))
                        }
                    }
                }
            }
            
            val titleText = renderer.title.runs?.firstOrNull()?.text
            if (artists.isEmpty() && titleText != null) {
                if (renderer.onTap.watchEndpoint != null) {
                    val headerTitle = renderer.header?.musicCardShelfHeaderBasicRenderer?.title?.runs?.firstOrNull()?.text
                    if (headerTitle != null) {
                        artists.add(Artist(name = headerTitle, id = null))
                    }
                }
            }

            return when {
                renderer.onTap.watchEndpoint != null -> {
                    SongItem(
                        id = renderer.onTap.watchEndpoint.videoId ?: return null,
                        title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                        artists = artists.ifEmpty { return null },
                        album = album,
                        duration = duration,
                        thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        explicit = renderer.subtitleBadges?.find { it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE" } != null,
                        musicVideoType = renderer.onTap.watchEndpoint.watchEndpointMusicSupportedConfigs?.watchEndpointMusicConfig?.musicVideoType,
                        views = views
                    )
                }

                renderer.onTap.browseEndpoint?.isArtistEndpoint == true -> {
                    ArtistItem(
                        id = renderer.onTap.browseEndpoint.browseId,
                        title =
                            renderer.title.runs
                                ?.firstOrNull()
                                ?.text ?: return null,
                        thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        shuffleEndpoint =
                            renderer.buttons
                                .find { it.buttonRenderer.icon?.iconType == "MUSIC_SHUFFLE" }
                                ?.buttonRenderer
                                ?.command
                                ?.watchPlaylistEndpoint ?: return null,
                        radioEndpoint =
                            renderer.buttons
                                .find { it.buttonRenderer.icon?.iconType == "MIX" }
                                ?.buttonRenderer
                                ?.command
                                ?.watchPlaylistEndpoint ?: return null,
                    )
                }

                renderer.onTap.browseEndpoint?.isAlbumEndpoint == true -> {
                    AlbumItem(
                        browseId = renderer.onTap.browseEndpoint.browseId,
                        playlistId =
                            renderer.buttons
                                .firstOrNull()
                                ?.buttonRenderer
                                ?.command
                                ?.anyWatchEndpoint
                                ?.playlistId ?: return null,
                        title =
                            renderer.title.runs
                                ?.firstOrNull()
                                ?.text ?: return null,
                        artists = artists.ifEmpty { return null },
                        year = null,
                        thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        explicit =
                            renderer.subtitleBadges?.find {
                                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                            } != null,
                    )
                }

                renderer.onTap.browseEndpoint?.isPlaylistEndpoint == true -> {
                    PlaylistItem(
                        id =
                            renderer.onTap.browseEndpoint.browseId
                                .removePrefix("VL"),
                        title =
                            renderer.header?.musicCardShelfHeaderBasicRenderer?.title?.runs
                                ?.joinToString(separator = "") { it.text }
                                ?: return null,
                        author =
                            Artist(
                                id = null,
                                name = renderer.subtitle.runs?.joinToString { it.text } ?: return null,
                            ),
                        songCountText = null,
                        thumbnail = renderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        playEndpoint =
                            renderer.buttons
                                .find { it.buttonRenderer.icon?.iconType == "PLAY_ARROW" }
                                ?.buttonRenderer
                                ?.command
                                ?.watchPlaylistEndpoint
                                ?: return null,
                        shuffleEndpoint =
                            renderer.buttons
                                .find { it.buttonRenderer.icon?.iconType == "MUSIC_SHUFFLE" }
                                ?.buttonRenderer
                                ?.command
                                ?.watchPlaylistEndpoint
                                ?: return null,
                        radioEndpoint = null,
                    )
                }

                else -> null
            }
        }

        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer, fallbackArtist: String? = null): YTItem? {
            val secondaryLine = renderer.flexColumns.getOrNull(1)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.splitBySeparator() ?: emptyList()
            val thirdLine = renderer.flexColumns.getOrNull(2)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.splitBySeparator() ?: emptyList()
            val allRuns = (secondaryLine + thirdLine)
            
            var album: Album? = null
            val artists = mutableListOf<Artist>()
            var views: String? = null
            var duration: Int? = null

            allRuns.forEach { runs ->
                val text = runs.joinToString("") { it.text }.trim()
                if (text.isEmpty()) return@forEach

                var hasEndpoint = false
                runs.forEach { run ->
                    val pageType = run.navigationEndpoint?.browseEndpoint?.browseEndpointContextSupportedConfigs?.browseEndpointContextMusicConfig?.pageType
                    if (pageType == MUSIC_PAGE_TYPE_ALBUM || pageType == "MUSIC_PAGE_TYPE_ALBUM") {
                        album = Album(name = run.text, id = run.navigationEndpoint.browseEndpoint.browseId!!)
                        hasEndpoint = true
                    } else if (pageType == MUSIC_PAGE_TYPE_ARTIST || pageType == "MUSIC_PAGE_TYPE_ARTIST" || pageType == MUSIC_PAGE_TYPE_USER_CHANNEL || pageType == "MUSIC_PAGE_TYPE_USER_CHANNEL") {
                        artists.add(Artist(name = run.text, id = run.navigationEndpoint.browseEndpoint.browseId!!))
                        hasEndpoint = true
                    }
                }

                if (!hasEndpoint) {
                    if (text.parseTime() != null) {
                        duration = text.parseTime()
                    } else if (text.any { it.isDigit() } && text.contains(Regex("view|play", RegexOption.IGNORE_CASE))) {
                        views = text
                    } else if (text.equals("Song", true) || text.equals("Video", true) || text.equals("Explicit", true)) {
                        // ignore
                    } else if (artists.isEmpty() && !text.matches(Regex("^\\d{4}$"))) {
                        runs.oddElements().forEach { run ->
                            artists.add(Artist(name = run.text, id = run.navigationEndpoint?.browseEndpoint?.browseId))
                        }
                    }
                }
            }

            if (artists.isEmpty() && fallbackArtist != null) {
                artists.add(Artist(name = fallbackArtist, id = null))
            }

            val watchEndpoint = renderer.navigationEndpoint?.watchEndpoint 
                ?: renderer.overlay?.musicItemThumbnailOverlayRenderer?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint

            return when {
                renderer.isSong -> {
                    SongItem(
                        id = renderer.playlistItemData?.videoId ?: return null,
                        title = renderer.flexColumns.firstOrNull()?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()?.text ?: return null,
                        artists = artists.ifEmpty { return null },
                        album = album,
                        duration = duration,
                        thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        explicit = renderer.badges?.find { it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE" } != null,
                        musicVideoType = watchEndpoint?.watchEndpointMusicSupportedConfigs?.watchEndpointMusicConfig?.musicVideoType,
                        views = views
                    )
                }

                renderer.isArtist -> {
                    ArtistItem(
                        id = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                        title =
                            renderer.flexColumns
                                .firstOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.text
                                ?: return null,
                        thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        shuffleEndpoint =
                            renderer.menu
                                ?.menuRenderer
                                ?.items
                                ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE" }
                                ?.menuNavigationItemRenderer
                                ?.navigationEndpoint
                                ?.watchPlaylistEndpoint ?: return null,
                        radioEndpoint =
                            renderer.menu?.menuRenderer?.items
                                ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MIX" }
                                ?.menuNavigationItemRenderer
                                ?.navigationEndpoint
                                ?.watchPlaylistEndpoint ?: return null,
                    )
                }

                renderer.isAlbum -> {
                    AlbumItem(
                        browseId = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                        playlistId =
                            renderer.overlay
                                ?.musicItemThumbnailOverlayRenderer
                                ?.content
                                ?.musicPlayButtonRenderer
                                ?.playNavigationEndpoint
                                ?.watchPlaylistEndpoint
                                ?.playlistId
                                ?: return null,
                        title =
                            renderer.flexColumns
                                .firstOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.text ?: return null,
                        artists = artists.ifEmpty { return null },
                        year = null,
                        thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        explicit =
                            renderer.badges?.find {
                                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                            } != null,
                    )
                }

                renderer.isPlaylist -> {
                    PlaylistItem(
                        id =
                            renderer.navigationEndpoint
                                ?.browseEndpoint
                                ?.browseId
                                ?.removePrefix("VL") ?: return null,
                        title =
                            renderer.flexColumns
                                .firstOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.text ?: return null,
                        author =
                            artists.firstOrNull() ?: return null,
                        songCountText =
                            renderer.flexColumns
                                .getOrNull(1)
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.lastOrNull()
                                ?.text ?: return null,
                        thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                        playEndpoint =
                            renderer.overlay
                                ?.musicItemThumbnailOverlayRenderer
                                ?.content
                                ?.musicPlayButtonRenderer
                                ?.playNavigationEndpoint
                                ?.watchPlaylistEndpoint ?: return null,
                        shuffleEndpoint =
                            renderer.menu
                                ?.menuRenderer
                                ?.items
                                ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE" }
                                ?.menuNavigationItemRenderer
                                ?.navigationEndpoint
                                ?.watchPlaylistEndpoint ?: return null,
                        radioEndpoint =
                            renderer.menu?.menuRenderer?.items
                                ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MIX" }
                                ?.menuNavigationItemRenderer
                                ?.navigationEndpoint
                                ?.watchPlaylistEndpoint ?: return null,
                    )
                }

                else -> null
            }
        }
    }
}
