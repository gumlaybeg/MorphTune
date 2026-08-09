package com.arturo254.opentune.ui.component

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BlurMaskFilter
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.arturo254.opentune.LocalDatabase
import com.arturo254.opentune.LocalPlayerConnection
import com.arturo254.opentune.R
import com.arturo254.opentune.constants.AnimateLyricsKey
import com.arturo254.opentune.constants.DarkModeKey
import com.arturo254.opentune.constants.LyricsClickKey
import com.arturo254.opentune.constants.LyricsScrollKey
import com.arturo254.opentune.constants.LyricsTextPositionKey
import com.arturo254.opentune.constants.PlayerBackgroundStyle
import com.arturo254.opentune.constants.PlayerBackgroundStyleKey
import com.arturo254.opentune.constants.RomanizeLyricsKey
import com.arturo254.opentune.constants.RotateBackgroundKey
import com.arturo254.opentune.constants.SliderStyle
import com.arturo254.opentune.constants.SliderStyleKey
import com.arturo254.opentune.db.entities.LyricsEntity
import com.arturo254.opentune.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import com.arturo254.opentune.lyrics.LyricsEntry
import com.arturo254.opentune.lyrics.LyricsUtils.findActiveLineIndices
import com.arturo254.opentune.lyrics.LyricsUtils.findCurrentLineIndex
import com.arturo254.opentune.lyrics.LyricsUtils.parseLyrics
import com.arturo254.opentune.lyrics.WordTimestamp
import com.arturo254.opentune.playback.PlayerConnection
import com.arturo254.opentune.ui.menu.LyricsMenu
import com.arturo254.opentune.ui.screens.settings.DarkMode
import com.arturo254.opentune.ui.screens.settings.LyricsPosition
import com.arturo254.opentune.ui.utils.fadingEdge
import com.arturo254.opentune.utils.ComposeToImage
import com.arturo254.opentune.utils.makeTimeString
import com.arturo254.opentune.utils.rememberEnumPreference
import com.arturo254.opentune.utils.rememberPreference
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.saket.squiggles.SquigglySlider
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds

private data class HyphenGroupWord(val pos: Int, val size: Int, val isLast: Boolean, val groupStartMs: Long, val groupEndMs: Long)

private fun String.containsRtl(): Boolean {
    for (c in this) {
        val directionality = Character.getDirectionality(c).toInt()
        if (directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT.toInt() ||
            directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.toInt()
        ) return true
    }
    return false
}

private fun String.toGraphemeClusters(): List<String> {
    if (isEmpty()) return emptyList()
    val result = mutableListOf<String>()
    val it = java.text.BreakIterator.getCharacterInstance()
    it.setText(this)
    var start = it.first()
    var end = it.next()
    while (end != java.text.BreakIterator.DONE) {
        result.add(substring(start, end))
        start = end
        end = it.next()
    }
    return result
}

