package com.arturo254.innertube.models

import com.arturo254.innertube.models.response.BrowseResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class SectionListRenderer(
    val header: Header?,
    val contents: List<Content>?,
    val continuations: List<Continuation>?,
) {
    @Serializable
    data class Header(
        val chipCloudRenderer: ChipCloudRenderer?,
    ) {
        @Serializable
        data class ChipCloudRenderer(
            val chips: List<Chip>,
        ) {
            @Serializable
            data class Chip(
                val chipCloudChipRenderer: ChipCloudChipRenderer,
            ) {
                @Serializable
                data class ChipCloudChipRenderer(
                    val isSelected: Boolean,
                    val navigationEndpoint: NavigationEndpoint,
                    val onDeselectedCommand: NavigationEndpoint? = null, 
                    val text: Runs?,
                    val uniqueId: String?,
                )
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class Content(
        @JsonNames("musicImmersiveCarouselShelfRenderer")
        val musicCarouselShelfRenderer: MusicCarouselShelfRenderer?,
        val musicShelfRenderer: MusicShelfRenderer?,
        val musicCardShelfRenderer: MusicCardShelfRenderer?,
        val musicPlaylistShelfRenderer: MusicPlaylistShelfRenderer?,
        val musicDescriptionShelfRenderer: MusicDescriptionShelfRenderer?,
        val musicResponsiveHeaderRenderer: BrowseResponse.Header.MusicHeaderRenderer?, 
        val musicEditablePlaylistDetailHeaderRenderer: BrowseResponse.Header.MusicEditablePlaylistDetailHeaderRenderer?,
        val gridRenderer: GridRenderer?,
        val itemSectionRenderer: ItemSectionRenderer? = null
    ) {
        @Serializable
        data class ItemSectionRenderer(
            val contents: List<ItemSectionContent>? = null
        ) {
            @Serializable
            data class ItemSectionContent(
                val musicResponsiveListItemRenderer: MusicResponsiveListItemRenderer? = null,
                val continuationItemRenderer: ContinuationItemRenderer? = null
            )
        }
    }
}
