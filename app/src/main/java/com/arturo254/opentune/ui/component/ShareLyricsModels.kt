package com.arturo254.opentune.ui.component

import androidx.compose.ui.graphics.Color

enum class LyricsBackgroundStyle { SOLID, BLUR, GRADIENT, APPLE_MUSIC }
enum class CustomFontStyle { REGULAR, BOLD, EXTRA_BOLD }
enum class LogoPosition { BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT, NONE }
enum class BackgroundStyle { SOLID, GRADIENT, PATTERN }
enum class CustomTextAlignment { LEFT, CENTER, RIGHT }
enum class LogoSize { SMALL, MEDIUM, LARGE }
enum class CoverArtStyle { ROUNDED, CIRCLE, SQUARE }
enum class LyricsStyle { NORMAL, ITALIC, CONDENSED }

data class ImageCustomization(
    val backgroundColor: Color = Color(0xFF1A1A1A),
    val textColor: Color = Color.White,
    val secondaryTextColor: Color = Color.White.copy(alpha = 0.7f),
    val backgroundStyle: BackgroundStyle = BackgroundStyle.SOLID,
    val gradientColors: List<Color>? = null,
    val fontStyle: CustomFontStyle = CustomFontStyle.EXTRA_BOLD,
    val showCoverArt: Boolean = true,
    val showSongTitle: Boolean = true,
    val showArtistName: Boolean = true,
    val showLogo: Boolean = true,
    val logoPosition: LogoPosition = LogoPosition.BOTTOM_RIGHT,
    val logoSize: LogoSize = LogoSize.MEDIUM,
    val patternOpacity: Float = 0.05f,
    val cornerRadius: Float = 16f,
    val isDark: Boolean = true,
    val textAlignment: CustomTextAlignment = CustomTextAlignment.CENTER,
    val padding: Float = 24f,
    val textShadowEnabled: Boolean = true,
    val borderEnabled: Boolean = false,
    val borderColor: Color = Color.White.copy(alpha = 0.3f),
    val borderWidth: Float = 2f,
    val coverArtStyle: CoverArtStyle = CoverArtStyle.ROUNDED,
    val lyricsStyle: LyricsStyle = LyricsStyle.NORMAL,
    val accentColor: Color? = null,
    val showAccentLine: Boolean = false,
    val spacingBetweenElements: Float = 16f,
    val lyricsLineSpacing: Float = 1.3f
)

data class ColorPreset(val name: String, val customization: ImageCustomization)

val colorPresets = listOf(
    ColorPreset("Dark", ImageCustomization(backgroundColor = Color(0xFF1A1A1A), textColor = Color.White, secondaryTextColor = Color.White.copy(alpha = 0.7f), isDark = true)),
    ColorPreset("Light", ImageCustomization(backgroundColor = Color(0xFFF5F5F5), textColor = Color.Black, secondaryTextColor = Color.Black.copy(alpha = 0.7f), isDark = false)),
    ColorPreset("Blue", ImageCustomization(backgroundColor = Color(0xFF1E3A8A), textColor = Color.White, secondaryTextColor = Color.White.copy(alpha = 0.8f), isDark = true)),
    ColorPreset("Purple", ImageCustomization(backgroundColor = Color(0xFF4C1D95), textColor = Color.White, secondaryTextColor = Color.White.copy(alpha = 0.8f), isDark = true)),
    ColorPreset("Red", ImageCustomization(backgroundColor = Color(0xFF991B1B), textColor = Color.White, secondaryTextColor = Color.White.copy(alpha = 0.8f), isDark = true)),
    ColorPreset("Green", ImageCustomization(backgroundColor = Color(0xFF065F46), textColor = Color.White, secondaryTextColor = Color.White.copy(alpha = 0.8f), isDark = true)),
    ColorPreset("Gradient Blue", ImageCustomization(backgroundStyle = BackgroundStyle.GRADIENT, gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6), Color(0xFF60A5FA)), textColor = Color.White, secondaryTextColor = Color.White.copy(alpha = 0.9f), isDark = true)),
    ColorPreset("Gradient Purple", ImageCustomization(backgroundStyle = BackgroundStyle.GRADIENT, gradientColors = listOf(Color(0xFF4C1D95), Color(0xFF7C3AED), Color(0xFFA78BFA)), textColor = Color.White, secondaryTextColor = Color.White.copy(alpha = 0.9f), isDark = true)),
    ColorPreset("Gradient Sunset", ImageCustomization(backgroundStyle = BackgroundStyle.GRADIENT, gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6)), textColor = Color.White, secondaryTextColor = Color.White.copy(alpha = 0.9f), isDark = true))
)

fun Color.isDarkColor(): Boolean {
    val luminance = 0.299f * this.red + 0.587f * this.green + 0.114f * this.blue
    return luminance < 0.5f
}
