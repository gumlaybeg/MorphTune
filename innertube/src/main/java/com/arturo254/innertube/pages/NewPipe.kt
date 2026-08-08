package com.arturo254.innertube.pages

import com.arturo254.innertube.YouTubeExtractor
import com.arturo254.innertube.models.response.PlayerResponse

object NewPipeExtractor {

    fun getSignatureTimestamp(videoId: String): Result<Int> {
        return runCatching {
            YouTubeExtractor.getSignatureTimestamp()
        }
    }

    fun getStreamUrl(
        format: PlayerResponse.StreamingData.Format,
        videoId: String
    ): String? {
        val signatureCipher = format.signatureCipher
        return if (!signatureCipher.isNullOrEmpty()) {
            YouTubeExtractor.decryptUrl(signatureCipher)
        } else if (!format.url.isNullOrEmpty()) {
            YouTubeExtractor.deobfuscateUrlNParam(format.url)
        } else {
            null
        }
    }

    fun newPipePlayer(videoId: String): List<Pair<Int, String>> {
        return emptyList()
    }
}
