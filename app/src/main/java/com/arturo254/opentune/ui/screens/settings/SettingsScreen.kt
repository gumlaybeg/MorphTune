@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalFoundationApi::class
)

package com.arturo254.opentune.ui.screens.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import com.arturo254.opentune.checkForUpdates
import com.arturo254.opentune.isNewerVersion
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.ui.platform.UriHandler
import com.arturo254.opentune.ui.component.SettingsCategory
import com.arturo254.opentune.ui.component.SettingsCategoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL

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
    fun <T> staggerTween(index: Int): FiniteAnimationSpec<T> =
        if (LocalAnimationsDisabled.current) snap()
        else tween(durationMillis = EntranceSlideDuration, delayMillis = index * StaggerDelayPerItem)
}

// --- MODELS ---

data class SettingsQuickAction(
    val icon: androidx.compose.ui.graphics.painter.Painter,
    val label: String,
    val onClick: () -> Unit,
    val accentColor: Color,
)

data class SettingsGroup(
    val title: String,
    val items: List<SettingsItem>,
)

data class SettingsItem(
    val icon: androidx.compose.ui.graphics.painter.Painter,
    val title: String,
    val subtitle: String? = null,
    val badge: String? = null,
    val showUpdateIndicator: Boolean = false,
    val accentColor: Color = Color.Unspecified,
    val keywords: List<String> = emptyList(),
    val onClick: () -> Unit,
)

data class SettingsIntegrationAction(
    val icon: androidx.compose.ui.graphics.painter.Painter,
    val label: String,
    val onClick: () -> Unit,
    val accentColor: Color,
)

data class SettingsProfileState(
    val isLoading: Boolean,
    val isLoggedIn: Boolean,
    val accountName: String,
    val accountEmail: String,
    val accountImageUrl: String?,
)

