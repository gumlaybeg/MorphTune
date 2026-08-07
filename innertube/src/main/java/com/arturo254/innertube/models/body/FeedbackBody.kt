package com.arturo254.innertube.models.body

import com.arturo254.innertube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackBody(
    val context: Context,
    val feedbackTokens: List<String>
)
