package com.arturo254.innertube.models.body

import com.arturo254.innertube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class EditPlaylistBody(
    val context: Context,
    val playlistId: String,
    val actions: List<Action>
)

@Serializable
sealed class Action {
    @Serializable
    data class AddVideoAction(
        val action: String = "ACTION_ADD_VIDEO",
        val addedVideoId: String
    ) : Action()

    @Serializable
    data class AddPlaylistAction(
        val action: String = "ACTION_ADD_PLAYLIST",
        val addedFullListId: String
    ) : Action()

    @Serializable
    data class MoveVideoAction(
        val action: String = "ACTION_MOVE_VIDEO_BEFORE",
        val setVideoId: String?,
        val movedSetVideoIdSuccessor: String? = null
    ) : Action()

    @Serializable
    data class RemoveVideoAction(
        val action: String = "ACTION_REMOVE_VIDEO",
        val setVideoId: String?,
        val removedVideoId: String?
    ) : Action()

    @Serializable
    data class RenamePlaylistAction(
        val action: String = "ACTION_SET_PLAYLIST_NAME",
        val playlistName: String
    ) : Action()

    @Serializable
    data class SetCustomThumbnailAction(
        val action: String = "ACTION_SET_PLAYLIST_CUSTOM_THUMBNAIL",
        val addedCustomThumbnail: AddedCustomThumbnail
    ) : Action() {
        @Serializable
        data class AddedCustomThumbnail(val playlistScottyEncryptedBlobId: String)
    }

    @Serializable
    data class RemoveCustomThumbnailAction(
        val action: String = "ACTION_REMOVE_PLAYLIST_CUSTOM_THUMBNAIL"
    ) : Action()
}
