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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.arturo254.opentune.BuildConfig
import com.arturo254.opentune.LocalPlayerAwareWindowInsets
import com.arturo254.opentune.R
import com.arturo254.opentune.constants.AccountEmailKey
import com.arturo254.opentune.constants.AccountNameKey
import com.arturo254.opentune.constants.InnerTubeCookieKey
import com.arturo254.opentune.ui.component.AvatarPreferenceManager
import com.arturo254.opentune.ui.component.AvatarSelection
import com.arturo254.opentune.ui.component.ChangelogScreen
import com.arturo254.opentune.ui.component.IconButton
import com.arturo254.opentune.ui.component.TopSearch
import com.arturo254.opentune.ui.utils.backToMain
import com.arturo254.opentune.utils.rememberPreference
import com.arturo254.opentune.viewmodels.HomeViewModel
import com.arturo254.innertube.utils.parseCookieString
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val LocalAnimationsDisabled = compositionLocalOf { false }

// --- DYNAMIC RESOURCE HELPERS ---

@Composable
fun settingsString(name: String, fallback: String): String {
    val context = LocalContext.current
    val resId = remember(name) {
        context.resources.getIdentifier(name, "string", context.packageName)
    }
    return if (resId != 0) {
        stringResource(resId)
    } else {
        fallback
    }
}

@Composable
fun settingsString(name: String, fallback: String, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val resId = remember(name) {
        context.resources.getIdentifier(name, "string", context.packageName)
    }
    return if (resId != 0) {
        stringResource(resId, *formatArgs)
    } else {
        String.format(fallback, *formatArgs)
    }
}

