@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.arturo254.opentune.ui.screens.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.arturo254.innertube.utils.parseCookieString
import com.arturo254.opentune.BuildConfig
import com.arturo254.opentune.LocalPlayerAwareWindowInsets
import com.arturo254.opentune.R
import com.arturo254.opentune.constants.AccountEmailKey
import com.arturo254.opentune.constants.AccountNameKey
import com.arturo254.opentune.constants.InnerTubeCookieKey
import com.arturo254.opentune.ui.component.IconButton
import com.arturo254.opentune.ui.component.TopSearch
import com.arturo254.opentune.ui.utils.backToMain
import com.arturo254.opentune.utils.rememberPreference
import com.arturo254.opentune.viewmodels.HomeViewModel
import com.arturo254.opentune.viewmodels.NewReleaseViewModel

val LocalAnimationsDisabled = compositionLocalOf { false }

// --- DIMENSIONS & ANIMATIONS ---

object SettingsDimensions {
    val GroupCardCornerRadius = 16.dp
    val QuickActionCardCornerRadius = 20.dp
    val IntegrationPillCornerRadius = 14.dp
    val BannerCardCornerRadius = 20.dp
    val HeroCardCornerRadius = 24.dp
    val RowIconCornerRadius = 12.dp

    val ScreenHorizontalPadding = 16.dp
    val SectionSpacing = 14.dp
    val RowVerticalPadding = 14.dp
    val RowHorizontalPadding = 16.dp

    val RowIconSize = 36.dp
    val RowIconInnerSize = 20.dp
    val QuickActionIconSize = 40.dp
    val QuickActionIconInnerSize = 22.dp
    val HeroIconSize = 56.dp
    val HeroIconInnerSize = 30.dp
    val IntegrationIconSize = 28.dp
    val IntegrationIconInnerSize = 16.dp
    val BannerIconSize = 44.dp
    val BannerIconInnerSize = 22.dp
    val ChevronSize = 18.dp

    val DividerThickness = 0.5.dp
    val DividerStartIndent = 60.dp

    val SectionHeaderBottomPadding = 6.dp
    val SectionHeaderHorizontalPadding = 20.dp

    val QuickActionTileAspectRatio = 1.4f

    val CompactColumns = 2
    val MediumColumns = 4
    val ExpandedColumns = 4

    val MediumPaneLeftWeight = 0.42f
    val MediumPaneRightWeight = 0.58f
    val ExpandedListPaneWidth = 380.dp
}

object SettingsAnimations {
    val PressScale = 0.97f
    val TilePressScale = 0.94f
    val PillPressScale = 0.95f
    val IconPressRotation = 5f
    val PillPressLift = (-2).dp

    val EntranceSlideDuration = 350
    val StaggerDelayPerItem = 80
    val ExitFadeDuration = 200

    @Composable
    fun <T> pressSpring(): FiniteAnimationSpec<T> =
        if (LocalAnimationsDisabled.current) snap()
        else spring(stiffness = Spring.StiffnessMedium)

    @Composable
    fun <T> entranceSpring(): FiniteAnimationSpec<T> =
        if (LocalAnimationsDisabled.current) snap()
        else spring(stiffness = Spring.StiffnessLow, dampingRatio = 0.85f)

    @Composable
    fun <T> exitTween(): FiniteAnimationSpec<T> =
        if (LocalAnimationsDisabled.current) snap()
        else tween(durationMillis = ExitFadeDuration)

    @Composable
    fun <T> fadeTween(durationMillis: Int): FiniteAnimationSpec<T> =
        if (LocalAnimationsDisabled.current) snap()
        else tween(durationMillis = durationMillis)

    @Composable
    fun <T>收藏staggerTween(index: Int): FiniteAnimationSpec<T> =
        if (LocalAnimationsDisabled.current) snap()
        else tween(durationMillis = EntranceSlideDuration, delayMillis = index * StaggerDelayPerItem)
}

// --- MODELS ---

data class SettingsQuickAction(
    val icon: Painter,
    val label: String,
    val onClick: () -> Unit,
    val accentColor: Color,
)

data class SettingsGroup(
    val title: String,
    val items: List<SettingsItem>,
)

