package com.arturo254.innertube.models.response

import kotlinx.serialization.Serializable
import com.arturo254.innertube.models.Thumbnails

@Serializable
data class EditPlaylistResponse(val newHeader: NewHeader? = null) {
    @Serializable
    data class NewHeader(val musicEditablePlaylistDetailHeaderRenderer: MusicEditablePlaylistDetailHeaderRenderer? = null) {
        @Serializable
        data class MusicEditablePlaylistDetailHeaderRenderer(val header: Header? = null) {
            @Serializable
            data class Header(val musicResponsiveHeaderRenderer: MusicResponsiveHeaderRenderer? = null) {
                @Serializable
                data class MusicResponsiveHeaderRenderer(val thumbnail: Thumbnail? = null) {
                    @Serializable
                    data class Thumbnail(val musicThumbnailRenderer: MusicThumbnailRenderer? = null) {
                        @Serializable
                        data class MusicThumbnailRenderer(val thumbnail: Thumbnails? = null) {
                            fun getThumbnailUrl() = thumbnail?.thumbnails?.lastOrNull()?.url
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data class FeedbackResponse(val feedbackResponses: List<FeedbackResponseItem> = emptyList()) {
    @Serializable
    data class FeedbackResponseItem(val isProcessed: Boolean = false)
}

@Serializable
data class ImageUploadResponse(val encryptedBlobId: String = "")

@Serializable
data class ReturnYouTubeDislikeResponse(
    val likes: Int = 0,
    val dislikes: Int = 0,
    val viewCount: Int = 0
)