@Composable
fun settingsIcon(name: String, fallbackId: Int): Painter {
    val context = LocalContext.current
    val resId = remember(name) {
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
    return painterResource(if (resId != 0) resId else fallbackId)
}

// --- VIEWMODEL PLACEHOLDER ---

class NewsViewModel : ViewModel() {
    private val _hasUnreadNews = MutableStateFlow(false)
    val hasUnreadNews: StateFlow<Boolean> = _hasUnreadNews.asStateFlow()
}

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
        else junit.framework.Assert.fail("Animations error").run { tween(durationMillis = ExitFadeDuration) }

    @Composable
    fun <T> fadeTween(durationMillis: Int): FiniteAnimationSpec<T> =
        if (LocalAnimationsDisabled.current) snap()
        else tween(durationMillis = durationMillis)

    @Composable
    fun <T> staggerTween(index: Int): FiniteAnimationSpec<T> =
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
    val listState = rememberLazyListState()
    val viewModel: HomeViewModel = hiltViewModel()

    val newsViewModel: NewsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val hasUnreadNews by newsViewModel.hasUnreadNews.collectAsState()

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

    val searchResultsTitle = settingsString("search_results", "Search Results")

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
            title = settingsString("internal_settings", "Internal Settings"),
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
                            text = settingsString("settings", "Settings"),
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = navController::navigateUp,
                            onLongClick = navController::backToMain,
                        ) {
                            Icon(
                                settingsIcon("arrow_back", R.drawable.arrow_back),
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
                                settingsIcon("search", R.drawable.search),
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
                    placeholder = { Text(text = settingsString("search", "Search")) },
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
                                settingsIcon("arrow_back", R.drawable.arrow_back),
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
                                        settingsIcon("close", R.drawable.close),
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
            icon = settingsIcon("palette", R.drawable.palette),
            label = settingsString("appearance", "Appearance"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") },
            accentColor = MaterialTheme.colorScheme.primary
        ),
        SettingsQuickAction(
            icon = settingsIcon("play", R.drawable.play),
            label = settingsString("player_and_audio", "Player and sound"),
            onClick = { resetSearch(); navController.navigate("settings/player") },
            accentColor = MaterialTheme.colorScheme.secondary
        ),
        SettingsQuickAction(
            icon = settingsIcon("language", R.drawable.language),
            label = settingsString("content", "Content"),
            onClick = { resetSearch(); navController.navigate("settings/content") },
            accentColor = MaterialTheme.colorScheme.tertiary
        ),
        SettingsQuickAction(
            icon = settingsIcon("storage", R.drawable.storage),
            label = settingsString("storage", "Storage"),
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
            icon = settingsIcon("person", R.drawable.person),
            label = settingsString("music_together", "Music Together"),
            onClick = { resetSearch(); onTogetherClick() },
            accentColor = Color(0xFF1DB954)
        ),
        SettingsIntegrationAction(
            icon = settingsIcon("discord", R.drawable.discord),
            label = settingsString("discord", "Discord"),
            onClick = { resetSearch(); navController.navigate("settings/discord") },
            accentColor = Color(0xFF5865F2)
        ),
        SettingsIntegrationAction(
            icon = settingsIcon("github", R.drawable.github),
            label = settingsString("github", "GitHub"),
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
            title = settingsString("general_settings", "General Settings"),
            items = listOf(
                SettingsItem(
                    icon = settingsIcon("person", R.drawable.person),
                    title = settingsString("account", "Account"),
                    keywords = listOf("account", "login", "profile"),
                    onClick = { resetSearch(); navController.navigate("settings/account") }
                ),
                SettingsItem(
                    icon = settingsIcon("speed", R.drawable.play),
                    title = settingsString("performance", "Performance"),
                    keywords = listOf("performance", "speed", "blur", "minimal"),
                    onClick = { resetSearch(); navController.navigate("settings/performance") }
                ),
                SettingsItem(
                    icon = settingsIcon("security", R.drawable.security),
                    title = settingsString("privacy", "Privacy"),
                    keywords = listOf("privacy", "history", "security"),
                    onClick = { resetSearch(); navController.navigate("settings/privacy") }
                ),
                SettingsItem(
                    icon = settingsIcon("restore", R.drawable.restore),
                    title = settingsString("backup_restore", "Backup & Restore"),
                    keywords = listOf("backup", "restore", "data"),
                    onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
                ),
                SettingsItem(
                    icon = settingsIcon("schedule", R.drawable.schedule),
                    title = "Alarm",
                    keywords = listOf("alarm", "wake", "time", "clock", "snooze"),
                    onClick = { resetSearch(); navController.navigate("alarm_settings") }
                ),
                SettingsItem(
                    icon = settingsIcon("link", R.drawable.link),
                    title = settingsString("open_supported_links", "Open supported links"),
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
                    icon = settingsIcon("info", R.drawable.info),
                    title = settingsString("about", "About"),
                    keywords = listOf("about", "info", "version"),
                    onClick = { resetSearch(); navController.navigate("settings/about") }
                )
            )
        ),
        SettingsGroup(
            title = settingsString("community", "Community"),
            items = listOf(
                SettingsItem(
                    icon = settingsIcon("newspaper", R.drawable.info),
                    title = settingsString("news", "News"),
                    badge = if (hasUnreadNews) settingsString("new_badge", "NEW") else null,
                    showUpdateIndicator = hasUnreadNews,
                    keywords = listOf("news", "updates", "announcements"),
                    onClick = { resetSearch(); navController.navigate("news") }
                ),
                SettingsItem(
                    icon = settingsIcon("schedule", R.drawable.schedule),
                    title = settingsString("Changelog", "Changelog"),
                    keywords = listOf("changelog", "updates", "features"),
                    onClick = { resetSearch(); onChangelogClick() }
                ),
                SettingsItem(
                    icon = settingsIcon("telegram", R.drawable.telegram),
                    title = settingsString("Telegramchanel", "Telegram Channel"),
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
            icon = settingsIcon("person", R.drawable.person),
            title = settingsString("login", "Login"),
            keywords = listOf("account", "login", "google", "sign in"),
            onClick = { resetSearch(); navController.navigate("settings/account") }
        ),
        SettingsItem(
            icon = settingsIcon("token", R.drawable.info),
            title = settingsString("advanced_login", "Advanced login"),
            keywords = listOf("advanced", "login", "token", "cookie"),
            onClick = { resetSearch(); navController.navigate("settings/account") }
        ),
        SettingsItem(
            icon = settingsIcon("person", R.drawable.person),
            title = settingsString("use_login_for_browse", "Use login for browse"),
            keywords = listOf("use", "login", "browse", "account"),
            onClick = { resetSearch(); navController.navigate("settings/account") }
        ),
        SettingsItem(
            icon = settingsIcon("cached", R.drawable.replay),
            title = settingsString("ytm_sync", "YTM Sync"),
            keywords = listOf("youtube", "music", "sync", "ytm", "playlists"),
            onClick = { resetSearch(); navController.navigate("settings/account") }
        ),

        // Appearance
        SettingsItem(
            icon = settingsIcon("palette", R.drawable.palette),
            title = settingsString("enable_dynamic_theme", "Enable dynamic theme"),
            keywords = listOf("dynamic", "theme", "color", "material you"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("palette", R.drawable.palette),
            title = settingsString("color_palette", "Color palette"),
            keywords = listOf("color", "palette", "custom theme"),
            onClick = { resetSearch(); navController.navigate("settings/appearance/palette") }
        ),
        SettingsItem(
            icon = settingsIcon("dark_mode", R.drawable.schedule),
            title = settingsString("dark_theme", "Dark theme"),
            keywords = listOf("dark", "light", "theme", "mode", "amoled"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("contrast", R.drawable.contrast),
            title = settingsString("pure_black", "Pure black"),
            keywords = listOf("pitch", "black", "amoled", "oled", "dark"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("text_fields", R.drawable.lyrics),
            title = settingsString("use_system_font", "Use system font"),
            keywords = listOf("font", "system", "text", "typeface"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("format_align_left", R.drawable.format_align_left),
            title = settingsString("app_text_size", "App text size"),
            keywords = listOf("text", "size", "large", "small", "font"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("language", R.drawable.language),
            title = settingsString("app_language", "App language"),
            keywords = listOf("app", "language", "locale", "translation"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("gradient", R.drawable.palette),
            title = settingsString("player_background_style", "Player background style"),
            keywords = listOf("player", "background", "style", "blur", "gradient"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("line_curve", R.drawable.line_curve),
            title = settingsString("shape_and_corners", "Shape and corners"),
            keywords = listOf("thumbnail", "corner", "radius", "shape", "curve"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("palette", R.drawable.palette),
            title = settingsString("player_buttons_style", "Player buttons style"),
            keywords = listOf("player", "buttons", "style", "primary", "tertiary"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("sliders", R.drawable.sliders),
            title = settingsString("player_slider_style", "Player slider style"),
            keywords = listOf("player", "slider", "style", "squiggly", "slim"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("swipe", R.drawable.swipe),
            title = settingsString("enable_swipe_thumbnail", "Enable swipe thumbnail"),
            keywords = listOf("swipe", "thumbnail", "gesture"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("format_align_center", R.drawable.format_align_center),
            title = settingsString("player_text_alignment", "Player text alignment"),
            keywords = listOf("player", "text", "alignment", "center", "sided"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("lyrics_text_position", "Lyrics text position"),
            keywords = listOf("lyrics", "text", "position", "alignment"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("lyrics_click_change", "Lyrics click change"),
            keywords = listOf("lyrics", "click", "change", "seek"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("artist", R.drawable.artist),
            title = settingsString("turn_on_artist_canvas", "Turn on artist canvas"),
            keywords = listOf("artist", "canvas", "video", "background"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("nav_bar", R.drawable.nav_bar),
            title = settingsString("default_open_tab", "Default open tab"),
            keywords = listOf("default", "open", "tab", "home", "explore", "library"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("tab", R.drawable.tab),
            title = settingsString("default_lib_chips", "Default library chips"),
            keywords = listOf("default", "library", "chips", "filter"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("nav_bar", R.drawable.nav_bar),
            title = settingsString("slim_navbar", "Slim navbar"),
            keywords = listOf("slim", "navbar", "navigation", "bar"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = settingsIcon("grid_view", R.drawable.grid_view),
            title = settingsString("grid_cell_size", "Grid cell size"),
            keywords = listOf("grid", "cell", "size", "large", "small"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        
        // Player
        SettingsItem(
            icon = settingsIcon("graphic_eq", R.drawable.graphic_eq),
            title = settingsString("audio_quality", "Audio quality"),
            keywords = listOf("audio", "quality", "high", "low", "auto"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("fast_forward", R.drawable.fast_forward),
            title = settingsString("double_tap_to_seek", "Double tap to seek"),
            keywords = listOf("double", "tap", "seek", "forward", "rewind"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("fast_forward", R.drawable.fast_forward),
            title = settingsString("skip_silence", "Skip silence"),
            keywords = listOf("skip", "silence", "audio"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("skip_next", R.drawable.skip_next),
            title = settingsString("enable_sponsorblock", "Enable SponsorBlock"),
            keywords = listOf("sponsor", "block", "skip", "sponsorblock"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("graphic_eq", R.drawable.graphic_eq),
            title = settingsString("premium_audio_fading", "Audio fading"),
            keywords = listOf("premium", "audio", "fading", "fade", "crossfade"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("volume_up", R.drawable.volume_up),
            title = settingsString("audio_normalization", "Audio normalization"),
            keywords = listOf("audio", "normalization", "volume", "loudness"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("queue_music", R.drawable.queue_music),
            title = settingsString("persistent_queue", "Persistent queue"),
            keywords = listOf("persistent", "queue", "save", "restore"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("skip_next", R.drawable.skip_next),
            title = settingsString("auto_skip_next_on_error", "Auto skip next on error"),
            keywords = listOf("auto", "skip", "error", "next"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("clear_all", R.drawable.clear_all),
            title = settingsString("stop_music_on_task_clear", "Stop music on task clear"),
            keywords = listOf("stop", "music", "task", "clear", "kill"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = settingsIcon("info", R.drawable.info),
            title = settingsString("show_nerd_stats", "Show nerd stats"),
            keywords = listOf("nerd", "stats", "info", "technical"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),

        // Performance
        SettingsItem(
            icon = settingsIcon("play", R.drawable.play),
            title = settingsString("minimal_player_design", "Minimal player design"),
            keywords = listOf("minimal", "player", "design", "performance"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),
        SettingsItem(
            icon = settingsIcon("image", R.drawable.image),
            title = settingsString("disable_blur_effects", "Disable blur effects"),
            keywords = listOf("disable", "blur", "effects", "performance"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("animate_lyrics", "Animate lyrics"),
            keywords = listOf("animate", "lyrics", "smooth", "performance"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),
        SettingsItem(
            icon = settingsIcon("playlist_add", R.drawable.playlist_add),
            title = settingsString("auto_load_more", "Auto load more"),
            keywords = listOf("auto", "load", "more", "queue", "network"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),
        SettingsItem(
            icon = settingsIcon("similar", R.drawable.similar),
            title = settingsString("enable_similar_content", "Enable similar content"),
            keywords = listOf("enable", "similar", "content", "recommendations"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),

        // Content
        SettingsItem(
            icon = settingsIcon("language", R.drawable.language),
            title = settingsString("content_language", "Content language"),
            keywords = listOf("content", "language", "locale"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("location_on", R.drawable.location_on),
            title = settingsString("content_country", "Content country"),
            keywords = listOf("content", "country", "region"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("explicit", R.drawable.explicit),
            title = settingsString("hide_explicit", "Hide explicit"),
            keywords = listOf("hide", "explicit", "content", "nsfw"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("play", R.drawable.play),
            title = settingsString("hide_music_videos", "Hide music videos"),
            keywords = listOf("hide", "music", "videos", "omv"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("info", R.drawable.info),
            title = settingsString("notification", "Notification"),
            keywords = listOf("notification", "permission", "alert"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("wifi_proxy", R.drawable.wifi_proxy),
            title = settingsString("enable_proxy", "Enable proxy"),
            keywords = listOf("proxy", "network", "connection"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("enable_lyrics_plus", "Enable Lyrics+"),
            keywords = listOf("lyrics", "plus", "provider", "ttml"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("enable_better_lyrics", "Enable Better Lyrics"),
            keywords = listOf("better", "lyrics", "provider", "ttml"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("enable_simpmusic", "Enable SimpMusic"),
            keywords = listOf("simpmusic", "lyrics", "provider"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("enable_paxsenix", "Enable Paxsenix"),
            keywords = listOf("paxsenix", "lyrics", "provider"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("enable_lrclib", "Enable LrcLib"),
            keywords = listOf("lrclib", "lyrics", "provider", "synced"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("lyrics", R.drawable.lyrics),
            title = settingsString("enable_kugou", "Enable Kugou"),
            keywords = listOf("kugou", "lyrics", "provider"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("list", R.drawable.list),
            title = settingsString("lyrics_provider_priority", "Lyrics provider priority"),
            keywords = listOf("lyrics", "provider", "priority", "order"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("trending_up", R.drawable.trending_up),
            title = settingsString("top_length", "Top length"),
            keywords = listOf("top", "length", "size", "playlist"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("home_outlined", R.drawable.home_outlined),
            title = settingsString("set_quick_picks", "Set quick picks"),
            keywords = listOf("quick", "picks", "home", "last listened"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = settingsIcon("history", R.drawable.history),
            title = settingsString("history_duration", "History duration"),
            keywords = listOf("history", "duration", "scrobble", "time"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),

        // Storage
        SettingsItem(
            icon = settingsIcon("download", R.drawable.download),
            title = settingsString("downloaded_songs", "Downloaded songs"),
            keywords = listOf("downloaded", "songs", "storage", "clear"),
            onClick = { resetSearch(); navController.navigate("settings/storage") }
        ),
        SettingsItem(
            icon = settingsIcon("music_note", R.drawable.music_note),
            title = settingsString("song_cache", "Song cache"),
            keywords = listOf("song", "cache", "storage", "clear"),
            onClick = { resetSearch(); navController.navigate("settings/storage") }
        ),
        SettingsItem(
            icon = settingsIcon("image", R.drawable.image),
            title = settingsString("image_cache", "Image cache"),
            keywords = listOf("image", "cache", "storage", "clear"),
            onClick = { resetSearch(); navController.navigate("settings/storage") }
        ),

        // Privacy
        SettingsItem(
            icon = settingsIcon("history", R.drawable.history),
            title = settingsString("pause_listen_history", "Pause listen history"),
            keywords = listOf("pause", "listen", "history", "privacy"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),
        SettingsItem(
            icon = settingsIcon("delete_history", R.drawable.delete_history),
            title = settingsString("clear_listen_history", "Clear listen history"),
            keywords = listOf("clear", "listen", "history", "privacy"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),
        SettingsItem(
            icon = settingsIcon("search_off", R.drawable.search_off),
            title = settingsString("pause_search_history", "Pause search history"),
            keywords = listOf("pause", "search", "history", "privacy"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),
        SettingsItem(
            icon = settingsIcon("clear_all", R.drawable.clear_all),
            title = settingsString("clear_search_history", "Clear search history"),
            keywords = listOf("clear", "search", "history", "privacy"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),
        SettingsItem(
            icon = settingsIcon("screenshot", R.drawable.screenshot),
            title = settingsString("disable_screenshot", "Disable screenshot"),
            keywords = listOf("disable", "screenshot", "privacy", "secure"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") }
        ),

        // Backup & Restore
        SettingsItem(
            icon = settingsIcon("cloud_lock", R.drawable.cloud_lock),
            title = settingsString("cloud_upload_title", "Cloud upload"),
            keywords = listOf("cloud", "upload", "backup", "sync"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
        ),
        SettingsItem(
            icon = settingsIcon("backup", R.drawable.backup),
            title = settingsString("backup", "Backup"),
            keywords = listOf("backup", "export", "data"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
        ),
        SettingsItem(
            icon = settingsIcon("restore", R.drawable.restore),
            title = settingsString("restore", "Restore"),
            keywords = listOf("restore", "import", "data"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
        ),
        SettingsItem(
            icon = settingsIcon("replay", R.drawable.replay),
            title = settingsString("visitor_data_title", "Visitor data"),
            keywords = listOf("visitor", "data", "reset", "clear"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
        ),
        
        // Discord
        SettingsItem(
            icon = settingsIcon("discord", R.drawable.discord),
            title = settingsString("enable_discord_rpc", "Enable Discord RPC"),
            keywords = listOf("discord", "rpc", "rich presence", "status"),
            onClick = { resetSearch(); navController.navigate("settings/discord") }
        ),
        SettingsItem(
            icon = settingsIcon("info", R.drawable.info),
            title = settingsString("discord_use_details", "Discord use details"),
            keywords = listOf("discord", "details", "status"),
            onClick = { resetSearch(); navController.navigate("settings/discord") }
        ),
        
        // General
        SettingsItem(
            icon = settingsIcon("link", R.drawable.link),
            title = settingsString("open_supported_links", "Open supported links"),
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

// --- MUSIC TOGETHER SCREEN PLACEHOLDER ---

@Composable
fun MusicTogetherScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Music Together") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Music Together Feature coming soon.", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
