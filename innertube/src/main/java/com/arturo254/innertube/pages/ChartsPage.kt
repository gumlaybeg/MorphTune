package com.arturo254.innertube.pages

import com.arturo254.innertube.models.YTItem

data class ChartsPage(
    val sections: List<ChartSection>,
    val continuation: String? = null
) {
    data class ChartSection(
        val title: String,
        val items: List<YTItem>,
        val chartType: ChartType
    )
    enum class ChartType { TRENDING, TOP, GENRE, NEW_RELEASES }
}
