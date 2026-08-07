package com.arturo254.innertube.models.comment

import kotlinx.serialization.Serializable
import com.arturo254.innertube.models.Runs
import com.arturo254.innertube.models.Thumbnails
import com.arturo254.innertube.models.ContinuationItemRenderer

@Serializable
data class CommentResponse(
    val onResponseReceivedEndpoints: List<OnResponseReceivedEndpoint>? = null,
    val frameworkUpdates: FrameworkUpdates? = null
)

@Serializable
data class OnResponseReceivedEndpoint(
    val reloadContinuationItemsCommand: ContinuationItemsCommand? = null,
    val appendContinuationItemsAction: ContinuationItemsCommand? = null
)

@Serializable
data class ContinuationItemsCommand(
    val continuationItems: List<ContinuationItem>? = null
)

@Serializable
data class ContinuationItem(
    val commentThreadRenderer: CommentThreadRenderer? = null,
    val commentRenderer: CommentRenderer? = null,
    val commentViewModel: CommentViewModelWrapper? = null,
    val continuationItemRenderer: ContinuationItemRenderer? = null
)

@Serializable
data class CommentViewModelWrapper(val commentViewModel: CommentViewModel?)

@Serializable
data class CommentViewModel(val commentId: String?)

@Serializable
data class CommentThreadRenderer(
    val comment: CommentWrapper? = null,
    val replies: Replies? = null
) {
    @Serializable data class CommentWrapper(val commentRenderer: CommentRenderer?)
    @Serializable data class Replies(val commentRepliesRenderer: CommentRepliesRenderer?)
    @Serializable data class CommentRepliesRenderer(val contents: List<ContinuationItemRenderer>?)
}

@Serializable
data class CommentRenderer(
    val commentId: String? = null,
    val authorText: Runs? = null,
    val authorThumbnail: Thumbnails? = null,
    val contentText: Runs? = null,
    val publishedTimeText: Runs? = null,
    val voteCount: Runs? = null,
    val voteStatus: String? = null,
    val replyCount: Int = 0
)

@Serializable
data class FrameworkUpdates(val entityBatchUpdate: EntityBatchUpdate?)

@Serializable
data class EntityBatchUpdate(val mutations: List<Mutation>?)

@Serializable
data class Mutation(val payload: Payload?)

@Serializable
data class Payload(
    val commentEntityPayload: CommentEntityPayload? = null,
    val engagementToolbarStateEntityPayload: EngagementToolbarStateEntityPayload? = null,
    val engagementToolbarSurfaceEntityPayload: EngagementToolbarSurfaceEntityPayload? = null
)

@Serializable
data class CommentEntityPayload(
    val properties: CommentProperties? = null,
    val author: CommentAuthor? = null,
    val toolbar: CommentToolbar? = null
)

@Serializable
data class CommentProperties(
    val commentId: String? = null,
    val content: CommentContent? = null,
    val publishedTime: String? = null,
    val toolbarStateKey: String? = null,
    val toolbarSurfaceKey: String? = null
)

@Serializable
data class CommentContent(val content: String? = null)

@Serializable
data class CommentAuthor(
    val displayName: String? = null,
    val avatarThumbnailUrl: String? = null
)

@Serializable
data class CommentToolbar(
    val likeCountNotliked: String? = null,
    val replyCount: String? = null
)

@Serializable
data class EngagementToolbarStateEntityPayload(
    val key: String? = null,
    val likeState: String? = null
)

@Serializable
data class EngagementToolbarSurfaceEntityPayload(
    val key: String? = null,
    val toolbar: CommentToolbar? = null
)