data class SettingsContentState(
    val profileHeader: SettingsProfileState,
    val quickActions: List<SettingsQuickAction>,
    val integrations: List<SettingsIntegrationAction>,
    val groups: List<SettingsGroup>,
    val internalGroup: SettingsGroup?,
    val showPermissionBanner: Boolean,
    val showUpdateBanner: Boolean,
    val latestVersion: String,
    val showBetaUpdateBanner: Boolean,
    val latestBetaVersion: String,
    val isSearchActive: Boolean,
    val searchQuery: String,
    val searchHistory: List<String>,
    val hasSearchResults: Boolean,
    val onProfileHeaderClick: () -> Unit,
    val onRequestPermission: () -> Unit,
    val onUpdateClick: () -> Unit,
    val onBetaUpdateClick: () -> Unit,
    val onSearchHistoryItemClick: (String) -> Unit,
    val onRemoveSearchHistoryItem: (String) -> Unit,
    val onClearSearchHistory: () -> Unit,
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

    val hasUnreadNews = false

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
        if (newVersion != null) {
            if (isNewerVersion(newVersion, BuildConfig.VERSION_NAME)) {
                hasUpdate = true
                fetchedLatestVersion = newVersion
            }
        }
        
        val newBetaVersion = checkForBetaUpdates()
        if (newBetaVersion != null) {
            if (isNewerVersion(newBetaVersion, BuildConfig.VERSION_NAME)) {
                if (newVersion == null || isNewerVersion(newBetaVersion, newVersion)) {
                    hasBetaUpdate = true
                    fetchedLatestBetaVersion = newBetaVersion
                }
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

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isStorageGranted = ContextCompat.checkSelfPermission(context, storagePermission) == PackageManager.PERMISSION_GRANTED
                isNotificationGranted = notificationPermission == null ||
                    ContextCompat.checkSelfPermission(context, notificationPermission) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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

    val searchResultsTitle = "Search Results"

    val filteredQuickActions = if (queryText.isBlank()) emptyList<SettingsQuickAction>() else filterQuickActions(quickActions, queryText)
    val filteredIntegrations = if (queryText.isBlank()) emptyList<SettingsIntegrationAction>() else filterIntegrations(integrationActions, queryText)
    val filteredGroups = if (queryText.isBlank()) emptyList<SettingsGroup>() else filterSettingsGroups(settingsGroups, queryText, searchResultsTitle)
    val filteredInternalItems = if (queryText.isBlank()) emptyList<SettingsItem>() else filterInternalItems(internalItems, queryText)

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
            title = "Internal Settings",
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
                        data = Uri.parse("package:${context.packageName}")
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
            label = "Music Together",
            onClick = { resetSearch(); onTogetherClick() },
            accentColor = Color(0xFF1DB954)
        ),
        SettingsIntegrationAction(
            icon = painterResource(R.drawable.discord),
            label = "Discord",
            onClick = { resetSearch(); navController.navigate("settings/discord") },
            accentColor = Color(0xFF5865F2)
        ),
        SettingsIntegrationAction(
            icon = painterResource(R.drawable.github),
            label = "GitHub",
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
                    icon = painterResource(R.drawable.speed),
                    title = "Performance",
                    keywords = listOf("performance", "speed", "blur", "minimal"),
                    onClick = { resetSearch(); navController.navigate("settings/performance") }
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
                    icon = painterResource(R.drawable.schedule),
                    title = "Alarm",
                    keywords = listOf("alarm", "wake", "time", "clock", "snooze"),
                    onClick = { resetSearch(); navController.navigate("alarm_settings") }
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
                    icon = painterResource(R.drawable.info),
                    title = "News",
                    badge = if (hasUnreadNews) "New" else null,
                    showUpdateIndicator = hasUnreadNews,
                    keywords = listOf("news", "updates", "announcements"),
                    onClick = { resetSearch(); navController.navigate("news") }
                ),
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
            icon = painterResource(R.drawable.palette),
            title = "Color Palette",
            keywords = listOf("color", "palette", "custom theme"),
            onClick = { resetSearch(); navController.navigate("settings/appearance/palette") }
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
            icon = painterResource(R.drawable.info),
            title = "Use System Font",
            keywords = listOf("font", "system", "text", "typeface"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.format_align_left),
            title = "App Text Size",
            keywords = listOf("text", "size", "large", "small", "font"),
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
            title = "Shape and Corners",
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
            keywords = listOf("player", "sliders", "style", "squiggly", "slim"),
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
            icon = painterResource(R.drawable.album),
            title = "Turn on Artist Canvas",
            keywords = listOf("artist", "canvas", "video", "background"),
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
            title = "Double Tap to Seek",
            keywords = listOf("double", "tap", "seek", "forward", "rewind"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.fast_forward),
            title = stringResource(R.string.skip_silence),
            keywords = listOf("skip", "silence", "audio"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.skip_next),
            title = "Enable SponsorBlock",
            keywords = listOf("sponsor", "block", "skip", "sponsorblock"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.graphic_eq),
            title = "Premium Audio Fading",
            keywords = listOf("premium", "audio", "fading", "fade", "crossfade"),
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
        SettingsItem(
            icon = painterResource(R.drawable.info),
            title = "Show Nerd Stats",
            keywords = listOf("nerd", "stats", "info", "technical"),
            onClick = { resetSearch(); navController.navigate("settings/player") }
        ),

        // Performance
        SettingsItem(
            icon = painterResource(R.drawable.play),
            title = "Minimal Player Design",
            keywords = listOf("minimal", "player", "design", "performance"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.image),
            title = "Disable Blur Effects",
            keywords = listOf("disable", "blur", "effects", "performance"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.lyrics),
            title = stringResource(R.string.animate_lyrics),
            keywords = listOf("animate", "lyrics", "smooth", "performance"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.playlist_add),
            title = stringResource(R.string.auto_load_more),
            keywords = listOf("auto", "load", "more", "queue", "network"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.similar),
            title = stringResource(R.string.enable_similar_content),
            keywords = listOf("enable", "similar", "content", "recommendations"),
            onClick = { resetSearch(); navController.navigate("settings/performance") }
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
            icon = painterResource(R.drawable.play),
            title = "Hide Music Videos",
            keywords = listOf("hide", "music", "videos", "omv"),
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
            title = "Enable Lyrics Plus",
            keywords = listOf("lyrics", "plus", "provider", "ttml"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.lyrics),
            title = "Enable Better Lyrics",
            keywords = listOf("better", "lyrics", "provider", "ttml"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.lyrics),
            title = "Enable SimpMusic",
            keywords = listOf("simpmusic", "lyrics", "provider"),
            onClick = { resetSearch(); navController.navigate("settings/content") }
        ),
        SettingsItem(
            icon = painterResource(R.drawable.lyrics),
            title = "Enable Paxsenix",
            keywords = listOf("paxsenix", "lyrics", "provider"),
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
            icon = painterResource(R.drawable.list),
            title = "Lyrics Provider Priority",
            keywords = listOf("lyrics", "provider", "priority", "order"),
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

// --- CORE UTILITY FUNCTIONS ---

@Composable
fun VersionCard(uriHandler: androidx.compose.ui.platform.UriHandler) {
    val context = LocalContext.current
    val appVersion = remember { getAppVersion(context) }

    Spacer(Modifier.height(16.dp))

    SettingsCategory(
        title = "App Info",
        items = listOf(
            SettingsCategoryItem(
                icon = painterResource(R.drawable.info),
                title = {
                    Column {
                        Text(
                            text = "Version",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = appVersion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_forward),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = { uriHandler.openUri("https://github.com/Arturo254/OpenTune/releases/latest") }
            )
        )
    )
}

@Composable
fun UpdateCard(latestVersion: String = "") {
    val context = LocalContext.current
    var showUpdateCard by remember { mutableStateOf(false) }
    var currentLatestVersion by remember { mutableStateOf(latestVersion) }
    var showDownloadDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val newVersion = checkForUpdates()
        if (newVersion != null && isNewerVersion(newVersion, BuildConfig.VERSION_NAME)) {
            showUpdateCard = true
            currentLatestVersion = newVersion
        }
    }

    if (showDownloadDialog) {
        UpdateDownloadDialog(
            latestVersion = currentLatestVersion,
            isBeta = false,
            onDismiss = { showDownloadDialog = false }
        )
    }

    if (showUpdateCard) {
        Spacer(Modifier.height(25.dp))
        androidx.compose.material3.ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(170.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
            shape = RoundedCornerShape(38.dp),
            onClick = {
                showDownloadDialog = true
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(3.dp))

                Text(
                    text = "New Version: $currentLatestVersion",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Warning ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.error,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Tap to Update",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun UpdateDownloadDialog(
    latestVersion: String,
    isBeta: Boolean = false,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var downloadProgress by remember { mutableStateOf(0f) }
    var downloadStatus by remember { mutableStateOf(DownloadStatus.NOT_STARTED) }
    var downloadedApkUri by remember { mutableStateOf<Uri?>(null) }
    val downloadScope = rememberCoroutineScope()

    val installPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context.packageManager.canRequestPackageInstalls() && downloadedApkUri != null) {
                installApk(context, downloadedApkUri!!)
            }
        }
    }

    Dialog(onDismissRequest = {
        if (downloadStatus != DownloadStatus.DOWNLOADING) {
            onDismiss()
        }
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBeta) "Update Beta Version ($latestVersion)" else "Update Version ($latestVersion)",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (downloadStatus) {
                    DownloadStatus.NOT_STARTED -> {
                        Text(if (isBeta) "Do you want to download the beta update?" else "Do you want to download the update?")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Cancel")
                            }
                            Button(onClick = {
                                downloadStatus = DownloadStatus.DOWNLOADING
                                downloadScope.launch {
                                    downloadedApkUri =
                                        downloadApk(context, latestVersion) { progress ->
                                            downloadProgress = progress
                                        }
                                    if (downloadedApkUri != null) {
                                        downloadStatus = DownloadStatus.COMPLETED
                                        downloadProgress = 1f
                                    } else {
                                        downloadStatus = DownloadStatus.ERROR
                                    }
                                }
                            }) {
                                Text("Download")
                            }
                        }
                    }

                    DownloadStatus.DOWNLOADING -> {
                        Text("Downloading...")
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "${(downloadProgress * 100).toInt()}%",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    DownloadStatus.COMPLETED -> {
                        Text("Download completed")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Close")
                            }
                            Button(onClick = {
                                if (downloadedApkUri != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        if (!context.packageManager.canRequestPackageInstalls()) {
                                            val intent =
                                                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                                                    .setData("package:${context.packageName}".toUri())

                                            installPermissionLauncher.launch(intent)
                                        } else {
                                            installApk(context, downloadedApkUri!!)
                                        }
                                    } else {
                                        installApk(context, downloadedApkUri!!)
                                    }
                                }
                            }) {
                                Text("Install")
                            }
                        }
                    }

                    DownloadStatus.ERROR -> {
                        Text("Download error")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onDismiss) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

suspend fun downloadApk(
    context: Context,
    version: String,
    onProgressUpdate: (Float) -> Unit
): Uri? = withContext(Dispatchers.IO) {
    try {
        val apkUrl = "https://github.com/Arturo254/OpenTune/releases/download/$version/app-release.apk"

        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val apkFile = File(downloadDir, "app-release-$version.apk")

        if (apkFile.exists()) {
            apkFile.delete()
        }

        val client = OkHttpClient()
        var request = Request.Builder().url(apkUrl).build()
        var response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            val altUrl = "https://github.com/Arturo254/OpenTune/releases/download/$version/OpenTune-$version.apk"
            request = Request.Builder().url(altUrl).build()
            response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext null
            }
        }

        val body = response.body ?: return@withContext null
        val contentLength = body.contentLength()
        val inputStream = body.byteStream()
        val outputStream = FileOutputStream(apkFile)
        val buffer = ByteArray(8 * 1024)
        var totalBytesRead = 0L
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
            totalBytesRead += bytesRead

            if (contentLength > 0) {
                val progress = totalBytesRead.toFloat() / contentLength.toFloat()
                withContext(Dispatchers.Main) {
                    onProgressUpdate(progress)
                }
            }
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
        
        withContext(Dispatchers.Main) {
            onProgressUpdate(1f)
        }

        return@withContext FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}

fun installApk(context: Context, apkUri: Uri) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pm = context.packageManager
        val isAllowed = pm.canRequestPackageInstalls()
        if (!isAllowed) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                .setData("package:${context.packageName}".toUri())
            context.startActivity(intent)
            return
        }
    }

    val installIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(apkUri, "application/vnd.android.package-archive")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }

    context.startActivity(installIntent)
}

