/*
 * ArchiveTune & OpenTune Projects (2026)
 * Licensed Under GPL-3.0 | see git history for contributors
 */

package com.arturo254.opentune.ui.screens.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.minimumInteractiveComponentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.arturo254.innertube.utils.parseCookieString
import com.arturo254.opentune.BuildConfig
import com.arturo254.opentune.LocalPlayerAwareWindowInsets
import com.arturo254.opentune.R
import com.arturo254.opentune.checkForUpdates
import com.arturo254.opentune.isNewerVersion
import com.arturo254.opentune.UpdateDownloadDialog
import com.arturo254.opentune.DownloadStatus
import com.arturo254.opentune.ui.component.AvatarPreferenceManager
import com.arturo254.opentune.ui.component.AvatarSelection
import com.arturo254.opentune.ui.component.ChangelogScreen
import com.arturo254.opentune.ui.component.TopSearch
import com.arturo254.opentune.ui.utils.backToMain
import com.arturo254.opentune.utils.rememberPreference
import com.arturo254.opentune.utils.rememberEnumPreference
import com.arturo254.opentune.constants.*

// ==========================================
// 1. MAIN SETTINGS CONTAINER COMPOSABLE
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    latestVersion: Long,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val isAndroid12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val listState = rememberLazyListState()

    val avatarManager = remember { AvatarPreferenceManager(context) }
    val currentSelection by avatarManager.getAvatarSelection.collectAsState(initial = AvatarSelection.Default)
    val accountName by rememberPreference(AccountNameKey, "")
    val accountEmail by rememberPreference(AccountEmailKey, "")
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }

    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(TextFieldValue()) }
    val focusRequester = remember { FocusRequester() }

    var showTranslateDialog by remember { mutableStateOf(false) }
    var showChangelogSheet by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    var currentLatestVersion by remember { mutableStateOf(BuildConfig.VERSION_NAME) }

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
    ) { result ->
        isStorageGranted = result[storagePermission] == true || isStorageGranted
        if (notificationPermission != null) {
            isNotificationGranted = result[notificationPermission] == true || isNotificationGranted
        }
    }

    val shouldShowPermissionHint = !isStorageGranted || !isNotificationGranted

    var hasUpdate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val newVersion = checkForUpdates()
        if (newVersion != null && isNewerVersion(newVersion, BuildConfig.VERSION_NAME)) {
            hasUpdate = true
            currentLatestVersion = newVersion
        }
    }

    val resetSearch: () -> Unit = {
        isSearching = false
        query = TextFieldValue()
        focusManager.clearFocus()
    }

    val quickActions = buildQuickActions(navController, resetSearch)
    val integrationActions = buildIntegrationActions(navController, resetSearch)
    
    val settingsGroups = buildSettingsGroups(
        navController = navController,
        isLoggedIn = isLoggedIn,
        accountEmail = accountEmail,
        onTranslateClick = { showTranslateDialog = true },
        onChangelogClick = { showChangelogSheet = true },
        onDonateClick = { uriHandler.openUri("https://www.paypal.me/OpenTune") },
        onTelegramClick = { uriHandler.openUri("https://t.me/opentune_updates") },
        resetSearch = resetSearch
    )
    val internalItems = buildInternalItems(navController, resetSearch)

    val queryText = query.text.trim()
    val showSearchBar = isSearching || queryText.isNotBlank()

    val filteredQuickActions = filterQuickActions(quickActions, queryText)
    val filteredIntegrations = filterIntegrations(integrationActions, queryText)
    val filteredGroups = filterSettingsGroups(settingsGroups, queryText)
    val filteredInternalItems = filterInternalItems(internalItems, queryText)

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
            title = stringResource(R.string.advanced),
            items = filteredInternalItems,
        )
    } else null

    val contentState = SettingsContentState(
        quickActions = if (queryText.isBlank()) quickActions else filteredQuickActions,
        integrations = if (queryText.isBlank()) integrationActions else filteredIntegrations,
        groups = if (queryText.isBlank()) settingsGroups else filteredGroups,
        internalGroup = if (queryText.isNotBlank()) internalGroup else null,
        showPermissionBanner = shouldShowPermissionHint,
        showUpdateBanner = hasUpdate,
        latestVersion = currentLatestVersion,
        isSearchActive = queryText.isNotBlank(),
        hasSearchResults = hasSearchResults,
        onRequestPermission = {
            val toRequest = buildList {
                if (!isStorageGranted) add(storagePermission)
                if (!isNotificationGranted && notificationPermission != null) {
                    add(notificationPermission)
                }
            }
            if (toRequest.isNotEmpty()) {
                permissionLauncher.launch(toRequest.toTypedArray())
            }
        },
        onUpdateClick = { showDownloadDialog = true },
    )

    Scaffold(
        topBar = {
            if (!showSearchBar) {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.settings),
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        SettingsIconButton(
                            onClick = { navController.navigateUp() },
                            onLongClick = { navController.backToMain() }
                        ) {
                            Icon(
                                painterResource(R.drawable.arrow_back),
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        SettingsIconButton(
                            onClick = { isSearching = true }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.search),
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
            if (!showSearchBar) {
                AdaptiveSettingsLayout(
                    state = contentState,
                    listState = listState,
                    topPadding = innerPadding.calculateTopPadding(),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AnimatedVisibility(
                visible = showSearchBar,
                enter = fadeIn(tween(durationMillis = 220)),
                exit = fadeOut(tween(durationMillis = 160)),
            ) {
                TopSearch(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { focusManager.clearFocus() },
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
                        SettingsIconButton(
                            onClick = { resetSearch() },
                            onLongClick = {
                                if (queryText.isBlank()) {
                                    navController.backToMain()
                                }
                            }
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
                                SettingsIconButton(
                                    onClick = { query = TextFieldValue() }
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
                    val searchState = contentState.copy(
                        isSearchActive = true,
                    )
                    AdaptiveSettingsLayout(
                        state = searchState,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    // Modal & Dialog Management
    if (showTranslateDialog) {
        AlertDialog(
            onDismissRequest = { showTranslateDialog = false },
            title = { Text(stringResource(R.string.Redirección)) },
            text = { Text(stringResource(R.string.poeditor_redirect)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTranslateDialog = false
                        uriHandler.openUri("https://poeditor.com/join/project/208BwCVazA")
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showChangelogSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChangelogSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = {
                Surface(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(32.dp)
                        .height(4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ) {}
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ChangelogScreen()
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (showDownloadDialog) {
        UpdateDownloadDialog(
            latestVersion = currentLatestVersion,
            onDismiss = { showDownloadDialog = false }
        )
    }
}

// ==========================================
// 2. HELPER BUTTONS (SettingsIconButton)
// ==========================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .clip(CircleShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick ?: {},
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// ==========================================
// 3. PROFILE HEADER & SYSTEM COMPONENTS
// ==========================================

@Composable
fun SettingsProfileHeader(
    isLoggedIn: Boolean,
    accountName: String,
    currentSelection: AvatarSelection,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SettingsDimensions.HeroCardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f),
                            Color.Transparent,
                        ),
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 22.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(SettingsDimensions.HeroIconSize)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
                                ),
                            ),
                        )
                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoggedIn) {
                        when (currentSelection) {
                            is AvatarSelection.Custom -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data((currentSelection as AvatarSelection.Custom).uri.toUri())
                                        .crossfade(true)
                                        .error(R.drawable.person)
                                        .placeholder(R.drawable.person)
                                        .build(),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            is AvatarSelection.DiceBear -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data((currentSelection as AvatarSelection.DiceBear).url)
                                        .crossfade(true)
                                        .error(R.drawable.person)
                                        .placeholder(R.drawable.person)
                                        .build(),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                val initials = remember(accountName) {
                                    val cleanName = accountName.replace("@", "").trim()
                                    when {
                                        cleanName.isEmpty() -> "?"
                                        cleanName.contains(" ") -> {
                                            val parts = cleanName.split(" ")
                                            "${parts.first().firstOrNull()?.uppercase() ?: ""}${
                                                parts.last().firstOrNull()?.uppercase() ?: ""
                                            }"
                                        }
                                        else -> cleanName.take(2).uppercase()
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.tertiary
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.opentune_monochrome),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(SettingsDimensions.HeroIconInnerSize),
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isLoggedIn) accountName.replace("@", "").trim() else stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    ) {
                        Text(
                            text = if (isLoggedIn) stringResource(R.string.account) else "v${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsPermissionBanner(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SettingsDimensions.BannerCardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                    ),
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(SettingsDimensions.BannerIconSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.security),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(SettingsDimensions.BannerIconInnerSize),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "System Permissions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Grant permissions for storage and notifications to enable full features.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { onRequestPermission() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Allow",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
fun SettingsUpdateBanner(
    latestVersion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) SettingsAnimations.PressScale else 1f,
        animationSpec = SettingsAnimations.pressSpring(),
        label = "updateScale",
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(SettingsDimensions.BannerCardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent,
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    ),
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(SettingsDimensions.BannerIconSize)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.update),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(SettingsDimensions.BannerIconInnerSize),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(R.string.new_version_available),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "v$latestVersion",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium,
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.navigate_next),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
fun SettingsSearchEmpty(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SettingsDimensions.GroupCardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }

            Text(
                text = stringResource(R.string.no_results_found),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = "Search again with different keywords",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun SettingsGroupCard(
    group: SettingsGroup,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = group.title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 1.2f,
            modifier = Modifier.padding(
                horizontal = SettingsDimensions.SectionHeaderHorizontalPadding,
                vertical = SettingsDimensions.SectionHeaderBottomPadding,
            ),
        )

        Card(
            shape = RoundedCornerShape(SettingsDimensions.GroupCardCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column {
                group.items.forEachIndexed { index, item ->
                    SettingsRow(
                        item = item,
                        showDivider = index < group.items.size - 1,
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    item: SettingsItem,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    val effectiveAccent = if (item.accentColor.isSpecified) {
        item.accentColor
    } else {
        MaterialTheme.colorScheme.primary
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = SettingsAnimations.pressSpring(),
        label = "rowScale",
    )
    val bgAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.06f else 0f,
        animationSpec = SettingsAnimations.pressSpring(),
        label = "rowBgAlpha",
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .background(MaterialTheme.colorScheme.primary.copy(alpha = bgAlpha))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = item.onClick,
                )
                .padding(
                    horizontal = SettingsDimensions.RowHorizontalPadding,
                    vertical = SettingsDimensions.RowVerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(SettingsDimensions.RowIconSize)
                    .clip(RoundedCornerShape(SettingsDimensions.RowIconCornerRadius))
                    .background(effectiveAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                if (item.showUpdateIndicator) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(8.dp),
                            )
                        },
                    ) {
                        Icon(
                            painter = item.icon,
                            contentDescription = null,
                            tint = effectiveAccent,
                            modifier = Modifier.size(SettingsDimensions.RowIconInnerSize),
                        )
                    }
                } else {
                    Icon(
                        painter = item.icon,
                        contentDescription = null,
                        tint = effectiveAccent,
                        modifier = Modifier.size(SettingsDimensions.RowIconInnerSize),
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                item.subtitle?.let { subtitle ->
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (item.showUpdateIndicator) {
                            effectiveAccent
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            item.badge?.let { badge ->
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                painter = painterResource(R.drawable.navigate_next),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(SettingsDimensions.ChevronSize),
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = SettingsDimensions.DividerStartIndent),
                thickness = SettingsDimensions.DividerThickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            )
        }
    }
}

// ==========================================
// 8. ADAPTIVE LAYOUTS
// ==========================================

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
    val context = LocalContext.current
    val avatarManager = remember { AvatarPreferenceManager(context) }
    val currentSelection by avatarManager.getAvatarSelection.collectAsState(initial = AvatarSelection.Default)
    val accountName by rememberPreference(AccountNameKey, "")
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }

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
                    isLoggedIn = isLoggedIn,
                    accountName = accountName,
                    currentSelection = currentSelection,
                    modifier = Modifier
                        .padding(horizontal = pad)
                        .padding(top = 4.dp, bottom = spacing),
                )
            }
        }

        if (!state.isSearchActive) {
            item(key = "permission") {
                AnimatedVisibility(
                    visible = bannerVisible && state.showPermissionBanner,
                    enter = fadeIn(SettingsAnimations.entranceSpring()) +
                        expandVertically(SettingsAnimations.entranceSpring()),
                    exit = fadeOut(tween(300)) + shrinkVertically(tween(300)),
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
                    exit = fadeOut(tween(300)) + shrinkVertically(tween(300)),
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
        }

        if (state.quickActions.isNotEmpty()) {
            item(key = "quickActions") {
                AnimatedVisibility(
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

        if (state.isSearchActive && !state.hasSearchResults) {
            item(key = "empty") {
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSearchEmpty(
                    modifier = Modifier.padding(horizontal = pad),
                )
            }
        } else {
            if (state.internalGroup != null && state.internalGroup.items.isNotEmpty()) {
                item(key = "internalSearchResults") {
                    SettingsGroupCard(
                        group = state.internalGroup,
                        modifier = Modifier
                            .padding(horizontal = pad)
                            .padding(bottom = spacing),
                    )
                }
            }

            items(
                count = state.groups.size,
                key = { state.groups[it].title },
            ) { index ->
                val group = state.groups[index]
                AnimatedVisibility(
                    visible = categoriesVisible,
                    enter = fadeIn(
                        tween(
                            SettingsAnimations.EntranceSlideDuration,
                            delayMillis = index * SettingsAnimations.StaggerDelayPerItem,
                        )
                    ) + slideInVertically(
                        initialOffsetY = { it / 5 },
                        animationSpec = tween(
                            SettingsAnimations.EntranceSlideDuration,
                            delayMillis = index * SettingsAnimations.StaggerDelayPerItem,
                        ),
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
    val context = LocalContext.current
    val avatarManager = remember { AvatarPreferenceManager(context) }
    val currentSelection by avatarManager.getAvatarSelection.collectAsState(initial = AvatarSelection.Default)
    val accountName by rememberPreference(AccountNameKey, "")
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }

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
            item(key = "hero") {
                AnimatedVisibility(
                    visible = heroVisible,
                    enter = fadeIn(SettingsAnimations.entranceSpring()),
                ) {
                    SettingsProfileHeader(
                        isLoggedIn = isLoggedIn,
                        accountName = accountName,
                        currentSelection = currentSelection,
                        modifier = Modifier.padding(top = 4.dp, bottom = spacing),
                    )
                }
            }

            if (!state.isSearchActive) {
                item(key = "permission") {
                    AnimatedVisibility(
                        visible = bannerVisible && state.showPermissionBanner,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            expandVertically(SettingsAnimations.entranceSpring()),
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(300)),
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
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(300)),
                    ) {
                        SettingsUpdateBanner(
                            latestVersion = state.latestVersion,
                            onClick = state.onUpdateClick,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }
            }

            if (state.quickActions.isNotEmpty()) {
                item(key = "quickActions") {
                    AnimatedVisibility(
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

        LazyColumn(
            modifier = Modifier
                .weight(SettingsDimensions.MediumPaneRightWeight)
                .fillMaxHeight(),
            contentPadding = PaddingValues(top = topPadding, bottom = 32.dp),
        ) {
            if (state.isSearchActive && !state.hasSearchResults) {
                item(key = "empty") {
                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSearchEmpty()
                }
            } else {
                if (state.internalGroup != null && state.internalGroup.items.isNotEmpty()) {
                    item(key = "internalSearchResults") {
                        SettingsGroupCard(
                            group = state.internalGroup,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }

                items(
                    count = state.groups.size,
                    key = { state.groups[it].title },
                ) { index ->
                    AnimatedVisibility(
                        visible = categoriesVisible,
                        enter = fadeIn(
                            tween(
                                SettingsAnimations.EntranceSlideDuration,
                                delayMillis = index * SettingsAnimations.StaggerDelayPerItem,
                            )
                        ) + slideInVertically(
                            initialOffsetY = { it / 5 },
                            animationSpec = tween(
                                SettingsAnimations.EntranceSlideDuration,
                                delayMillis = index * SettingsAnimations.StaggerDelayPerItem,
                            ),
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
    val context = LocalContext.current
    val avatarManager = remember { AvatarPreferenceManager(context) }
    val currentSelection by avatarManager.getAvatarSelection.collectAsState(initial = AvatarSelection.Default)
    val accountName by rememberPreference(AccountNameKey, "")
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }

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
            item(key = "hero") {
                AnimatedVisibility(
                    visible = heroVisible,
                    enter = fadeIn(SettingsAnimations.entranceSpring()),
                ) {
                    SettingsProfileHeader(
                        isLoggedIn = isLoggedIn,
                        accountName = accountName,
                        currentSelection = currentSelection,
                        modifier = Modifier.padding(top = 4.dp, bottom = spacing),
                    )
                }
            }

            if (!state.isSearchActive) {
                item(key = "permission") {
                    AnimatedVisibility(
                        visible = bannerVisible && state.showPermissionBanner,
                        enter = fadeIn(SettingsAnimations.entranceSpring()) +
                            expandVertically(SettingsAnimations.entranceSpring()),
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(300)),
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
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(300)),
                    ) {
                        SettingsUpdateBanner(
                            latestVersion = state.latestVersion,
                            onClick = state.onUpdateClick,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }
            }

            if (state.quickActions.isNotEmpty()) {
                item(key = "quickActions") {
                    AnimatedVisibility(
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

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(top = topPadding, bottom = 32.dp),
        ) {
            if (state.isSearchActive && !state.hasSearchResults) {
                item(key = "empty") {
                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSearchEmpty()
                }
            } else {
                if (state.internalGroup != null && state.internalGroup.items.isNotEmpty()) {
                    item(key = "internalSearchResults") {
                        SettingsGroupCard(
                            group = state.internalGroup,
                            modifier = Modifier.padding(bottom = spacing),
                        )
                    }
                }

                items(
                    count = state.groups.size,
                    key = { state.groups[it].title },
                ) { index ->
                    AnimatedVisibility(
                        visible = categoriesVisible,
                        enter = fadeIn(
                            tween(
                                SettingsAnimations.EntranceSlideDuration,
                                delayMillis = index * SettingsAnimations.StaggerDelayPerItem,
                            )
                        ) + slideInVertically(
                            initialOffsetY = { it / 5 },
                            animationSpec = tween(
                                SettingsAnimations.EntranceSlideDuration,
                                delayMillis = index * SettingsAnimations.StaggerDelayPerItem,
                            ),
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

// ==========================================
// 9. QUICK ACTION COMPONENT
// ==========================================

@Composable
fun SettingsQuickActionsSection(
    actions: List<SettingsQuickAction>,
    columns: Int = SettingsDimensions.CompactColumns,
    modifier: Modifier = Modifier,
) {
    if (actions.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val rows = actions.chunked(columns)
        rows.forEach { rowActions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowActions.forEach { action ->
                    QuickActionCard(
                        action = action,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowActions.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    action: SettingsQuickAction,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) SettingsAnimations.TilePressScale else 1f,
        animationSpec = SettingsAnimations.pressSpring(),
        label = "tileScale",
    )
    val iconRotation by animateFloatAsState(
        targetValue = if (isPressed) SettingsAnimations.IconPressRotation else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconRotation",
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .aspectRatio(SettingsDimensions.QuickActionTileAspectRatio),
        shape = RoundedCornerShape(SettingsDimensions.QuickActionCardCornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        onClick = action.onClick,
        interactionSource = interactionSource,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            action.accentColor.copy(alpha = 0.10f),
                            Color.Transparent,
                        ),
                    ),
                )
                .padding(14.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(SettingsDimensions.QuickActionIconSize)
                        .clip(RoundedCornerShape(12.dp))
                        .background(action.accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = action.icon,
                        contentDescription = action.label,
                        tint = action.accentColor,
                        modifier = Modifier
                            .size(SettingsDimensions.QuickActionIconInnerSize)
                            .graphicsLayer { rotationZ = iconRotation },
                    )
                }

                Text(
                    text = action.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ==========================================
// 10. INTEGRATIONS BAR
// ==========================================

@Composable
fun SettingsIntegrationsSection(
    integrations: List<SettingsIntegrationAction>,
    modifier: Modifier = Modifier,
) {
    if (integrations.isEmpty()) return

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            count = integrations.size,
            key = { integrations[it].label },
        ) { index ->
            IntegrationPill(action = integrations[index])
        }
    }
}

@Composable
fun IntegrationPill(
    action: SettingsIntegrationAction,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) SettingsAnimations.PillPressScale else 1f,
        animationSpec = SettingsAnimations.pressSpring(),
        label = "pillScale",
    )
    val lift by animateFloatAsState(
        targetValue = if (isPressed) SettingsAnimations.PillPressLift.value else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pillLift",
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .graphicsLayer { translationY = lift },
        shape = RoundedCornerShape(SettingsDimensions.IntegrationPillCornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        onClick = action.onClick,
        interactionSource = interactionSource,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(SettingsDimensions.IntegrationIconSize)
                    .clip(RoundedCornerShape(8.dp))
                    .background(action.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = action.icon,
                    contentDescription = null,
                    tint = action.accentColor,
                    modifier = Modifier.size(SettingsDimensions.IntegrationIconInnerSize),
                )
            }

            Text(
                text = action.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ==========================================
// 11. BUILDERS & ACTION MAPPINGS
// ==========================================

@Composable
fun buildQuickActions(
    navController: NavController,
    resetSearch: () -> Unit,
): List<SettingsQuickAction> =
    listOf(
        SettingsQuickAction(
            icon = painterResource(R.drawable.palette),
            label = stringResource(R.string.appearance),
            onClick = { resetSearch(); navController.navigate("settings/appearance") },
            accentColor = MaterialTheme.colorScheme.primary,
        ),
        SettingsQuickAction(
            icon = painterResource(R.drawable.play),
            label = stringResource(R.string.player_and_audio),
            onClick = { resetSearch(); navController.navigate("settings/player") },
            accentColor = MaterialTheme.colorScheme.tertiary,
        ),
        SettingsQuickAction(
            icon = painterResource(R.drawable.storage),
            label = stringResource(R.string.storage),
            onClick = { resetSearch(); navController.navigate("settings/storage") },
            accentColor = MaterialTheme.colorScheme.secondary,
        ),
        SettingsQuickAction(
            icon = painterResource(R.drawable.security),
            label = stringResource(R.string.privacy),
            onClick = { resetSearch(); navController.navigate("settings/privacy") },
            accentColor = MaterialTheme.colorScheme.error,
        ),
    )

@Composable
fun buildIntegrationActions(
    navController: NavController,
    resetSearch: () -> Unit,
): List<SettingsIntegrationAction> =
    listOf(
        SettingsIntegrationAction(
            icon = painterResource(R.drawable.discord),
            label = stringResource(R.string.discord_integration),
            onClick = { resetSearch(); navController.navigate("settings/discord") },
            accentColor = Color(0xFF5865F2),
        )
    )

@Composable
fun buildSettingsGroups(
    navController: NavController,
    isLoggedIn: Boolean,
    accountEmail: String,
    onTranslateClick: () -> Unit,
    onChangelogClick: () -> Unit,
    onDonateClick: () -> Unit,
    onTelegramClick: () -> Unit,
    resetSearch: () -> Unit,
): List<SettingsGroup> =
    buildList {
        add(
            SettingsGroup(
                title = stringResource(R.string.account),
                items = listOf(
                    SettingsItem(
                        icon = painterResource(R.drawable.person),
                        title = stringResource(R.string.account),
                        subtitle = if (isLoggedIn && accountEmail.isNotBlank()) accountEmail else stringResource(R.string.not_logged_in),
                        accentColor = MaterialTheme.colorScheme.primary,
                        keywords = listOf("google", "account", "login", "sync", "cuenta", "sesion"),
                        onClick = { resetSearch(); navController.navigate("settings/account") }
                    ),
                    SettingsItem(
                        icon = painterResource(R.drawable.language),
                        title = stringResource(R.string.content),
                        subtitle = stringResource(R.string.content_language),
                        accentColor = MaterialTheme.colorScheme.secondary,
                        keywords = listOf("language", "content", "country", "explicit", "idioma", "contenido", "pais"),
                        onClick = { resetSearch(); navController.navigate("settings/content") }
                    ),
                    SettingsItem(
                        icon = painterResource(R.drawable.restore),
                        title = stringResource(R.string.backup_restore),
                        subtitle = stringResource(R.string.backup),
                        accentColor = MaterialTheme.colorScheme.tertiary,
                        keywords = listOf("backup", "restore", "import", "export", "copia", "seguridad", "restauracion"),
                        onClick = { resetSearch(); navController.navigate("settings/backup_restore") }
                    )
                )
            )
        )

        add(
            SettingsGroup(
                title = stringResource(R.string.app_info),
                items = listOf(
                    SettingsItem(
                        icon = painterResource(R.drawable.info),
                        title = stringResource(R.string.about),
                        subtitle = "OpenTune",
                        accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        keywords = listOf("about", "version", "app", "info", "contributors", "colaboradores", "acerca de"),
                        onClick = { resetSearch(); navController.navigate("settings/about") }
                    ),
                    SettingsItem(
                        icon = painterResource(R.drawable.translate),
                        title = stringResource(R.string.Translate),
                        accentColor = MaterialTheme.colorScheme.primary,
                        keywords = listOf("translate", "traducir", "poeditor", "languages", "idioma"),
                        onClick = { resetSearch(); onTranslateClick() }
                    )
                )
            )
        )

        add(
            SettingsGroup(
                title = stringResource(R.string.community),
                items = listOf(
                    SettingsItem(
                        icon = painterResource(R.drawable.schedule),
                        title = stringResource(R.string.Changelog),
                        accentColor = MaterialTheme.colorScheme.secondary,
                        keywords = listOf("changelog", "release", "changes", "updates", "registro de cambios"),
                        onClick = { resetSearch(); onChangelogClick() }
                    ),
                    SettingsItem(
                        icon = painterResource(R.drawable.paypal),
                        title = stringResource(R.string.Donate),
                        accentColor = MaterialTheme.colorScheme.error,
                        keywords = listOf("donate", "paypal", "support", "donar", "contribucion"),
                        onClick = { resetSearch(); onDonateClick() }
                    ),
                    SettingsItem(
                        icon = painterResource(R.drawable.telegram),
                        title = stringResource(R.string.Telegramchanel),
                        accentColor = Color(0xFF0088CC),
                        keywords = listOf("telegram", "channel", "community", "updates", "canal"),
                        onClick = { resetSearch(); onTelegramClick() }
                    )
                )
            )
        )
    }

@Composable
fun buildInternalItems(
    navController: NavController,
    resetSearch: () -> Unit,
): List<SettingsItem> =
    listOf(
        SettingsItem(
            icon = painterResource(R.drawable.palette),
            title = stringResource(R.string.appearance),
            subtitle = stringResource(R.string.dark_theme),
            accentColor = MaterialTheme.colorScheme.primary,
            keywords = listOf("theme", "palette", "material you", "dynamic color", "font", "ui"),
            onClick = { resetSearch(); navController.navigate("settings/appearance") },
        ),
        SettingsItem(
            icon = painterResource(R.drawable.play),
            title = stringResource(R.string.player_and_audio),
            subtitle = stringResource(R.string.audio_quality),
            accentColor = MaterialTheme.colorScheme.tertiary,
            keywords = listOf("audio", "playback", "volume", "quality", "equalizer", "crossfade"),
            onClick = { resetSearch(); navController.navigate("settings/player") },
        ),
        SettingsItem(
            icon = painterResource(R.drawable.storage),
            title = stringResource(R.string.storage),
            subtitle = stringResource(R.string.cache),
            accentColor = MaterialTheme.colorScheme.secondary,
            keywords = listOf("storage", "cache", "offline", "downloads", "cleanup"),
            onClick = { resetSearch(); navController.navigate("settings/storage") },
        ),
        SettingsItem(
            icon = painterResource(R.drawable.security),
            title = stringResource(R.string.privacy),
            subtitle = stringResource(R.string.pause_listen_history),
            accentColor = MaterialTheme.colorScheme.error,
            keywords = listOf("privacy", "history", "tracking", "security", "permissions"),
            onClick = { resetSearch(); navController.navigate("settings/privacy") },
        ),
        SettingsItem(
            icon = painterResource(R.drawable.restore),
            title = stringResource(R.string.backup_restore),
            subtitle = stringResource(R.string.backup),
            accentColor = MaterialTheme.colorScheme.tertiary,
            keywords = listOf("backup", "restore", "import", "export", "migration"),
            onClick = { resetSearch(); navController.navigate("settings/backup_restore") },
        ),
        SettingsItem(
            icon = painterResource(R.drawable.discord),
            title = stringResource(R.string.discord_integration),
            subtitle = stringResource(R.string.discord_integration),
            accentColor = Color(0xFF5865F2),
            keywords = listOf("discord", "rpc", "rich presence", "status", "activity"),
            onClick = { resetSearch(); navController.navigate("settings/discord") },
        ),
        SettingsItem(
            icon = painterResource(R.drawable.security),
            title = stringResource(R.string.advanced_login),
            subtitle = stringResource(R.string.discord),
            accentColor = Color(0xFF5865F2),
            keywords = listOf("token", "login", "authentication", "discord login"),
            onClick = { resetSearch(); navController.navigate("settings/discord/login") },
        )
    )