private fun generateFallbackWords(text: String, startTimeMs: Long): List<WordTimestamp> {
    val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
    val wordDurationSec = 0.18
    val wordStaggerSec = 0.03
    val startTimeSec = startTimeMs / 1000.0
    return words.mapIndexed { idx, wordText ->
        WordTimestamp(
            text = wordText,
            startTime = startTimeSec + (idx * wordStaggerSec),
            endTime = startTimeSec + (idx * wordStaggerSec) + wordDurationSec,
            hasTrailingSpace = idx < words.size - 1
        )
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope", "StringFormatInvalid", "LocalContextGetResourceValueCall")
@Composable
fun Lyrics(
    sliderPositionProvider: () -> Long?,
    onNavigateBack: (() -> Unit)? = null,
    mediaMetadata: com.arturo254.opentune.models.MediaMetadata? = null,
    onBackClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    backgroundAlpha: () -> Float = { 1f }
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val menuState = LocalMenuState.current
    val density = LocalDensity.current
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    val database = LocalDatabase.current

    val isFullscreen = onNavigateBack != null
    val sliderStyle by rememberEnumPreference(SliderStyleKey, SliderStyle.DEFAULT)
    val landscapeOffset = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val lyricsTextPosition by rememberEnumPreference(LyricsTextPositionKey, LyricsPosition.CENTER)
    val changeLyrics by rememberPreference(LyricsClickKey, true)
    val scrollLyrics by rememberPreference(LyricsScrollKey, true)
    val animateLyrics by rememberPreference(AnimateLyricsKey, true)
    val romanizeLyrics by rememberPreference(RomanizeLyricsKey, defaultValue = false)

    val rotateBackground by rememberPreference(RotateBackgroundKey, defaultValue = false)

    val currentMetadata = mediaMetadata ?: playerConnection.mediaMetadata.collectAsState().value
    val currentSongId = currentMetadata?.id

    var currentLineIndex by remember { mutableIntStateOf(-1) }
    var activeLineIndices by remember(currentSongId) { mutableStateOf(setOf<Int>()) }
    var deferredCurrentLineIndex by remember(currentSongId) { mutableIntStateOf(0) }
    var previousLineIndex by remember(currentSongId) { mutableIntStateOf(0) }
    var lastPreviewTime by remember(currentSongId) { mutableLongStateOf(0L) }
    var isSeeking by remember { mutableStateOf(false) }
    var initialScrollDone by remember(currentSongId) { mutableStateOf(false) }
    var shouldScrollToFirstLine by remember(currentSongId) { mutableStateOf(true) }
    var isAppMinimized by rememberSaveable { mutableStateOf(false) }
    var sliderPosition by remember { mutableStateOf<Long?>(null) }
    var cornerRadius by remember { mutableFloatStateOf(16f) }

    var isAutoScrollEnabled by rememberSaveable { mutableStateOf(true) }

    var isSelectionModeActive by remember(currentSongId) { mutableStateOf(false) }
    val selectedIndices = remember(currentSongId) { mutableStateListOf<Int>() }
    var showMaxSelectionToast by remember { mutableStateOf(false) }

    var showProgressDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var shareDialogData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val lazyListState = rememberLazyListState()
    var isAnimating by remember { mutableStateOf(false) }
    val maxSelectionLimit = 5

    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsState()
    val canSkipNext by playerConnection.canSkipNext.collectAsState()

    val dbLyrics by playerConnection.currentLyrics.collectAsState(initial = null)
    var fetchedLyrics by remember { mutableStateOf<String?>(null) }
    var isLoadingLyrics by remember { mutableStateOf(false) }

    LaunchedEffect(currentSongId) {
        if (currentSongId == null) return@LaunchedEffect

        fetchedLyrics = null
        isLoadingLyrics = true

        playerConnection.currentLyrics.collect { currentDbLyrics ->
            if (currentDbLyrics != null && currentDbLyrics.id == currentSongId) {
                fetchedLyrics = currentDbLyrics.lyrics
                isLoadingLyrics = false
            } else {
                withContext(Dispatchers.IO) {
                    try {
                        val entryPoint = EntryPointAccessors.fromApplication(
                            context.applicationContext, 
                            com.arturo254.opentune.di.LyricsHelperEntryPoint::class.java
                        )
                        val fetched = currentMetadata?.let { entryPoint.lyricsHelper().getLyrics(it) }
                        val finalLyrics = if (!fetched.isNullOrBlank()) fetched else LYRICS_NOT_FOUND
                        
                        database.query { upsert(LyricsEntity(currentSongId, finalLyrics)) }
                    } catch (e: Exception) {
                        fetchedLyrics = LYRICS_NOT_FOUND
                        isLoadingLyrics = false
                    }
                }
            }
        }
    }

    val lyrics = fetchedLyrics?.trim()

    val playbackState by playerConnection.playbackState.collectAsState()
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val currentSong by playerConnection.currentSong.collectAsState(initial = null)

    val playerBackground by rememberEnumPreference(PlayerBackgroundStyleKey, defaultValue = PlayerBackgroundStyle.DEFAULT)
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }

    var position by rememberSaveable(playbackState) { mutableLongStateOf(playerConnection.player.currentPosition) }
    var duration by rememberSaveable(playbackState) { mutableLongStateOf(playerConnection.player.duration) }

    val expressiveAccent = when (playerBackground) {
        PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.tertiary
    }
    val textBackgroundColor = when (playerBackground) {
        PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.onBackground
        PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT, PlayerBackgroundStyle.APPLE_MUSIC -> Color.White
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    var gradientColors by remember { mutableStateOf<List<Color>>(emptyList()) }
    val gradientColorsCache = remember { mutableMapOf<String, List<Color>>() }

    LaunchedEffect(currentMetadata?.id, playerBackground) {
        if ((playerBackground == PlayerBackgroundStyle.GRADIENT || playerBackground == PlayerBackgroundStyle.APPLE_MUSIC) && currentMetadata?.thumbnailUrl != null) {
            val cachedColors = gradientColorsCache[currentMetadata.id]
            if (cachedColors != null) {
                gradientColors = cachedColors
                return@LaunchedEffect
            }
            withContext(Dispatchers.IO) {
                try {
                    val fallbackColors = listOf(primaryColor, secondaryColor, tertiaryColor)
                    gradientColorsCache[currentMetadata.id] = fallbackColors
                    withContext(Dispatchers.Main) { gradientColors = fallbackColors }
                } catch (e: Exception) {
                    val fallbackColors = listOf(primaryColor, secondaryColor, tertiaryColor)
                    gradientColorsCache[currentMetadata.id] = fallbackColors
                    withContext(Dispatchers.Main) { gradientColors = fallbackColors }
                }
            }
        } else {
            gradientColors = emptyList()
        }
    }

    val lines = remember(lyrics, scope) {
        if (lyrics == null || lyrics == LYRICS_NOT_FOUND) {
            emptyList()
        } else if (lyrics.startsWith("[")) {
            val parsedLines = parseLyrics(lyrics)
            listOf(LyricsEntry.HEAD_LYRICS_ENTRY) + parsedLines
        } else {
            lyrics.lines().mapIndexed { index, line ->
                LyricsEntry(index * 100L, line)
            }
        }
    }

    val romanizedWordsMap = remember(currentSongId) { mutableStateMapOf<Int, List<WordTimestamp>>() }

    LaunchedEffect(lines, romanizeLyrics) {
        if (romanizeLyrics && lines.isNotEmpty() && lyrics != null && lyrics != LYRICS_NOT_FOUND) {
            val enabledLangs = listOf("Japanese", "Korean", "Chinese", "Hindi", "Ukrainian", "Russian", "Serbian", "Bulgarian", "Belarusian", "Kyrgyz", "Macedonian")
            for (i in lines.indices) {
                val line = lines[i]
                if (line.text.isNotBlank()) {
                    launch(Dispatchers.Default) {
                        val romanized = com.arturo254.opentune.lyrics.LyricsUtils.romanize(
                            text = lyrics,
                            line = line.text,
                            enabledLanguages = enabledLangs,
                            romanizeCyrillicByLine = false
                        )
                        line.romanizedTextFlow.value = romanized

                        if (line.words != null) {
                            val romWords = line.words.map { w ->
                                val rWord = com.arturo254.opentune.lyrics.LyricsUtils.romanize(
                                    text = lyrics,
                                    line = w.text,
                                    enabledLanguages = enabledLangs,
                                    romanizeCyrillicByLine = true
                                ) ?: w.text
                                w.copy(text = rWord)
                            }
                            romanizedWordsMap[i] = romWords
                        }
                    }
                }
            }
        } else if (!romanizeLyrics) {
            for (line in lines) {
                line.romanizedTextFlow.value = null
            }
            romanizedWordsMap.clear()
        }
    }

    LaunchedEffect(lines) {
        isSelectionModeActive = false
        selectedIndices.clear()
        currentLineIndex = -1
        activeLineIndices = emptySet()
        deferredCurrentLineIndex = 0
        previousLineIndex = 0
        initialScrollDone = false
        shouldScrollToFirstLine = true
        isAutoScrollEnabled = true
    }

    val isSynced = remember(lyrics) { !lyrics.isNullOrEmpty() && lyrics.startsWith("[") }

    BackHandler(enabled = isSelectionModeActive || isFullscreen) {
        when {
            isSelectionModeActive -> { isSelectionModeActive = false; selectedIndices.clear() }
            isFullscreen -> onNavigateBack?.invoke()
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput) isAutoScrollEnabled = false
                if (!isSelectionModeActive) lastPreviewTime = System.currentTimeMillis()
                return super.onPostScroll(consumed, available, source)
            }
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                isAutoScrollEnabled = false
                if (!isSelectionModeActive) lastPreviewTime = System.currentTimeMillis()
                return super.onPostFling(consumed, available)
            }
        }
    }

    LaunchedEffect(Unit) { if (isFullscreen) cornerRadius = 16f }

    LaunchedEffect(playbackState) {
        if (isFullscreen && playbackState == Player.STATE_READY) {
            while (isActive) {
                delay(100)
                position = playerConnection.player.currentPosition
                duration = playerConnection.player.duration
            }
        }
    }

    LaunchedEffect(showMaxSelectionToast) {
        if (showMaxSelectionToast) {
            Toast.makeText(context, context.getString(R.string.max_selection_limit, maxSelectionLimit), Toast.LENGTH_SHORT).show()
            showMaxSelectionToast = false
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                val isCurrentLineVisible = lazyListState.layoutInfo.visibleItemsInfo.any { it.index == currentLineIndex }
                if (isCurrentLineVisible) initialScrollDone = false
                isAppMinimized = true
            } else if (event == Lifecycle.Event.ON_START) {
                isAppMinimized = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(lyrics) {
        if (lyrics.isNullOrEmpty() || !lyrics.startsWith("[")) {
            currentLineIndex = -1
            activeLineIndices = emptySet()
            return@LaunchedEffect
        }
        while (isActive) {
            delay(50)
            val sliderPos = sliderPositionProvider()
            isSeeking = sliderPos != null
            val pos = sliderPos ?: playerConnection.player.currentPosition
            currentLineIndex = findCurrentLineIndex(lines, pos)
            activeLineIndices = findActiveLineIndices(lines, pos)
        }
    }

    LaunchedEffect(isSeeking, lastPreviewTime) {
        if (isSeeking) lastPreviewTime = 0L
        else if (lastPreviewTime != 0L) {
            delay(if (isFullscreen) 2.seconds else 2.seconds)
            lastPreviewTime = 0L
        }
    }

    suspend fun performSmoothPageScroll(targetIndex: Int, duration: Int = 1500) {
        if (isAnimating) return
        isAnimating = true
        try {
            val itemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }
            if (itemInfo != null) {
                val viewportHeight = lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
                val center = lazyListState.layoutInfo.viewportStartOffset + (viewportHeight / 2)
                val itemCenter = itemInfo.offset + itemInfo.size / 2
                val offset = itemCenter - center
                if (kotlin.math.abs(offset) > 10) lazyListState.animateScrollBy(value = offset.toFloat(), animationSpec = tween(durationMillis = duration))
            } else lazyListState.scrollToItem(targetIndex)
        } finally { isAnimating = false }
    }

    LaunchedEffect(currentLineIndex, lastPreviewTime, initialScrollDone, isAutoScrollEnabled) {
        if (!isSynced) return@LaunchedEffect
        if (isAutoScrollEnabled) {
            if ((currentLineIndex == 0 && shouldScrollToFirstLine) || !initialScrollDone) {
                shouldScrollToFirstLine = false
                performSmoothPageScroll(kotlin.math.max(0, currentLineIndex), 800)
                if (!isAppMinimized) initialScrollDone = true
            } else if (currentLineIndex != -1) {
                deferredCurrentLineIndex = currentLineIndex
                if (isSeeking) performSmoothPageScroll(kotlin.math.max(0, currentLineIndex - 1), 500)
                else if ((lastPreviewTime == 0L || currentLineIndex != previousLineIndex) && scrollLyrics) {
                    if (currentLineIndex != previousLineIndex) performSmoothPageScroll(currentLineIndex, 1500)
                }
            }
        }
        if (currentLineIndex > 0) shouldScrollToFirstLine = true
        previousLineIndex = currentLineIndex
    }

    Box(modifier = modifier.fillMaxSize().background(if (isFullscreen) MaterialTheme.colorScheme.background else Color.Transparent)) {
        if (isFullscreen) {
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = backgroundAlpha() }) {
                when (playerBackground) {
                    PlayerBackgroundStyle.BLUR -> {
                        currentMetadata?.let {
                            AsyncImage(
                                model = it.thumbnailUrl, contentDescription = null, contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize().blur(if (useDarkTheme) 150.dp else 100.dp)
                            )
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                        }
                    }
                    PlayerBackgroundStyle.GRADIENT -> {
                        if (gradientColors.isNotEmpty()) {
                            val stops = if (gradientColors.size >= 3) arrayOf(0.0f to gradientColors[0], 0.5f to gradientColors[1], 1.0f to gradientColors[2])
                                else arrayOf(0.0f to gradientColors[0], 0.6f to gradientColors[0].copy(alpha = 0.7f), 1.0f to Color.Black)
                            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colorStops = stops)).background(Color.Black.copy(alpha = 0.2f)))
                        }
                    }
                    PlayerBackgroundStyle.APPLE_MUSIC -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (gradientColors.isNotEmpty()) {
                                val c1 = gradientColors[0]; val c2 = gradientColors.getOrElse(1) { c1.copy(alpha = 0.8f) }; val c3 = gradientColors.getOrElse(2) { c1.copy(alpha = 0.6f) }
                                Canvas(modifier = Modifier.fillMaxSize().blur(100.dp)) {
                                    drawRect(Brush.verticalGradient(listOf(c1, c2, c3)))
                                    drawCircle(Brush.radialGradient(listOf(c1, Color.Transparent), Offset(size.width*0.2f, size.height*0.2f), size.width*0.8f), size.width*0.8f, Offset(size.width*0.2f, size.height*0.2f))
                                    drawCircle(Brush.radialGradient(listOf(c2, Color.Transparent), Offset(size.width*0.8f, size.height*0.5f), size.width*0.7f), size.width*0.7f, Offset(size.width*0.8f, size.height*0.5f))
                                    drawCircle(Brush.radialGradient(listOf(c3, Color.Transparent), Offset(size.width*0.3f, size.height*0.8f), size.width*0.9f), size.width*0.9f, Offset(size.width*0.3f, size.height*0.8f))
                                }
                                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)))
                            }
                        }
                    }
                    else -> {}
                }
                if (playerBackground != PlayerBackgroundStyle.DEFAULT) Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(WindowInsets.systemBars.asPaddingValues())) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp), contentAlignment = Alignment.TopCenter) {
                BoxWithConstraints(contentAlignment = Alignment.TopStart, modifier = Modifier.fillMaxSize()) {
                    val topPadding = with(density) { 100.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding() }
                    
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(top = topPadding, bottom = if (isFullscreen) 180.dp else 0.dp, start = 8.dp, end = 8.dp),
                        modifier = Modifier.fadingEdge(vertical = 32.dp).nestedScroll(nestedScrollConnection)
                    ) {
                        val displayedCurrentLineIndex = if (!isAutoScrollEnabled || isSeeking || isSelectionModeActive) deferredCurrentLineIndex else currentLineIndex

                        if (isLoadingLyrics) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                                    contentAlignment = when (lyricsTextPosition) { LyricsPosition.LEFT -> Alignment.CenterStart; LyricsPosition.CENTER -> Alignment.Center; else -> Alignment.CenterEnd }
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        CircularProgressIndicator(modifier = Modifier.size(56.dp), color = expressiveAccent)
                                    }
                                }
                            }
                        } else {
                            itemsIndexed(items = lines, key = { index, item -> "$index-${item.time}" }) { index, item ->
                                val isSelected = selectedIndices.contains(index)
                                val isActiveLine = (index in activeLineIndices || index == displayedCurrentLineIndex) && isSynced
                                val romWords = romanizedWordsMap[index]

                                LyricsLine(
                                    index = index,
                                    item = item,
                                    romWords = romWords,
                                    isSynced = isSynced,
                                    isActiveLine = isActiveLine,
                                    bgVisible = true,
                                    isSelected = isSelected,
                                    isSelectionModeActive = isSelectionModeActive,
                                    currentPositionState = position,
                                    lyricsOffset = 0L,
                                    playerConnection = playerConnection,
                                    lyricsTextSize = 25f,
                                    lyricsLineSpacing = 1.2f,
                                    expressiveAccent = expressiveAccent,
                                    lyricsTextPosition = lyricsTextPosition,
                                    respectAgentPositioning = true,
                                    isAutoScrollEnabled = isAutoScrollEnabled,
                                    displayedCurrentLineIndex = displayedCurrentLineIndex,
                                    romanizeAsMain = false,
                                    enabledLanguages = emptyList(),
                                    romanizeLyrics = romanizeLyrics,
                                    onSizeChanged = { },
                                    onClick = {
                                        if (isSelectionModeActive) {
                                            if (isSelected) {
                                                selectedIndices.remove(index)
                                                if (selectedIndices.isEmpty()) isSelectionModeActive = false
                                            } else {
                                                if (selectedIndices.size < maxSelectionLimit) selectedIndices.add(index) else showMaxSelectionToast = true
                                            }
                                        } else if (isSynced && changeLyrics) {
                                            playerConnection.player.seekTo(item.time)
                                            scope.launch { performSmoothPageScroll(index, 1500) }
                                            lastPreviewTime = 0L
                                        }
                                    },
                                    onLongClick = {
                                        if (!isSelectionModeActive) {
                                            isSelectionModeActive = true
                                            selectedIndices.add(index)
                                        } else if (!isSelected && selectedIndices.size < maxSelectionLimit) {
                                            selectedIndices.add(index)
                                        } else if (!isSelected) showMaxSelectionToast = true
                                    }
                                )
                            }
                        }

                        if (lyrics == LYRICS_NOT_FOUND) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 32.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(painterResource(R.drawable.music_note), null, Modifier.size(32.dp), MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(stringResource(R.string.lyrics_not_found), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                val offsetXAnimatable = remember { Animatable(0f) }
                var dragStartTime by remember { mutableLongStateOf(0L) }
                var totalDragDistance by remember { mutableFloatStateOf(0f) }
                val layoutDirection = LocalLayoutDirection.current

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.weight(1f).pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { dragStartTime = System.currentTimeMillis(); totalDragDistance = 0f },
                            onDragCancel = { scope.launch { offsetXAnimatable.animateTo(0f, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)) } },
                            onHorizontalDrag = { _, dragAmount ->
                                val adjustedDragAmount = if (layoutDirection == LayoutDirection.Rtl) -dragAmount else dragAmount
                                val allowLeft = adjustedDragAmount < 0 && canSkipNext
                                val allowRight = adjustedDragAmount > 0 && canSkipPrevious
                                if (allowLeft || allowRight) {
                                    totalDragDistance += kotlin.math.abs(adjustedDragAmount)
                                    scope.launch { offsetXAnimatable.snapTo(offsetXAnimatable.value + adjustedDragAmount) }
                                }
                            },
                            onDragEnd = {
                                val dragDuration = System.currentTimeMillis() - dragStartTime
                                val velocity = if (dragDuration > 0) totalDragDistance / dragDuration else 0f
                                val currentOffset = offsetXAnimatable.value
                                val shouldChangeSong = (kotlin.math.abs(currentOffset) > 50f && velocity > ((0.73f * -8.25f) + 8.5f)) || (kotlin.math.abs(currentOffset) > 600 / (1f + kotlin.math.exp(-(-11.44748 * 0.73f + 9.04945))).roundToInt())
                                if (shouldChangeSong) {
                                    if (currentOffset > 0 && canSkipPrevious) playerConnection.seekToPrevious()
                                    else if (currentOffset <= 0 && canSkipNext) playerConnection.seekToNext()
                                }
                                scope.launch { offsetXAnimatable.animateTo(0f, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)) }
                            }
                        )
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.offset { IntOffset(offsetXAnimatable.value.roundToInt(), 0) }.fillMaxWidth()) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).clickable { if (playbackState == Player.STATE_ENDED) { playerConnection.player.seekTo(0, 0); playerConnection.player.playWhenReady = true } else { if (isPlaying) playerConnection.player.pause() else playerConnection.player.play() } }) {
                                currentMetadata?.let { AsyncImage(model = it.thumbnailUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
                                val overlayAlpha by androidx.compose.animation.core.animateFloatAsState(if (isPlaying) 0.4f else 0.4f, label = "overlay_alpha")
                                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = overlayAlpha)))
                                androidx.compose.animation.AnimatedVisibility(visible = playbackState == Player.STATE_ENDED || !isPlaying || isPlaying, enter = fadeIn(), exit = fadeOut()) {
                                    Icon(painterResource(if (playbackState == Player.STATE_ENDED) R.drawable.replay else if (isPlaying) R.drawable.pause else R.drawable.play), null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                                currentMetadata?.let {
                                    Text(text = it.title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold), color = textBackgroundColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Spacer(Modifier.height(6.dp))
                                    Text(text = if (it.artists.isNotEmpty()) it.artists.joinToString(", ") { a -> a.name } else stringResource(R.string.unknown), style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp), color = textBackgroundColor.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(32.dp).clickable { playerConnection.toggleLike() }, contentAlignment = Alignment.Center) {
                            Icon(painterResource(if (currentSong?.song?.liked == true) R.drawable.favorite else R.drawable.favorite_border), null, tint = if (currentSong?.song?.liked == true) MaterialTheme.colorScheme.error else textBackgroundColor.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                        }
                        Box(modifier = Modifier.size(32.dp).clickable { currentMetadata?.let { menuState.show { LyricsMenu(lyricsProvider = { dbLyrics }, mediaMetadataProvider = { it }, onDismiss = menuState::dismiss) } } }, contentAlignment = Alignment.Center) {
                            Icon(painterResource(R.drawable.more_horiz), null, tint = textBackgroundColor, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                when (sliderStyle) {
                    SliderStyle.DEFAULT -> Slider((sliderPosition ?: position).toFloat(), onValueChange = { sliderPosition = it.toLong() }, onValueChangeFinished = { sliderPosition?.let { playerConnection.player.seekTo(it); position = it }; sliderPosition = null }, valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()), colors = SliderDefaults.colors(activeTrackColor = textBackgroundColor, inactiveTrackColor = textBackgroundColor.copy(alpha = 0.3f), thumbColor = textBackgroundColor), modifier = Modifier.padding(horizontal = 16.dp))
                    SliderStyle.SQUIGGLY -> SquigglySlider((sliderPosition ?: position).toFloat(), onValueChange = { sliderPosition = it.toLong() }, onValueChangeFinished = { sliderPosition?.let { playerConnection.player.seekTo(it); position = it }; sliderPosition = null }, valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()), colors = SliderDefaults.colors(activeTrackColor = textBackgroundColor, inactiveTrackColor = textBackgroundColor.copy(alpha = 0.3f), thumbColor = textBackgroundColor), modifier = Modifier.padding(horizontal = 16.dp), squigglesSpec = SquigglySlider.SquigglesSpec(amplitude = if (isPlaying) 4.dp else 0.dp, strokeWidth = 3.dp, wavelength = 36.dp))
                    SliderStyle.SLIM -> Slider((sliderPosition ?: position).toFloat(), onValueChange = { sliderPosition = it.toLong() }, onValueChangeFinished = { sliderPosition?.let { playerConnection.player.seekTo(it); position = it }; sliderPosition = null }, valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()), colors = SliderDefaults.colors(activeTrackColor = textBackgroundColor, inactiveTrackColor = textBackgroundColor.copy(alpha = 0.3f)), thumb = { Spacer(Modifier.size(0.dp)) }, modifier = Modifier.padding(horizontal = 16.dp))
                }
                Spacer(Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Text(makeTimeString(sliderPosition ?: position), style = MaterialTheme.typography.labelMedium, color = textBackgroundColor, modifier = Modifier.align(Alignment.CenterStart))
                    Text(if (duration != C.TIME_UNSET) makeTimeString(duration) else "", style = MaterialTheme.typography.labelMedium, color = textBackgroundColor, modifier = Modifier.align(Alignment.CenterEnd))
                }
            }
        }

        AnimatedVisibility(visible = !isAutoScrollEnabled && isSynced, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut(), modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 220.dp)) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), tonalElevation = 4.dp, modifier = Modifier.clickable { scope.launch { performSmoothPageScroll(currentLineIndex, 1500) }; isAutoScrollEnabled = true }.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Icon(painterResource(R.drawable.sync), null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.auto_scroll), color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        if (isFullscreen && isSelectionModeActive) {
            AnimatedVisibility(visible = isSelectionModeActive, enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it }, exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { it }, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 180.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f), tonalElevation = 4.dp, modifier = Modifier.size(56.dp).clickable { isSelectionModeActive = false; selectedIndices.clear() }) {
                        Box(contentAlignment = Alignment.Center) { Icon(painterResource(R.drawable.close), null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(24.dp)) }
                    }
                    if (selectedIndices.isNotEmpty()) {
                        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f), tonalElevation = 4.dp, modifier = Modifier.clickable {
                            val sortedIndices = selectedIndices.sorted()
                            val selectedLyricsText = sortedIndices.mapNotNull { lines.getOrNull(it)?.text }.joinToString("\n")
                            if (selectedLyricsText.isNotBlank()) {
                                shareDialogData = Triple(selectedLyricsText, currentMetadata?.title ?: "", currentMetadata?.artists?.joinToString { it.name } ?: "")
                                showShareDialog = true
                            }
                            isSelectionModeActive = false; selectedIndices.clear()
                        }) {
                            Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(painterResource(R.drawable.media3_icon_share), null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                                Text(stringResource(R.string.share), color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showProgressDialog) {
        BasicAlertDialog(onDismissRequest = { }) {
            Card(shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                Box(Modifier.padding(32.dp)) { Text(stringResource(R.string.generating_image) + "\n" + stringResource(R.string.please_wait), color = MaterialTheme.colorScheme.onSurface) }
            }
        }
    }

    if (showShareDialog && shareDialogData != null) {
        ShareLyricsDialog(
            lyricsText = shareDialogData!!.first,
            songTitle = shareDialogData!!.second,
            artists = shareDialogData!!.third,
            mediaMetadata = currentMetadata,
            onDismiss = { showShareDialog = false; shareDialogData = null }
        )
    }
}

@Composable
internal fun LyricsLine(
    index: Int,
    item: LyricsEntry,
    romWords: List<WordTimestamp>?,
    isSynced: Boolean,
    isActiveLine: Boolean,
    bgVisible: Boolean,
    isSelected: Boolean,
    isSelectionModeActive: Boolean,
    currentPositionState: Long,
    lyricsOffset: Long,
    playerConnection: PlayerConnection,
    lyricsTextSize: Float,
    lyricsLineSpacing: Float,
    expressiveAccent: Color,
    lyricsTextPosition: LyricsPosition,
    respectAgentPositioning: Boolean,
    isAutoScrollEnabled: Boolean,
    displayedCurrentLineIndex: Int,
    romanizeAsMain: Boolean,
    enabledLanguages: List<String>,
    romanizeLyrics: Boolean,
    onSizeChanged: (Int) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val itemModifier = modifier
        .fillMaxWidth()
        .onSizeChanged { onSizeChanged(it.height) }
        .clip(RoundedCornerShape(8.dp))
        .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        .background(if (isSelected && isSelectionModeActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent)
        .padding(
            start = when (lyricsTextPosition) { LyricsPosition.LEFT, LyricsPosition.RIGHT -> 11.dp; LyricsPosition.CENTER -> 24.dp },
            end = when (lyricsTextPosition) { LyricsPosition.LEFT, LyricsPosition.RIGHT -> 11.dp; LyricsPosition.CENTER -> 24.dp },
            top = if (item.isBackground) 0.dp else 12.dp,
            bottom = if (item.isBackground) 2.dp else 12.dp
        )

    val agentAlignment = when {
        respectAgentPositioning && item.agent == "v1" -> Alignment.Start
        respectAgentPositioning && item.agent == "v2" -> Alignment.End
        respectAgentPositioning && item.agent == "v1000" -> Alignment.CenterHorizontally
        item.isBackground -> Alignment.CenterHorizontally
        else -> when (lyricsTextPosition) { LyricsPosition.LEFT -> Alignment.Start; LyricsPosition.CENTER -> Alignment.CenterHorizontally; LyricsPosition.RIGHT -> Alignment.End }
    }
    
    val agentTextAlign = when {
        respectAgentPositioning && item.agent == "v1" -> TextAlign.Left
        respectAgentPositioning && item.agent == "v2" -> TextAlign.Right
        respectAgentPositioning && item.agent == "v1000" -> TextAlign.Center
        item.isBackground -> TextAlign.Center
        else -> when (lyricsTextPosition) { LyricsPosition.LEFT -> TextAlign.Left; LyricsPosition.CENTER -> TextAlign.Center; LyricsPosition.RIGHT -> TextAlign.Right }
    }

    Box(modifier = itemModifier, contentAlignment = when {
        respectAgentPositioning && item.agent == "v1" -> Alignment.CenterStart
        respectAgentPositioning && item.agent == "v2" -> Alignment.CenterEnd
        item.isBackground -> Alignment.Center
        respectAgentPositioning && item.agent == "v1000" -> Alignment.Center
        else -> when (lyricsTextPosition) { LyricsPosition.LEFT -> Alignment.CenterStart; LyricsPosition.RIGHT -> Alignment.CenterEnd; LyricsPosition.CENTER -> Alignment.Center }
    }) {
        @Composable
        fun LyricContent() {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = agentAlignment) {
                val inactiveAlpha = 0.15f
                val activeAlpha = 1f
                val targetAlpha = if (!isSynced || isActiveLine) activeAlpha
                else if (isAutoScrollEnabled && displayedCurrentLineIndex >= 0) {
                    when (abs(index - displayedCurrentLineIndex)) {
                        0 -> 0.5f
                        1 -> 0.35f
                        2 -> 0.25f
                        3 -> 0.18f
                        else -> 0.12f
                    }
                } else inactiveAlpha
                
                val animatedAlpha by animateFloatAsState(targetAlpha, tween(250), label = "lyricsLineAlpha")
                val lineColor = expressiveAccent.copy(alpha = animatedAlpha)
                
                val romanizedTextState by item.romanizedTextFlow.collectAsState()
                val isRomanizedAvailable = romanizedTextState != null
                
                val mainTextRaw = if (romanizeAsMain && isRomanizedAvailable) romanizedTextState else item.text
                val subTextRaw = if (romanizeAsMain && isRomanizedAvailable) item.text else romanizedTextState
                
                val mainText = if (item.isBackground) mainTextRaw?.removePrefix("(")?.removeSuffix(")") else mainTextRaw
                val subText = if (item.isBackground) subTextRaw?.removePrefix("(")?.removeSuffix(")") else subTextRaw

                val mainWords = if (romanizeAsMain && isRomanizedAvailable) romWords else item.words
                val subWords = if (romanizeAsMain && isRomanizedAvailable) item.words else romWords

                val lyricStyle = TextStyle(
                    fontSize = if (item.isBackground) (lyricsTextSize * 0.75f).sp else lyricsTextSize.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = if (item.isBackground) FontStyle.Italic else FontStyle.Normal,
                    lineHeight = if (item.isBackground) (lyricsTextSize * 0.75f * lyricsLineSpacing).sp else (lyricsTextSize * lyricsLineSpacing).sp,
                    letterSpacing = (-0.5).sp,
                    textAlign = agentTextAlign,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.Both)
                )

                val subLyricStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = if (item.isBackground) FontStyle.Italic else FontStyle.Normal,
                    lineHeight = (18f * lyricsLineSpacing).sp,
                    letterSpacing = (-0.5).sp,
                    textAlign = agentTextAlign,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.Both)
                )

                val effectiveMainWords = if (mainWords?.isNotEmpty() == true) {
                    mainWords
                } else if (mainText != null) {
                    remember(mainText, item.time) { generateFallbackWords(mainText, item.time) }
                } else null

                if (isSynced && effectiveMainWords != null && (isActiveLine || abs(index - displayedCurrentLineIndex) <= 3) && mainText != null) {
                    WordLevelLyrics(
                        mainText = mainText, words = effectiveMainWords, isActiveLine = isActiveLine, currentPositionState = currentPositionState, lyricsOffset = lyricsOffset, playerConnection = playerConnection, lyricStyle = lyricStyle, lineColor = lineColor, expressiveAccent = expressiveAccent, isBackground = item.isBackground, focusedAlpha = 0.5f, alignment = agentTextAlign
                    )
                } else {
                    Text(text = mainText ?: "", style = lyricStyle.copy(color = if (isActiveLine) expressiveAccent else lineColor), modifier = Modifier.fillMaxWidth())
                }
                
                if (romanizeLyrics && subText != null) {
                    val effectiveSubWords = if (subWords?.isNotEmpty() == true) {
                        subWords
                    } else {
                        remember(subText, item.time) { generateFallbackWords(subText, item.time) }
                    }

                    if (isSynced && effectiveSubWords != null && (isActiveLine || abs(index - displayedCurrentLineIndex) <= 3)) {
                        WordLevelLyrics(
                            mainText = subText, words = effectiveSubWords, isActiveLine = isActiveLine, currentPositionState = currentPositionState, lyricsOffset = lyricsOffset, playerConnection = playerConnection, lyricStyle = subLyricStyle, lineColor = lineColor.copy(alpha = 0.6f), expressiveAccent = expressiveAccent.copy(alpha = 0.8f), isBackground = item.isBackground, focusedAlpha = 0.6f, alignment = agentTextAlign
                        )
                    } else {
                        Text(text = subText, style = subLyricStyle.copy(color = if (isActiveLine) expressiveAccent.copy(alpha = 0.8f) else lineColor.copy(alpha = 0.6f)), modifier = Modifier.fillMaxWidth().padding(top = 2.dp))
                    }
                }
                val transText by item.translatedTextFlow.collectAsState()
                transText?.let { Text(text = it, fontSize = 16.sp, color = expressiveAccent.copy(alpha = 0.5f), textAlign = agentTextAlign, fontWeight = FontWeight.Normal, modifier = Modifier.padding(top = 4.dp)) }
            }
        }
        if (item.isBackground) { AnimatedVisibility(visible = bgVisible, enter = fadeIn(tween(250, 100)), exit = fadeOut(tween(250))) { LyricContent() } } else LyricContent()
    }
}

