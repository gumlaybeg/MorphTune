package com.arturo254.innertube.models.response

import com.arturo254.innertube.models.NavigationEndpoint
import com.arturo254.innertube.models.PlaylistPanelRenderer
import com.arturo254.innertube.models.Tabs
import com.arturo254.innertube.models.YouTubeDataPage
import kotlinx.serialization.Serializable

@Serializable
data class NextResponse(
    val contents: Contents,
    val continuationContents: ContinuationContents? = null,
    val currentVideoEndpoint: NavigationEndpoint? = null,
    val engagementPanels: List<EngagementPanel>? = null,
) {
    @Serializable
    data class Contents(
        val singleColumnMusicWatchNextResultsRenderer: SingleColumnMusicWatchNextResultsRenderer? = null,
        val twoColumnWatchNextResults: YouTubeDataPage.Contents.TwoColumnWatchNextResults? = null,
    ) {
        @Serializable
        data class SingleColumnMusicWatchNextResultsRenderer(
            val tabbedRenderer: TabbedRenderer,
        ) {
            @Serializable
            data class TabbedRenderer(
                val watchNextTabbedResultsRenderer: WatchNextTabbedResultsRenderer,
            ) {
                @Serializable
                data class WatchNextTabbedResultsRenderer(
                    val tabs: List<Tabs.Tab>,
                )
            }
        }
    }

    @Serializable
    data class ContinuationContents(
        val playlistPanelContinuation: PlaylistPanelRenderer,
    )

    @Serializable
    data class EngagementPanel(
        val engagementPanelSectionListRenderer: EngagementPanelSectionListRenderer? = null
    ) {
        @Serializable
        data class EngagementPanelSectionListRenderer(
            val panelIdentifier: String?,
            val content: Content?
        ) {
            @Serializable
            data class Content(
                val sectionListRenderer: SectionListRenderer?
            ) {
                @Serializable
                data class SectionListRenderer(
                    val contents: List<ItemSectionRendererWrapper>?
                ) {
                    @Serializable
                    data class ItemSectionRendererWrapper(
                        val itemSectionRenderer: ItemSectionRenderer?
                    ) {
                        @Serializable
                        data class ItemSectionRenderer(
                            val contents: List<ContinuationItemRendererWrapper>?
                        ) {
                            @Serializable
                            data class ContinuationItemRendererWrapper(
                                val continuationItemRenderer: com.arturo254.innertube.models.ContinuationItemRenderer?
                            )
                        }
                    }
                }
            }
        }
    }
}