suspend fun checkForBetaUpdates(): String? = withContext(Dispatchers.IO) {
    try {
        val url = URL("https://api.github.com/repos/Arturo254/OpenTune/releases")
        val connection = url.openConnection()
        connection.connect()
        val json = connection.getInputStream().reader().use { it.readText() }
        val jsonArray = org.json.JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val release = jsonArray.getJSONObject(i)
            if (release.getBoolean("prerelease")) {
                return@withContext release.getString("tag_name")
            }
        }
        return@withContext null
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}

enum class DownloadStatus {
    NOT_STARTED,
    DOWNLOADING,
    COMPLETED,
    ERROR
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                context.packageName,
                0
            )
        }
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

// --- COMPOSE MOCK WRAPPER FOR MUSIC TOGETHER ---

@Composable
fun MusicTogetherScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Music Together is not available in this build.", color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}

// --- ADAPTIVE LAYOUT COMPOSABLES ---

@Composable
fun AdaptiveSettingsLayout(
    state: SettingsContentState,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    topPadding: Dp = 0.dp,
) {
    val layoutMode = resolveLayoutMode()
    val animationsDisabled = LocalAnimationsDisabled.current

    var heroVisible by remember { mutableStateOf(false) }
    var bannerVisible by remember { mutableStateOf(false) }
    var quickActionsVisible by remember { mutableStateOf(false) }
    var integrationsVisible by remember { mutableStateOf(false) }
    var categoriesVisible by remember { mutableStateOf(false) }

    LaunchedEffect(animationsDisabled) {
        if (animationsDisabled) {
            heroVisible = true
            bannerVisible = true
            quickActionsVisible = true
            integrationsVisible = true
            categoriesVisible = true
            return@LaunchedEffect
        }

        val anim = Animatable(0f)
        anim.animateTo(1f, tween(50))
        heroVisible = true
        anim.animateTo(1f, tween(60))
        bannerVisible = true
        anim.animateTo(1f, tween(60))
        quickActionsVisible = true
        anim.animateTo(1f, tween(70))
        integrationsVisible = true
        anim.animateTo(1f, tween(70))
        categoriesVisible = true
    }

    val quickActionColumns = when (layoutMode) {
        SettingsLayoutMode.COMPACT -> SettingsDimensions.CompactColumns
        SettingsLayoutMode.MEDIUM -> SettingsDimensions.MediumColumns
        SettingsLayoutMode.EXPANDED -> SettingsDimensions.ExpandedColumns
    }

    when (layoutMode) {
        SettingsLayoutMode.COMPACT -> {
            CompactSettingsLayout(
                state = state,
                listState = listState,
                quickActionColumns = quickActionColumns,
                heroVisible = heroVisible,
                bannerVisible = bannerVisible,
                quickActionsVisible = quickActionsVisible,
                integrationsVisible = integrationsVisible,
                categoriesVisible = categoriesVisible,
                topPadding = topPadding,
                modifier = modifier,
            )
        }
        SettingsLayoutMode.MEDIUM -> {
            MediumSettingsLayout(
                state = state,
                quickActionColumns = quickActionColumns,
                heroVisible = heroVisible,
                bannerVisible = bannerVisible,
                quickActionsVisible = quickActionsVisible,
                integrationsVisible = integrationsVisible,
                categoriesVisible = categoriesVisible,
                topPadding = topPadding,
                modifier = modifier,
            )
        }
        SettingsLayoutMode.EXPANDED -> {
            ExpandedSettingsLayout(
                state = state,
                quickActionColumns = quickActionColumns,
                heroVisible = heroVisible,
                bannerVisible = bannerVisible,
                quickActionsVisible = quickActionsVisible,
                integrationsVisible = integrationsVisible,
                categoriesVisible = categoriesVisible,
                topPadding = topPadding,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CompactSettingsLayout(
    state: SettingsContentState,
    listState: LazyListState,
    quickActionColumns: Int,
    heroVisible: Boolean,
    bannerVisible: Boolean,
    quickActionsVisible: Boolean,
    integrationsVisible: Boolean,
    categoriesVisible: Boolean,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val pad = SettingsDimensions.ScreenHorizontalPadding
    val spacing = SettingsDimensions.SectionSpacing

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            ),
        contentPadding = PaddingValues(top = topPadding, bottom = 32.dp),
    ) {
        if (!state.isSearchActive) {
            item(key = "hero") {
                AnimatedVisibility(
                    visible = heroVisible,
                    enter = fadeIn(SettingsAnimations.entranceSpring()) +
                        slideInVertically(
                            initialOffsetY = { -it / 5 },
                            animationSpec = SettingsAnimations.entranceSpring(),
                        ),
                ) {
                    SettingsProfileHeader(
                        state = state.profileHeader,
                        onClick = state.onProfileHeaderClick,
                        modifier = Modifier
                            .padding(horizontal = pad)
                            .padding(top = 4.dp, bottom = spacing),
                    )
                }
            }

            item(key = "permission") {
                AnimatedVisibility(
                    visible = bannerVisible && state.showPermissionBanner,
                    enter = fadeIn(SettingsAnimations.entranceSpring()) +
                        expandVertically(SettingsAnimations.entranceSpring()),
                    exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                ) {
                    SettingsPermissionBanner(
                        onRequestPermission = state.onRequestPermission,
                        modifier = Modifier
                            .padding(horizontal = pad)
                            .padding(bottom = spacing),
                    )
                }
            }

            item(key = "update") {
                AnimatedVisibility(
                    visible = bannerVisible && state.showUpdateBanner,
                    enter = fadeIn(SettingsAnimations.entranceSpring()) +
                        expandVertically(SettingsAnimations.entranceSpring()),
                    exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                ) {
                    SettingsUpdateBanner(
                        latestVersion = state.latestVersion,
                        onClick = state.onUpdateClick,
                        modifier = Modifier
                            .padding(horizontal = pad)
                            .padding(bottom = spacing),
                    )
                }
            }

            item(key = "beta_update") {
                AnimatedVisibility(
                    visible = bannerVisible && state.showBetaUpdateBanner,
                    enter = fadeIn(SettingsAnimations.entranceSpring()) +
                        expandVertically(SettingsAnimations.entranceSpring()),
                    exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                ) {
                    SettingsBetaUpdateBanner(
                        latestVersion = state.latestBetaVersion,
                        onClick = state.onBetaUpdateClick,
                        modifier = Modifier
                            .padding(horizontal = pad)
                            .padding(bottom = spacing),
                    )
                }
            }
        }

        if (state.isSearchActive && state.searchQuery.isBlank()) {
            SearchHistorySection(state, pad)
        } else if (state.isSearchActive && !state.hasSearchResults) {
            item(key = "empty") {
                Spacer(modifier = Modifier.height(24.dp).animateItem())
                SettingsSearchEmpty(
                    modifier = Modifier.padding(horizontal = pad).animateItem(),
                )
            }
        } else {
            if (state.quickActions.isNotEmpty()) {
                item(key = "quickActions") {
                    AnimatedVisibility(
                        modifier = Modifier.animateItem(),
                        visible = quickActionsVisible,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            slideInVertically(
                                initialOffsetY = { it / 6 },
                                animationSpec = SettingsAnimations.entranceSpring(),
                            ),
                    ) {
                        SettingsQuickActionsSection(
                            actions = state.quickActions,
                            columns = quickActionColumns,
                            modifier = Modifier
                                .padding(horizontal = pad)
                                .padding(bottom = spacing),
                        )
                    }
                }
            }

            if (state.integrations.isNotEmpty()) {
                item(key = "integrations") {
                    AnimatedVisibility(
                        modifier = Modifier.animateItem(),
                        visible = integrationsVisible,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            slideInVertically(
                                initialOffsetY = { it / 6 },
                                animationSpec = SettingsAnimations.entranceSpring(),
                            ),
                    ) {
                        SettingsIntegrationsSection(
                            integrations = state.integrations,
                            modifier = Modifier
                                .padding(horizontal = pad)
                                .padding(bottom = spacing),
                        )
                    }
                }
            }

            if (state.internalGroup != null && state.internalGroup.items.isNotEmpty()) {
                item(key = "internalSearchResults") {
                    SettingsGroupCard(
                        group = state.internalGroup,
                        modifier = Modifier
                            .padding(horizontal = pad)
                            .padding(bottom = spacing)
                            .animateItem(),
                    )
                }
            }

            items(
                count = state.groups.size,
                key = { state.groups[it].title },
            ) { index ->
                val group = state.groups[index]
                AnimatedVisibility(
                    modifier = Modifier.animateItem(),
                    visible = categoriesVisible,
                    enter = fadeIn(
                        SettingsAnimations.staggerTween(index)
                    ) + slideInVertically(
                        initialOffsetY = { it / 5 },
                        animationSpec = SettingsAnimations.staggerTween(index),
                    ),
                ) {
                    SettingsGroupCard(
                        group = group,
                        modifier = Modifier
                            .padding(horizontal = pad)
                            .padding(bottom = spacing),
                    )
                }
            }
        }
    }
}

@Composable
private fun MediumSettingsLayout(
    state: SettingsContentState,
    quickActionColumns: Int,
    heroVisible: Boolean,
    bannerVisible: Boolean,
    quickActionsVisible: Boolean,
    integrationsVisible: Boolean,
    categoriesVisible: Boolean,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val pad = SettingsDimensions.ScreenHorizontalPadding
    val spacing = SettingsDimensions.SectionSpacing

    Row(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .padding(horizontal = pad),
        horizontalArrangement = Arrangement.spacedBy(pad),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(SettingsDimensions.MediumPaneLeftWeight)
                .fillMaxHeight(),
            contentPadding = PaddingValues(top = topPadding, bottom = 32.dp),
        ) {
            if (!state.isSearchActive) {
                item(key = "hero") {
                    AnimatedVisibility(
                        visible = heroVisible,
                        enter = fadeIn(SettingsAnimations.entranceSpring()),
                    ) {
                        SettingsProfileHeader(
                            state = state.profileHeader,
                            onClick = state.onProfileHeaderClick,
                            modifier = Modifier.padding(top = 4.dp, bottom = spacing),
                        )
                    }
                }

                item(key = "permission") {
                    AnimatedVisibility(
                        visible = bannerVisible && state.showPermissionBanner,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            expandVertically(SettingsAnimations.entranceSpring()),
                        exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                    ) {
                        SettingsPermissionBanner(
                            onRequestPermission = state.onRequestPermission,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }

                item(key = "update") {
                    AnimatedVisibility(
                        visible = bannerVisible && state.showUpdateBanner,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            expandVertically(SettingsAnimations.entranceSpring()),
                        exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                    ) {
                        SettingsUpdateBanner(
                            latestVersion = state.latestVersion,
                            onClick = state.onUpdateClick,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }

                item(key = "beta_update") {
                    AnimatedVisibility(
                        visible = bannerVisible && state.showBetaUpdateBanner,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            expandVertically(SettingsAnimations.entranceSpring()),
                        exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                    ) {
                        SettingsBetaUpdateBanner(
                            latestVersion = state.latestBetaVersion,
                            onClick = state.onBetaUpdateClick,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }
            }

            if (!state.isSearchActive || state.searchQuery.isNotBlank()) {
                if (state.quickActions.isNotEmpty()) {
                    item(key = "quickActions") {
                        AnimatedVisibility(
                            modifier = Modifier.animateItem(),
                            visible = quickActionsVisible,
                            enter = fadeIn(SettingsAnimations.entranceSpring()),
                        ) {
                            SettingsQuickActionsSection(
                                actions = state.quickActions,
                                columns = 2,
                                modifier = Modifier.padding(bottom = spacing),
                            )
                        }
                    }
                }

                if (state.integrations.isNotEmpty()) {
                    item(key = "integrations") {
                        AnimatedVisibility(
                            modifier = Modifier.animateItem(),
                            visible = integrationsVisible,
                            enter = fadeIn(SettingsAnimations.entranceSpring()),
                        ) {
                            SettingsIntegrationsSection(
                                integrations = state.integrations,
                                modifier = Modifier.padding(bottom = spacing),
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(SettingsDimensions.MediumPaneRightWeight)
                .fillMaxHeight(),
            contentPadding = PaddingValues(top = topPadding, bottom = 32.dp),
        ) {
            if (state.isSearchActive && state.searchQuery.isBlank()) {
                SearchHistorySection(state, 0.dp)
            } else if (state.isSearchActive && !state.hasSearchResults) {
                item(key = "empty") {
                    Spacer(modifier = Modifier.height(24.dp).animateItem())
                    SettingsSearchEmpty(modifier = Modifier.animateItem())
                }
            } else {
                if (state.internalGroup != null && state.internalGroup.items.isNotEmpty()) {
                    item(key = "internalSearchResults") {
                        SettingsGroupCard(
                            group = state.internalGroup,
                            modifier = Modifier.padding(bottom = spacing).animateItem(),
                        )
                    }
                }

                items(
                    count = state.groups.size,
                    key = { state.groups[it].title },
                ) { index ->
                    AnimatedVisibility(
                        modifier = Modifier.animateItem(),
                        visible = categoriesVisible,
                        enter = fadeIn(
                            SettingsAnimations.staggerTween(index)
                        ) + slideInVertically(
                            initialOffsetY = { it / 5 },
                            animationSpec = SettingsAnimations.staggerTween(index),
                        ),
                    ) {
                        SettingsGroupCard(
                            group = state.groups[index],
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedSettingsLayout(
    state: SettingsContentState,
    quickActionColumns: Int,
    heroVisible: Boolean,
    bannerVisible: Boolean,
    quickActionsVisible: Boolean,
    integrationsVisible: Boolean,
    categoriesVisible: Boolean,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val pad = SettingsDimensions.ScreenHorizontalPadding
    val spacing = SettingsDimensions.SectionSpacing

    Row(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .padding(horizontal = pad),
        horizontalArrangement = Arrangement.spacedBy(pad),
    ) {
        LazyColumn(
            modifier = Modifier
                .width(SettingsDimensions.ExpandedListPaneWidth)
                .fillMaxHeight(),
            contentPadding = PaddingValues(top = topPadding, bottom = 32.dp),
        ) {
            if (!state.isSearchActive) {
                item(key = "hero") {
                    AnimatedVisibility(
                        visible = heroVisible,
                        enter = fadeIn(SettingsAnimations.entranceSpring()),
                    ) {
                        SettingsProfileHeader(
                            state = state.profileHeader,
                            onClick = state.onProfileHeaderClick,
                            modifier = Modifier.padding(top = 4.dp, bottom = spacing),
                        )
                    }
                }

                item(key = "permission") {
                    AnimatedVisibility(
                        visible = bannerVisible && state.showPermissionBanner,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            expandVertically(SettingsAnimations.entranceSpring()),
                        exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                    ) {
                        SettingsPermissionBanner(
                            onRequestPermission = state.onRequestPermission,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }

                item(key = "update") {
                    AnimatedVisibility(
                        visible = bannerVisible && state.showUpdateBanner,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            expandVertically(SettingsAnimations.entranceSpring()),
                        exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                    ) {
                        SettingsUpdateBanner(
                            latestVersion = state.latestVersion,
                            onClick = state.onUpdateClick,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }

                item(key = "beta_update") {
                    AnimatedVisibility(
                        visible = bannerVisible && state.showBetaUpdateBanner,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            expandVertically(SettingsAnimations.entranceSpring()),
                        exit = fadeOut(SettingsAnimations.exitTween()) + shrinkVertically(SettingsAnimations.exitTween()),
                    ) {
                        SettingsBetaUpdateBanner(
                            latestVersion = state.latestBetaVersion,
                            onClick = state.onBetaUpdateClick,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }
            }

            if (!state.isSearchActive || state.searchQuery.isNotBlank()) {
                if (state.quickActions.isNotEmpty()) {
                    item(key = "quickActions") {
                        AnimatedVisibility(
                            modifier = Modifier.animateItem(),
                            visible = quickActionsVisible,
                            enter = fadeIn(SettingsAnimations.entranceSpring()),
                        ) {
                            SettingsQuickActionsSection(
                                actions = state.quickActions,
                                columns = 2,
                                modifier = Modifier.padding(bottom = spacing),
                            )
                        }
                    }
                }

                if (state.integrations.isNotEmpty()) {
                    item(key = "integrations") {
                        AnimatedVisibility(
                            modifier = Modifier.animateItem(),
                            visible = integrationsVisible,
                            enter = fadeIn(SettingsAnimations.entranceSpring()),
                        ) {
                            SettingsIntegrationsSection(
                                integrations = state.integrations,
                                modifier = Modifier.padding(bottom = spacing),
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(top = topPadding, bottom = 32.dp),
        ) {
            if (state.isSearchActive && state.searchQuery.isBlank()) {
                SearchHistorySection(state, 0.dp)
            } else if (state.isSearchActive && !state.hasSearchResults) {
                item(key = "empty") {
                    Spacer(modifier = Modifier.height(24.dp).animateItem())
                    SettingsSearchEmpty(modifier = Modifier.animateItem())
                }
            } else {
                if (state.internalGroup != null && state.internalGroup.items.isNotEmpty()) {
                    item(key = "internalSearchResults") {
                        SettingsGroupCard(
                            group = state.internalGroup,
                            modifier = Modifier.padding(bottom = spacing).animateItem(),
                        )
                    }
                }

                items(
                    count = state.groups.size,
                    key = { state.groups[it].title },
                ) { index ->
                    AnimatedVisibility(
                        modifier = Modifier.animateItem(),
                        visible = categoriesVisible,
                        enter = fadeIn(
                            SettingsAnimations.staggerTween(index)
                        ) + slideInVertically(
                            initialOffsetY = { it / 5 },
                            animationSpec = SettingsAnimations.staggerTween(index),
                        ),
                    ) {
                        SettingsGroupCard(
                            group = state.groups[index],
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }
            }
        }
    }
}