@Composable
private fun WordLevelLyrics(
    mainText: String, words: List<WordTimestamp>, isActiveLine: Boolean, currentPositionState: Long, lyricsOffset: Long, playerConnection: PlayerConnection, lyricStyle: TextStyle, lineColor: Color, expressiveAccent: Color, isBackground: Boolean, focusedAlpha: Float, alignment: TextAlign
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val glowPaint = remember { android.graphics.Paint().apply { isAntiAlias = true } }
    var smoothPosition by remember { mutableLongStateOf(currentPositionState + lyricsOffset) }
    
    LaunchedEffect(isActiveLine) {
        if (isActiveLine) {
            var lastPlayerPos = playerConnection.player.currentPosition
            var lastUpdateTime = System.currentTimeMillis()
            while (isActive) {
                withFrameMillis {
                    val now = System.currentTimeMillis()
                    val playerPos = playerConnection.player.currentPosition
                    if (playerPos != lastPlayerPos) { lastPlayerPos = playerPos; lastUpdateTime = now }
                    val elapsed = now - lastUpdateTime
                    smoothPosition = lastPlayerPos + lyricsOffset + (if (playerConnection.player.isPlaying) elapsed else 0)
                }
            }
        }
    }
    
    LaunchedEffect(isActiveLine, currentPositionState) {
        if (!isActiveLine) smoothPosition = currentPositionState + lyricsOffset
    }

    val (effectiveWords, effectiveToOriginalIdx) = remember(words, isBackground) {
        words.flatMapIndexed { originalIdx, word ->
            val shouldSplit = word.text.contains('-') && word.text.length > 1 && (!word.hasTrailingSpace || words.size == 1)
            if (shouldSplit) {
                val segments = mutableListOf<String>()
                var start = 0
                for (i in 0 until word.text.length) {
                    if (word.text[i] == '-') { segments.add(word.text.substring(start, i + 1)); start = i + 1 }
                }
                if (start < word.text.length) segments.add(word.text.substring(start))
                if (segments.size > 1) {
                    val totalDuration = word.endTime - word.startTime
                    val segmentDuration = totalDuration / segments.size
                    segments.mapIndexed { index, segmentText -> WordTimestamp(text = segmentText, startTime = word.startTime + index * segmentDuration, endTime = word.startTime + (index + 1) * segmentDuration, hasTrailingSpace = if (index == segments.size - 1) word.hasTrailingSpace else false) to originalIdx }
                } else listOf(word to originalIdx)
            } else listOf(word to originalIdx)
        }.let { data -> data.map { it.first } to data.map { it.second } }
    }

    val graphemeClusters = remember(mainText) { mainText.toGraphemeClusters() }
    val clusterCount = graphemeClusters.size
    val clusterCharOffsets = remember(mainText) {
        IntArray(clusterCount).also { offsets ->
            var charOffset = 0
            graphemeClusters.forEachIndexed { i, cluster -> offsets[i] = charOffset; charOffset += cluster.length }
        }
    }

    val charToWordData = remember(mainText, effectiveWords, isBackground, graphemeClusters, clusterCharOffsets) {
        val wordIdxMap = IntArray(clusterCount) { -1 }
        val charInWordMap = IntArray(clusterCount)
        val wordLenMap = IntArray(clusterCount) { 1 }
        var currentPos = 0; var clCursor = 0
        effectiveWords.forEachIndexed { wordIdx, word ->
            val rawWordText = word.text.let { if (isBackground) { var t = it; if (wordIdx == 0) t = t.removePrefix("("); if (wordIdx == effectiveWords.size - 1) t = t.removeSuffix(")"); t } else it }
            val indexInMain = mainText.indexOf(rawWordText, currentPos)
            if (indexInMain != -1) {
                val wordEndInMain = indexInMain + rawWordText.length
                while (clCursor < clusterCount && clusterCharOffsets[clCursor] < indexInMain) clCursor++
                val wordClusterIndices = mutableListOf<Int>()
                while (clCursor < clusterCount && clusterCharOffsets[clCursor] < wordEndInMain) { wordClusterIndices.add(clCursor); clCursor++ }
                val wordClusterLen = wordClusterIndices.size
                wordClusterIndices.forEachIndexed { posInWord, clIdx -> wordIdxMap[clIdx] = wordIdx; charInWordMap[clIdx] = posInWord; wordLenMap[clIdx] = wordClusterLen }
                if (clCursor < clusterCount && clusterCharOffsets[clCursor] == wordEndInMain && wordEndInMain < mainText.length && mainText[wordEndInMain] == ' ') {
                    val spaceClIdx = clCursor; wordIdxMap[spaceClIdx] = wordIdx; charInWordMap[spaceClIdx] = wordClusterLen; wordLenMap[spaceClIdx] = wordClusterLen + 1; clCursor++
                }
                currentPos = wordEndInMain
            }
        }
        Triple(wordIdxMap, charInWordMap, wordLenMap)
    }

    val hyphenGroupData = remember(effectiveWords) {
        val map = mutableMapOf<Int, HyphenGroupWord>()
        var currentGroup = mutableListOf<Int>()
        effectiveWords.forEachIndexed { wordIdx, word ->
            currentGroup.add(wordIdx)
            if (!word.text.endsWith("-")) {
                if (currentGroup.size > 1) {
                    val groupSize = currentGroup.size
                    val groupStartMs = (effectiveWords[currentGroup.first()].startTime * 1000).toLong()
                    val groupEndMs = (word.endTime * 1000).toLong()
                    currentGroup.forEachIndexed { pos, idx -> map[idx] = HyphenGroupWord(pos, groupSize, pos == groupSize - 1, groupStartMs, groupEndMs) }
                }
                currentGroup = mutableListOf()
            }
        }
        map
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val maxWidthPx = constraints.maxWidth
        val layoutResult = remember(mainText, maxWidthPx, lyricStyle) { textMeasurer.measure(text = mainText, style = lyricStyle, constraints = Constraints(minWidth = maxWidthPx, maxWidth = maxWidthPx), softWrap = true) }
        val letterLayouts = remember(mainText, lyricStyle) { graphemeClusters.map { cluster -> textMeasurer.measure(cluster, lyricStyle) } }
        val isRtlText = remember(mainText) { mainText.containsRtl() }
        
        Canvas(modifier = Modifier.fillMaxWidth().height(with(density) { layoutResult.size.height.toDp() }).graphicsLayer(clip = false, compositingStrategy = CompositingStrategy.Offscreen)) {
            if (mainText.isEmpty()) return@Canvas
            if (!isActiveLine) { drawText(layoutResult, color = lineColor) } else {
                if (isRtlText) {
                    val (wordIdxMap, _, _) = charToWordData
                    val wordFactors = effectiveWords.map { word ->
                        val wStartMs = (word.startTime * 1000).toLong(); val wEndMs = (word.endTime * 1000).toLong()
                        val isWordSung = smoothPosition > wEndMs; val isWordActive = smoothPosition in wStartMs..wEndMs
                        val sungFactor = if (isWordSung) 1f else if (isWordActive) ((smoothPosition - wStartMs).toFloat() / (wEndMs - wStartMs).coerceAtLeast(1)).coerceIn(0f, 1f) else 0f
                        Triple(sungFactor, isWordSung, isWordActive)
                    }
                    drawText(layoutResult, color = lineColor.copy(alpha = focusedAlpha))
                    effectiveWords.indices.forEach { wIdx ->
                        val (sungFactor, isWordSung, isWordActive) = wordFactors[wIdx]
                        var left = Float.MAX_VALUE; var right = Float.MIN_VALUE; var top = Float.MAX_VALUE; var bottom = Float.MIN_VALUE; var found = false
                        for (i in 0 until clusterCount) {
                            if (wordIdxMap[i] == wIdx) {
                                val charOffset = clusterCharOffsets[i]
                                val bounds = layoutResult.getBoundingBox(charOffset)
                                left = min(left, bounds.left); right = kotlin.math.max(right, bounds.right); top = min(top, bounds.top); bottom = kotlin.math.max(bottom, bounds.bottom); found = true
                            }
                        }
                        if (found) {
                            if (isWordSung) { clipRect(left = left, top = top, right = right, bottom = bottom) { drawText(layoutResult, color = expressiveAccent) } }
                            else if (isWordActive && sungFactor > 0f) { clipRect(left = left, top = top, right = right, bottom = bottom) { drawText(layoutResult, color = expressiveAccent.copy(alpha = focusedAlpha + (1f - focusedAlpha) * sungFactor)) } }
                        }
                    }
                    return@Canvas
                }

                val (wordIdxMap, charInWordMap, wordLenMap) = charToWordData
                val wordFactors = effectiveWords.map { word ->
                    val wStartMs = (word.startTime * 1000).toLong(); val wEndMs = (word.endTime * 1000).toLong()
                    val isWordSung = smoothPosition > wEndMs; val isWordActive = smoothPosition in wStartMs..wEndMs
                    val sungFactor = if (isWordSung) 1f else if (isWordActive) ((smoothPosition - wStartMs).toFloat() / (wEndMs - wStartMs).coerceAtLeast(1)).coerceIn(0f, 1f) else 0f
                    Triple(sungFactor, word, isWordSung)
                }

                val wordWobbles = FloatArray(words.size)
                words.forEachIndexed { wordIdx, word ->
                    val startMs = (word.startTime * 1000).toLong()
                    val timeSinceStart = (smoothPosition - startMs).toFloat()
                    val wobble = if (timeSinceStart in 0f..750f) { if (timeSinceStart < 125f) timeSinceStart / 125f else (1f - (timeSinceStart - 125f) / 625f).coerceAtLeast(0f) } else 0f
                    wordWobbles[wordIdx] = wobble
                }

                val lineCurrentPushes = FloatArray(layoutResult.lineCount)
                val lineTotalPushes = FloatArray(layoutResult.lineCount)
                
                for (i in 0 until clusterCount) {
                    val charOffset = clusterCharOffsets[i]
                    val lineIdx = layoutResult.getLineForOffset(charOffset)
                    val wordIdx = wordIdxMap[i]
                    val originalWordIdx = if (wordIdx != -1) effectiveToOriginalIdx[wordIdx] else -1
                    val (sungFactor, wordItem, isWordSung) = if (wordIdx != -1) wordFactors[wordIdx] else Triple(0f, null, false)
                    val wobble = if (originalWordIdx != -1) wordWobbles[originalWordIdx] else 0f
                    
                    var crescendoDeltaX = 0f
                    val groupWord = if (wordIdx != -1) hyphenGroupData[wordIdx] else null
                    if (groupWord != null) {
                        val p = sungFactor; val timeSinceEnd = (smoothPosition - groupWord.groupEndMs).toFloat(); val pOut = (timeSinceEnd / 600f).coerceIn(0f, 1f)
                        val peakScale = 0.06f; val decay = 2.5f; val freq = 10.0f; val baseScalePerSegment = 0.012f
                        if (pOut > 0f) {
                            val baseAtEnd = groupWord.pos * baseScalePerSegment; val totalAtEnd = baseAtEnd + peakScale
                            crescendoDeltaX = totalAtEnd * exp(-decay * pOut) * cos(freq * pOut * PI.toFloat()) * (1f - pOut)
                        } else if (groupWord.isLast) {
                            val base = groupWord.pos * baseScalePerSegment; val springPart = peakScale * (1f - exp(-decay * p) * cos(freq * p * PI.toFloat()) * (1f - p))
                            crescendoDeltaX = base + springPart
                        } else {
                            val boost = if (p > 0f) 0.02f * (1f - p) else 0f
                            crescendoDeltaX = (groupWord.pos * baseScalePerSegment) + boost
                        }
                    }
                    val charLp = if (wordItem != null) { val sMs = wordItem.startTime * 1000; val dur = (wordItem.endTime * 1000 - wordItem.startTime * 1000).coerceAtLeast(100.0); val wProg = (smoothPosition.toDouble() - sMs) / dur; val cInW = charInWordMap[i].toDouble(); val wLen = wordLenMap[i].toDouble(); ((wProg - cInW / wLen) * wLen).coerceIn(0.0, 1.0).toFloat() } else 0f
                    val nudgeScale = if (wordItem != null && !isWordSung && sungFactor > 0f) 0.038f * sin(charLp * PI.toFloat()) * exp(-3f * charLp) else 0f
                    val charScaleX = 1f + (wobble * 0.025f) + crescendoDeltaX + (nudgeScale * 0.3f)
                    val charBounds = layoutResult.getBoundingBox(charOffset)
                    lineTotalPushes[lineIdx] += charBounds.width * (charScaleX - 1f)
                }

                for (i in 0 until clusterCount) {
                    val charOffset = clusterCharOffsets[i]
                    val lineIdx = layoutResult.getLineForOffset(charOffset)
                    val charBounds = layoutResult.getBoundingBox(charOffset)
                    val wordIdx = wordIdxMap[i]
                    val originalWordIdx = if (wordIdx != -1) effectiveToOriginalIdx[wordIdx] else -1
                    val alignShift = when(alignment) { TextAlign.Center -> -lineTotalPushes[lineIdx] / 2f; TextAlign.Right -> -lineTotalPushes[lineIdx]; else -> 0f }
                    val (sungFactor, wordItem, isWordSung) = if (wordIdx != -1) wordFactors[wordIdx] else Triple(0f, null, false)
                    val wobble = if (originalWordIdx != -1) wordWobbles[originalWordIdx] else 0f
                    val wobbleX = wobble * 0.025f; val wobbleY = wobble * 0.015f
                    val charLp = if (wordItem != null) { val sMs = wordItem.startTime * 1000; val dur = (wordItem.endTime * 1000 - wordItem.startTime * 1000).coerceAtLeast(100.0); val wProg = (smoothPosition.toDouble() - sMs) / dur; val cInW = charInWordMap[i].toDouble(); val wLen = wordLenMap[i].toDouble(); ((wProg - cInW / wLen) * wLen).coerceIn(0.0, 1.0).toFloat() } else 0f
                    val shouldGlow = wordItem != null && !isWordSung && sungFactor > 0.001f
                    var crescendoDeltaX = 0f; var crescendoDeltaY = 0f
                    val groupWord = if (wordIdx != -1) hyphenGroupData[wordIdx] else null
                    if (groupWord != null) {
                        val p = sungFactor; val timeSinceEnd = (smoothPosition - groupWord.groupEndMs).toFloat(); val pOut = (timeSinceEnd / 600f).coerceIn(0f, 1f)
                        val peakScale = 0.06f; val decay = 3.5f; val freq = 5.0f; val baseScalePerSegment = 0.012f
                        if (pOut > 0f) {
                            val baseAtEnd = groupWord.pos * baseScalePerSegment; val totalAtEnd = baseAtEnd + peakScale
                            val springOut = totalAtEnd * exp(-decay * pOut) * cos(freq * pOut * PI.toFloat()) * (1f - pOut)
                            crescendoDeltaX = springOut; crescendoDeltaY = springOut
                        } else if (groupWord.isLast) {
                            val base = groupWord.pos * baseScalePerSegment; val springPart = peakScale * (1f - exp(-decay * p) * cos(freq * p * PI.toFloat()) * (1f - p))
                            crescendoDeltaX = base + springPart; crescendoDeltaY = base + springPart
                        } else {
                            val boost = if (p > 0f) 0.02f * (1f - p) else 0f; val base = (groupWord.pos * baseScalePerSegment) + boost
                            crescendoDeltaX = base; crescendoDeltaY = base
                        }
                    }
                    val nudgeScale = if (wordItem != null && !isWordSung && sungFactor > 0f) 0.038f * sin(charLp * PI.toFloat()) * exp(-3f * charLp) else 0f
                    val charScaleX = 1f + wobbleX + crescendoDeltaX + nudgeScale * 0.3f
                    val charScaleY = 1f + wobbleY + crescendoDeltaY + nudgeScale

                    withTransform({
                        var waveOffset = 0f
                        if (groupWord != null) {
                            val wallTime = System.currentTimeMillis(); val adjSmoothPos = smoothPosition
                            val timeInGroup = (adjSmoothPos - groupWord.groupStartMs).toFloat()
                            val timeToGroupEnd = (groupWord.groupEndMs - adjSmoothPos).toFloat()
                            val waveFade = (timeInGroup / 200f).coerceIn(0f, 1f) * (timeToGroupEnd / 200f).coerceIn(0f, 1f)
                            if (waveFade > 0.01f) {
                                val waveSpeed = 0.006f; val waveHeight = 3.24f; val phaseOffset = i * 0.4f
                                waveOffset = sin(wallTime * waveSpeed + phaseOffset) * waveHeight * waveFade
                            }
                        }
                        translate(left = alignShift + lineCurrentPushes[lineIdx] + charBounds.left, top = charBounds.top + waveOffset)
                        if (wordIdx != -1) scale(charScaleX, charScaleY, pivot = Offset(charBounds.width / 2f, charBounds.height))
                    }) {
                        if (shouldGlow) {
                            val sMs = wordItem.startTime * 1000; val eMs = wordItem.endTime * 1000; val dur = eMs - sMs
                            val wordLenText = wordItem.text.length.coerceAtLeast(1)
                            val impactRatio = dur.toFloat() / wordLenText
                            val fadeFactor = (sungFactor * 5f).coerceIn(0f, 1f) * ((1f - sungFactor) * 8f).coerceIn(0f, 1f)
                            val impactFactor = (((impactRatio - 100f) / 250f).coerceIn(0f, 1f) * 0.6f + ((dur.toFloat() - 300f) / 1500f).coerceIn(0f, 1f) * 0.4f).coerceIn(0f, 1f) * fadeFactor
                            if (impactFactor > 0.01f) {
                                val glowAlpha = (0.35f * impactFactor).coerceIn(0f, 0.4f)
                                val baseGlowRadius = 12.dp.toPx() * impactFactor                                                                                    
                                drawIntoCanvas { canvas ->
                                    glowPaint.maskFilter = BlurMaskFilter(baseGlowRadius, BlurMaskFilter.Blur.NORMAL)
                                    glowPaint.color = expressiveAccent.copy(alpha = glowAlpha).toArgb()
                                    glowPaint.textSize = lyricStyle.fontSize.toPx()
                                    glowPaint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                                    canvas.nativeCanvas.drawText(letterLayouts[i].layoutInput.text.text, 0f, letterLayouts[i].firstBaseline, glowPaint)
                                }
                            }
                        }
                        val baseAlpha = if (isWordSung || charLp > 0.99f) 1f else (focusedAlpha + (1f - focusedAlpha) * sungFactor)
                        drawText(letterLayouts[i], color = expressiveAccent.copy(alpha = if (wordIdx == -1) focusedAlpha else baseAlpha))
                        if (!isWordSung && charLp > 0f && charLp < 1f) {
                            val fXL = charBounds.width * charLp
                            val eW = (charBounds.width * 0.45f).coerceAtLeast(1f)
                            val sWL = (fXL - eW).coerceAtLeast(0f)
                            if (sWL > 0f) clipRect(left = 0f, top = 0f, right = sWL, bottom = charBounds.height) { drawText(letterLayouts[i], color = expressiveAccent) }
                            for (j in 0 until 12) {
                                val start = sWL + (j * eW / 12f)
                                val end = (sWL + ((j + 1) * eW / 12f) + 0.5f).coerceAtMost(fXL)
                                if (end > start) clipRect(left = start, top = 0f, right = end, bottom = charBounds.height) { drawText(letterLayouts[i], color = expressiveAccent.copy(alpha = 1f - (j + 0.5f) / 12f)) }
                            }
                        }
                    }
                    lineCurrentPushes[lineIdx] += charBounds.width * (charScaleX - 1f)
                }
            }
        }
    }
}
