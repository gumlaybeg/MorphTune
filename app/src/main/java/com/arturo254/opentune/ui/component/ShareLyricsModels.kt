package com.arturo254.opentune.ui.component

import androidx.compose.ui.graphics.Color

enum class CustomFontStyle { REGULAR, BOLD, EXTRA_BOLD }
enum class CustomTextAlignment { LEFT, CENTER, RIGHT }

fun Color.isDarkColor(): Boolean {
    val luminance = 0.299f * this.red + 0.587f * this.green + 0.114f * this.blue
    return luminance < 0.5f
}