data class SettingsItem(
    val icon: Painter,
    val title: String,
    val subtitle: String? = null,
    val badge: String? = null,
    val showUpdateIndicator: Boolean = false,
    val accentColor: Color = Color.Unspecified,
    val keywords: List<String> = emptyList(),
    val onClick: () -> Unit,
)

data class SettingsIntegrationAction(
    val icon: Painter,
    val label: String,
    val onClick: () -> Unit,
    val accentColor: Color,
)

// --- MAIN SCREEN ---

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    latestVersion: Long,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val animationsDisabled = LocalAnimationsDisabled.current
    val isAndroid12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val listState = rememberLazyListState()
    val viewModel: HomeViewModel = hiltViewModel()

    val newReleaseViewModel: NewReleaseViewModel = hiltViewModel()
    val hasUnreadNews by newReleaseViewModel.hasNewReleases.collectAsState()

    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) { "SAPISID" in parseCookieString(innerTubeCookie) }
    val isLoading = false

    val accountName by rememberPreference(AccountNameKey, "")
    val accountImageUrl by viewModel.accountImageUrl.collectAsState()
    val (accountEmail, _) = rememberPreference(AccountEmailKey, "")

    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(TextFieldValue()) }
    val focusRequester = remember { FocusRequester() }

    var showUpdateDialog by remember { mutableStateOf(false) }
    var showBetaUpdateDialog by remember { mutableStateOf(false) }

    var hasUpdate by remember { mutableStateOf(false) }
    var fetchedLatestVersion by remember { mutableStateOf(BuildConfig.VERSION_NAME) }

    var hasBetaUpdate by remember { mutableStateOf(false) }
    var fetchedLatestBetaVersion by remember { mutableStateOf(BuildConfig.VERSION_NAME) }

    var showTogetherScreen by remember { mutableStateOf(false) }

    if (showTogetherScreen) {
        MusicTogetherScreen(
            navController = navController,
            scrollBehavior = scrollBehavior,
            onBack = { showTogetherScreen = false }
        )
        return
    }

    // Search History State
    val prefs = context.getSharedPreferences("settings_search_history", Context.MODE_PRIVATE)
    var searchHistory by remember {
        mutableStateOf(prefs.getStringSet("history", emptySet())?.toList() ?: emptyList())
    }

    fun saveSearch(q: String) {
        if (q.isBlank()) return
        val newHistory = (listOf(q) + searchHistory).distinct().take(10)
        searchHistory = newHistory
        prefs.edit().putStringSet("history", newHistory.toSet()).apply()
    }

    fun clearHistory() {
        searchHistory = emptyList()
        prefs.edit().putStringSet("history", emptySet()).apply()
    }

    fun removeHistoryItem(q: String) {
        val newHistory = searchHistory.filter { it != q }
        searchHistory = newHistory
        prefs.edit().putStringSet("history", newHistory.toSet()).apply()
    }

    LaunchedEffect(Unit) {
        val newVersion = checkForUpdates()
        if (newVersion != null && isNewerVersion(newVersion, BuildConfig.VERSION_NAME)) {
            hasUpdate = true
            fetchedLatestVersion = newVersion
        }
        
        val newBetaVersion = checkForBetaUpdates()
        if (newBetaVersion != null && isNewerVersion(newBetaVersion, BuildConfig.VERSION_NAME)) {
            if (newVersion == null || isNewerVersion(newBetaVersion, newVersion)) {
                hasBetaUpdate = true
                fetchedLatestBetaVersion = newBetaVersion
            }
        }
    }

    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
        }
    }

    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        null
    }

    var isStorageGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, storagePermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isNotificationGranted by remember {
        mutableStateOf(
            notificationPermission == null ||
                ContextCompat.checkSelfPermission(context, notificationPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        isStorageGranted = ContextCompat.checkSelfPermission(context, storagePermission) == PackageManager.PERMISSION_GRANTED
        if (notificationPermission != null) {
            isNotificationGranted = ContextCompat.checkSelfPermission(context, notificationPermission) == PackageManager.PERMISSION_GRANTED
        }
    }

    val shouldShowPermissionHint = if (notificationPermission != null) {
        !isNotificationGranted
    } else {
        !isStorageGranted
    }

    val settingsPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var hasRequestedPermissions by remember {
        mutableStateOf(settingsPrefs.getBoolean("has_requested_permissions", false))
    }

    val resetSearch: () -> Unit = {
        isSearching = false
        query = TextFieldValue()
        focusManager.clearFocus()
    }

    val quickActions = buildQuickActions(navController, resetSearch)
    val integrationActions = buildIntegrationActions(navController, resetSearch) { showTogetherScreen = true }
    val settingsGroups = buildSettingsGroups(navController, resetSearch, onChangelogClick = { navController.navigate("settings/changelog") }, hasUnreadNews)
    val internalItems = buildInternalItems(navController, resetSearch)

    val queryText = query.text.trim()
    val showSearchBar = isSearching || queryText.isNotBlank()

    val searchResultsTitle = stringResource(R.string.search_results)

    val filteredQuickActions = if (queryText.isBlank()) emptyList() else filterQuickActions(quickActions, queryText)
    val filteredIntegrations = if (queryText.isBlank()) emptyList() else filterIntegrations(integrationActions, queryText)
    val filteredGroups = if (queryText.isBlank()) emptyList() else filterSettingsGroups(settingsGroups, queryText, searchResultsTitle)
    val filteredInternalItems = if (queryText.isBlank()) emptyList() else filterInternalItems(internalItems, queryText)

    val hasSearchResults by remember(
        filteredQuickActions,
        filteredGroups,
        filteredIntegrations,
        filteredInternalItems,
    ) {
        derivedStateOf {
            filteredQuickActions.isNotEmpty() ||
                filteredGroups.isNotEmpty() ||
                filteredIntegrations.isNotEmpty() ||
                filteredInternalItems.isNotEmpty()
        }
    }

    val internalGroup = if (filteredInternalItems.isNotEmpty()) {
        SettingsGroup(
            title = stringResource(R.string.internal_settings),
            items = filteredInternalItems,
        )
    } else null

    val contentState = SettingsContentState(
        profileHeader = SettingsProfileState(
            isLoading = isLoading,
            isLoggedIn = isLoggedIn,
            accountName = accountName,
            accountEmail = accountEmail,
            accountImageUrl = if (isLoggedIn) accountImageUrl else null,
        ),
        quickActions = quickActions,
        integrations = integrationActions,
        groups = settingsGroups,
        internalGroup = null,
        showPermissionBanner = shouldShowPermissionHint,
        showUpdateBanner = hasUpdate,
        latestVersion = fetchedLatestVersion,
        showBetaUpdateBanner = hasBetaUpdate,
        latestBetaVersion = fetchedLatestBetaVersion,
        isSearchActive = false,
        searchQuery = queryText,
        searchHistory = searchHistory,
        hasSearchResults = hasSearchResults,
        onProfileHeaderClick = { navController.navigate("settings/account") },
        onRequestPermission = {
            val toRequest = buildList {
                if (!isStorageGranted) add(storagePermission)
                if (!isNotificationGranted && notificationPermission != null) {
                    add(notificationPermission)
                }
            }
            if (toRequest.isNotEmpty()) {
                var currentContext = context
                var activity: android.app.Activity? = null
                while (currentContext is android.content.ContextWrapper) {
                    if (currentContext is android.app.Activity) {
                        activity = currentContext
                        break
                    }
                    currentContext = currentContext.baseContext
                }

                val shouldShowRationale = activity != null && toRequest.any {
                    androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
                }

                if (hasRequestedPermissions && !shouldShowRationale) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } else {
                    hasRequestedPermissions = true
                    settingsPrefs.edit().putBoolean("has_requested_permissions", true).apply()
                    permissionLauncher.launch(toRequest.toTypedArray())
                }
            }
        },
        onUpdateClick = { showUpdateDialog = true },
        onBetaUpdateClick = { showBetaUpdateDialog = true },
        onSearchHistoryItemClick = { clickedQuery ->
            query = TextFieldValue(clickedQuery)
            focusManager.clearFocus()
            saveSearch(clickedQuery)
        },
        onRemoveSearchHistoryItem = { q -> removeHistoryItem(q) },
        onClearSearchHistory = { clearHistory() },
    )

    val searchState = contentState.copy(
        isSearchActive = true,
        quickActions = filteredQuickActions,
        integrations = filteredIntegrations,
        groups = filteredGroups,
        internalGroup = internalGroup,
    )

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = !showSearchBar,
                enter = fadeIn(SettingsAnimations.fadeTween(if (animationsDisabled) 0 else 220)),
                exit = fadeOut(SettingsAnimations.fadeTween(if (animationsDisabled) 0 else 160)),
            ) {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.settings),
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = navController::navigateUp,
                            onLongClick = navController::backToMain,
                        ) {
                            Icon(
                                painterResource(R.drawable.arrow_back),
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { isSearching = true },
                            onLongClick = {},
                        ) {
                            Icon(
                                painterResource(R.drawable.search),
                                contentDescription = null,
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    ),
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = !showSearchBar,
                enter = fadeIn(SettingsAnimations.fadeTween(if (animationsDisabled) 0 else 220)),
                exit = fadeOut(SettingsAnimations.fadeTween(if (animationsDisabled) 0 else 160)),
            ) {
                AdaptiveSettingsLayout(
                    state = contentState,
                    listState = listState,
                    topPadding = innerPadding.calculateTopPadding(),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AnimatedVisibility(
                visible = showSearchBar,
                enter = fadeIn(SettingsAnimations.fadeTween(if (animationsDisabled) 0 else 220)),
                exit = fadeOut(SettingsAnimations.fadeTween(if (animationsDisabled) 0 else 160)),
            ) {
                TopSearch(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { 
                        focusManager.clearFocus() 
                        saveSearch(query.text.trim())
                    },
                    active = showSearchBar,
                    onActiveChange = { active ->
                        if (active) {
                            isSearching = true
                        } else {
                            resetSearch()
                        }
                    },
                    placeholder = { Text(text = stringResource(R.string.search)) },
                    leadingIcon = {
                        IconButton(
                            onClick = { resetSearch() },
                            onLongClick = {
                                if (queryText.isBlank()) {
                                    navController.backToMain()
                                }
                            },
                        ) {
                            Icon(
                                painterResource(R.drawable.arrow_back),
                                contentDescription = null,
                            )
                        }
                    },
                    trailingIcon = {
                        Row {
                            if (query.text.isNotBlank()) {
                                IconButton(
                                    onClick = { query = TextFieldValue() },
                                    onLongClick = {},
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.close),
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
                    },
                    focusRequester = focusRequester,
                ) {
                    AdaptiveSettingsLayout(
                        state = searchState,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    if (showUpdateDialog) {
        UpdateDownloadDialog(
            latestVersion = fetchedLatestVersion,
            isBeta = false,
            onDismiss = { showUpdateDialog = false }
        )
    }

    if (showBetaUpdateDialog) {
        UpdateDownloadDialog(
            latestVersion = fetchedLatestBetaVersion,
            isBeta = true,
            onDismiss = { showBetaUpdateDialog = false }
        )
    }
}

// --- SEARCH & FILTER LOGIC ---

fun filterQuickActions(
    actions: List<SettingsQuickAction>,
    query: String,
): List<SettingsQuickAction> {
    if (query.isBlank()) return emptyList()
    return actions.filter { it.label.contains(query, ignoreCase = true) }
}

fun filterSettingsGroups(
    groups: List<SettingsGroup>,
    query: String,
    searchResultsTitle: String
): List<SettingsGroup> {
    if (query.isBlank()) return emptyList()
    val allMatchedItems = groups.flatMap { it.items }.filter { matchesQuery(it, query) }
        .sortedBy { it.title.indexOf(query, ignoreCase = true).let { idx -> if (idx < 0) 1000 else idx } }
    
    if (allMatchedItems.isEmpty()) return emptyList()
    
    return listOf(SettingsGroup(title = searchResultsTitle, items = allMatchedItems))
}

fun matchesQuery(
    item: SettingsItem,
    query: String,
): Boolean {
    if (item.title.contains(query, ignoreCase = true)) return true
    if (item.subtitle?.contains(query, ignoreCase = true) == true) return true
    if (item.badge?.contains(query, ignoreCase = true) == true) return true
    return item.keywords.any { keyword ->
        keyword.contains(query, ignoreCase = true) ||
            query.contains(keyword, ignoreCase = true)
    }
}

fun filterInternalItems(
    items: List<SettingsItem>,
    query: String,
): List<SettingsItem> {
    if (query.isBlank()) return emptyList()
    return items.filter { matchesQuery(it, query) }
}

fun filterIntegrations(
    integrations: List<SettingsIntegrationAction>,
    query: String,
): List<SettingsIntegrationAction> {
    if (query.isBlank()) return emptyList()
    return integrations.filter { it.label.contains(query, ignoreCase = true) }
}

// --- BUILDER FUNCTIONS ---

@Composable
private fun buildQuickActions(navController: NavController, resetSearch: () -> Unit): List<SettingsQuickAction> {
    return listOf(
        SettingsQuickAction(
            icon = painterResource(R.drawable.palette),
            label = stringResource(R.string.appearance),
            onClick = { resetSearch(); navController.navigate("settings/appearance") },
            accentColor = MaterialTheme.colorScheme.primary
        ),
        SettingsQuickAction(
            icon = painterResource(R.drawable.play),
            label = stringResource(R.string.player_and_audio),
            onClick = { resetSearch(); navController.navigate("settings/player") },
            accentColor = MaterialTheme.colorScheme.secondary
        ),
        SettingsQuickAction(
            icon = painterResource(R.drawable.language),
            label = stringResource(R.string.content),
            onClick = { resetSearch(); navController.navigate("settings/content") },
            accentColor = MaterialTheme.colorScheme.tertiary
        ),
        SettingsQuickAction(
            icon = painterResource(R.drawable.storage),
            label = stringResource(R.string.storage),
            onClick = { resetSearch(); navController.navigate("settings/storage") },
            accentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun buildIntegrationActions(
    navController: NavController, 
    resetSearch: () -> Unit,
    onTogetherClick: () -> Unit
): List<SettingsIntegrationAction> {
    val uriHandler = LocalUriHandler.current
    return listOf(
        SettingsIntegrationAction(
            icon = painterResource(R.drawable.person),
            label = stringResource(R.string.music_together),
            onClick = { resetSearch(); onTogetherClick() },
            accentColor = Color(0xFF1DB954)
        ),
        SettingsIntegrationAction(
            icon = painterResource(R.drawable.discord),
            label = stringResource(R.string.discord),
            onClick = { resetSearch(); navController.navigate("settings/discord") },
            accentColor = Color(0xFF5865F2)
        ),
        SettingsIntegrationAction(
            icon = painterResource(R.drawable.github),
            label = stringResource(R.string.github),
            onClick = { resetSearch(); uriHandler.openUri("https://github.com/Arturo254/OpenTune") },
            accentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun buildSettingsGroups(
    navController: NavController,
    resetSearch: () -> Unit,
    onChangelogClick: () -> Unit,
    hasUnreadNews: Boolean
): List<SettingsGroup> {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    return listOf(
        SettingsGroup(
            title = stringResource(R.string.general_settings),
            items = listOf(
                SettingsItem(
                    icon = painterResource(R.drawable.person),
                    title = stringResource(R.string.account),
                    keywords = listOf("account", "login", "profile"),
                    onClick = { resetSearch(); navController.navigate("settings/account") }
                ),
                SettingsItem(
                    icon = painterResource(R.drawable.security),
                    title = stringResource(R.string.privacy),
                    keywords = listOf("privacy", "history", "security"),
                    onClick = { resetSearch(); navController.navigate("settings/privacy") }
                ),
                SettingsItem(
                    icon = painterResource(R.drawable.restore),
                    title = stringResource(R.string.backup_restore),
                    keywords = listOf("backup", "restore", "data"),
                    onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
                ),
                SettingsItem(
                    icon = painterResource(R.drawable.link),
                    title = stringResource(R.string.open_supported_links),
                    keywords = listOf("open", "supported", "links", "default"),
                    onClick = { 
                        resetSearch()
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                        } else {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                ),
                SettingsItem(
                    icon = painterResource(R.drawable.info),
                    title = stringResource(R.string.about),
                    keywords = listOf("about", "info", "version"),
                    onClick = { resetSearch(); navController.navigate("settings/about") }
                )
            )
        ),
        SettingsGroup(
            title = stringResource(R.string.community),
            items = listOf(
                SettingsItem(
                    icon = painterResource(R.drawable.schedule),
                    title = stringResource(R.string.Changelog),
                    keywords = listOf("changelog", "updates", "features"),
                    onClick = { resetSearch(); onChangelogClick() }
                ),
                SettingsItem(
                    icon = painterResource(R.drawable.telegram),
                    title = stringResource(R.string.Telegramchanel),
                    keywords = listOf("telegram", "community", "channel"),
                    onClick = { resetSearch(); uriHandler.openUri("https://t.me/opentune_updates") }
                )
            )
        )
    )
}

@Composable
private fun buildInternalItems(navController: NavController, resetSearch: () -> Unit): List<SettingsItem> {
    val context = LocalContext.current
    return listOf(
        // Account
        SettingsItem(
            icon = painterResource(R.drawable.person),
            title = stringResource(R.string.login),
            keywords = listOf("account", "login", "google", "sign in"),
            onClick = { resetSearch(); navController.navigate("settings/account") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.token),
            title = stringResource(R.string.advanced_login),
            keywords = listOf("advanced", "login", "token", "cookie"),
            onClick = { resetSearch(); navController.navigate("settings/account") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.person),
            title = stringResource(R.string.use_login_for_browse),
            keywords = listOf("use", "login", "browse", "account"),
            onClick = { resetSearch(); navController.navigate("settings/account") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.cached),
            title = stringResource(R.string.ytm_sync),
            keywords = listOf("youtube", "music", "sync", "ytm", "playlists"),
            onClick = { resetSearch(); navController.navigate("settings/account") }
        ),

        // Appearance
        SettingsItem(
            icon = painterResource(R.drawable.palette),
            title = stringResource(R.string.enable_dynamic_theme),
            keywords = listOf("dynamic", "theme", "color", "material you"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.dark_mode),
            title = stringResource(R.string.dark_theme),
            keywords = listOf("dark", "light", "theme", "mode", "amoled"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.contrast),
            title = stringResource(R.string.pure_black),
            keywords = listOf("pitch", "black", "amoled", "oled", "dark"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.language),
            title = stringResource(R.string.app_language),
            keywords = listOf("app", "language", "locale", "translation"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.gradient),
            title = stringResource(R.string.player_background_style),
            keywords = listOf("player", "background", "style", "blur", "gradient"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.line_curve),
            title = stringResource(R.string.player_buttons_style),
            keywords = listOf("thumbnail", "corner", "radius", "shape", "curve"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.palette),
            title = stringResource(R.string.player_buttons_style),
            keywords = listOf("player", "buttons", "style", "primary", "tertiary"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.sliders),
            title = stringResource(R.string.player_slider_style),
            keywords = listOf("player", "slider", "style", "squiggly", "slim"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.swipe),
            title = stringResource(R.string.enable_swipe_thumbnail),
            keywords = listOf("swipe", "thumbnail", "gesture"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.format_align_center),
            title = stringResource(R.string.player_text_alignment),
            keywords = listOf("player", "text", "alignment", "center", "sided"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.lyrics),
            title = stringResource(R.string.lyrics_text_position),
            keywords = listOf("lyrics", "text", "position", "alignment"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.lyrics),
            title = stringResource(R.string.lyrics_click_change),
            keywords = listOf("lyrics", "click", "change", "seek"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.nav_bar),
            title = stringResource(R.string.default_open_tab),
            keywords = listOf("default", "open", "tab", "home", "explore", "library"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.tab),
            title = stringResource(R.string.default_lib_chips),
            keywords = listOf("default", "library", "chips", "filter"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.nav_bar),
            title = stringResource(R.string.slim_navbar),
            keywords = listOf("slim", "navbar", "navigation", "bar"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.grid_view),
            title = stringResource(R.string.grid_cell_size),
            keywords = listOf("grid", "cell", "size", "large", "small"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        
        // Player
        SettingsItem(
            icon = painterResource(R.drawable.graphic_eq),
            title = stringResource(R.string.audio_quality),
            keywords = listOf("audio", "quality", "high", "low", "auto"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.fast_forward),
            title = stringResource(R.string.skip_silence),
            keywords = listOf("skip", "silence", "audio"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.volume_up),
            title = stringResource(R.string.audio_normalization),
            keywords = listOf("audio", "normalization", "volume", "loudness"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.queue_music),
            title = stringResource(R.string.persistent_queue),
            keywords = listOf("persistent", "queue", "save", "restore"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.playlist_add),
            title = stringResource(R.string.auto_load_more),
            keywords = listOf("auto", "load", "more", "queue", "network"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.similar),
            title = stringResource(R.string.enable_similar_content),
            keywords = listOf("enable", "similar", "content", "recommendations"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.skip_next),
            title = stringResource(R.string.auto_skip_next_on_error),
            keywords = listOf("auto", "skip", "error", "next"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.clear_all),
            title = stringResource(R.string.stop_music_on_task_clear),
            keywords = listOf("stop", "music", "task", "clear", "kill"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),

        // Content
        SettingsItem(
            icon = painterResource(R.drawable.language),
            title = stringResource(R.string.content_language),
            keywords = listOf("content", "language", "locale"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.location_on),
            title = stringResource(R.string.content_country),
            keywords = listOf("content", "country", "region"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.explicit),
            title = stringResource(R.string.hide_explicit),
            keywords = listOf("hide", "explicit", "content", "nsfw"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.info),
            title = stringResource(R.string.notification),
            keywords = listOf("notification", "permission", "alert"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.wifi_proxy),
            title = stringResource(R.string.enable_proxy),
            keywords = listOf("proxy", "network", "connection"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.lyrics),
            title = stringResource(R.string.enable_lrclib),
            keywords = listOf("lrclib", "lyrics", "provider", "synced"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.lyrics),
            title = stringResource(R.string.enable_kugou),
            keywords = listOf("kugou", "lyrics", "provider"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.trending_up),
            title = stringResource(R.string.top_length),
            keywords = listOf("top", "length", "size", "playlist"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.home_outlined),
            title = stringResource(R.string.set_quick_picks),
            keywords = listOf("quick", "picks", "home", "last listened"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.history),
            title = stringResource(R.string.history_duration),
            keywords = listOf("history", "duration", "scrobble", "time"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),

        // Storage
        SettingsItem(
            icon = painterResource(R.drawable.download),
            title = stringResource(R.string.downloaded_songs),
            keywords = listOf("downloaded", "songs", "storage", "clear"),
            onClick = { resetSearch(); navController.navigate("settings/storage") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.music_note),
            title = stringResource(R.string.song_cache),
            keywords = listOf("song", "cache", "storage", "clear"),
            onClick = { resetSearch(); navController.navigate("settings/storage") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.image),
            title = stringResource(R.string.image_cache),
            keywords = listOf("image", "cache", "storage", "clear"),
            onClick = { resetSearch(); navController.navigate("settings/storage") }
        ),

        // Privacy
        SettingsItem(
            icon = painterResource(R.drawable.history),
            title = stringResource(R.string.pause_listen_history),
            keywords = listOf("pause", "listen", "history", "privacy"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.delete_history),
            title = stringResource(R.string.clear_listen_history),
            keywords = listOf("clear", "listen", "history", "privacy"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.search_off),
            title = stringResource(R.string.pause_search_history),
            keywords = listOf("pause", "search", "history", "privacy"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.clear_all),
            title = stringResource(R.string.clear_search_history),
            keywords = listOf("clear", "search", "history", "privacy"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.screenshot),
            title = stringResource(R.string.disable_screenshot),
            keywords = listOf("disable", "screenshot", "privacy", "secure"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),

        // Backup & Restore
        SettingsItem(
            icon = painterResource(R.drawable.cloud_lock),
            title = stringResource(R.string.cloud_upload_title),
            keywords = listOf("cloud", "upload", "backup", "sync"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.backup),
            title = stringResource(R.string.backup),
            keywords = listOf("backup", "export", "data"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.restore),
            title = stringResource(R.string.restore),
            keywords = listOf("restore", "import", "data"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.replay),
            title = stringResource(R.string.visitor_data_title),
            keywords = listOf("visitor", "data", "reset", "clear"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
        ),
        
        // Discord
        SettingsItem(
            icon = painterResource(R.drawable.discord),
            title = stringResource(R.string.enable_discord_rpc),
            keywords = listOf("discord", "rpc", "rich presence", "status"),
            onClick = { resetSearch(); navController.navigate("settings/discord") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.info),
            title = stringResource(R.string.discord_use_details),
            keywords = listOf("discord", "details", "status"),
            onClick = { resetSearch(); navController.navigate("settings/discord") }
        ),
        
        // General
        SettingsItem(
            icon = painterResource(R.drawable.link),
            title = stringResource(R.string.open_supported_links),
            keywords = listOf("open", "supported", "links", "default"),
            onClick = {
                resetSearch()
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        )
    )
}

// --- STUB COMPOSABLES ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicTogetherScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.music_together), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.person),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.music_together),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This feature is currently unavailable or coming soon.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
