package com.arturo254.opentune.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.arturo254.opentune.NotificationPermissionPreference
import com.arturo254.opentune.R
import com.arturo254.opentune.constants.ContentCountryKey
import com.arturo254.opentune.constants.ContentLanguageKey
import com.arturo254.opentune.constants.CountryCodeToName
import com.arturo254.opentune.constants.DefaultLyricsProviderPriority
import com.arturo254.opentune.constants.EnableKugouKey
import com.arturo254.opentune.constants.EnableLrcLibKey
import com.arturo254.opentune.constants.EnableLyricsPlusKey
import com.arturo254.opentune.constants.EnablePaxsenixKey
import com.arturo254.opentune.constants.HideExplicitKey
import com.arturo254.opentune.constants.HistoryDuration
import com.arturo254.opentune.constants.LanguageCodeToName
import com.arturo254.opentune.constants.LyricsProviderPriorityKey
import com.arturo254.opentune.constants.ProxyEnabledKey
import com.arturo254.opentune.constants.ProxyTypeKey
import com.arturo254.opentune.constants.ProxyUrlKey
import com.arturo254.opentune.constants.QuickPicks
import com.arturo254.opentune.constants.QuickPicksKey
import com.arturo254.opentune.constants.RomanizeLyricsKey
import com.arturo254.opentune.constants.SYSTEM_DEFAULT
import com.arturo254.opentune.constants.TopSize
import com.arturo254.opentune.ui.component.DefaultDialog
import com.arturo254.opentune.ui.component.EditTextPreference
import com.arturo254.opentune.ui.component.ListPreference
import com.arturo254.opentune.ui.component.PreferenceEntry
import com.arturo254.opentune.ui.component.SettingsGeneralCategory
import com.arturo254.opentune.ui.component.SettingsPage
import com.arturo254.opentune.ui.component.SliderPreference
import com.arturo254.opentune.ui.component.SwitchPreference
import com.arturo254.opentune.utils.rememberEnumPreference
import com.arturo254.opentune.utils.rememberPreference
import java.net.Proxy
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (contentLanguage, onContentLanguageChange) = rememberPreference(
        key = ContentLanguageKey,
        defaultValue = "system"
    )
    val (contentCountry, onContentCountryChange) = rememberPreference(
        key = ContentCountryKey,
        defaultValue = "system"
    )
    val (hideExplicit, onHideExplicitChange) = rememberPreference(
        key = HideExplicitKey,
        defaultValue = false
    )
    val (proxyEnabled, onProxyEnabledChange) = rememberPreference(
        key = ProxyEnabledKey,
        defaultValue = false
    )
    val (proxyType, onProxyTypeChange) = rememberEnumPreference(
        key = ProxyTypeKey,
        defaultValue = Proxy.Type.HTTP
    )
    val (proxyUrl, onProxyUrlChange) = rememberPreference(
        key = ProxyUrlKey,
        defaultValue = "host:port"
    )
    val (lengthTop, onLengthTopChange) = rememberPreference(
        key = TopSize,
        defaultValue = "50"
    )
    val (historyDuration, onHistoryDurationChange) = rememberPreference(
        key = HistoryDuration,
        defaultValue = 30f
    )
    val (quickPicks, onQuickPicksChange) = rememberEnumPreference(
        key = QuickPicksKey,
        defaultValue = QuickPicks.QUICK_PICKS
    )
    val (enableKugou, onEnableKugouChange) = rememberPreference(
        key = EnableKugouKey,
        defaultValue = true
    )
    val (enableLrclib, onEnableLrclibChange) = rememberPreference(
        key = EnableLrcLibKey,
        defaultValue = true
    )
    val (enableLyricsPlus, onEnableLyricsPlusChange) = rememberPreference(
        key = EnableLyricsPlusKey,
        defaultValue = true
    )
    val (enablePaxsenix, onEnablePaxsenixChange) = rememberPreference(
        key = EnablePaxsenixKey,
        defaultValue = true
    )
    val (romanizeLyrics, onRomanizeLyricsChange) = rememberPreference(
        key = RomanizeLyricsKey,
        defaultValue = false
    )
    val (providerPriority, onProviderPriorityChange) = rememberPreference(
        key = LyricsProviderPriorityKey,
        defaultValue = DefaultLyricsProviderPriority
    )

    var showPriorityDialog by remember { mutableStateOf(false) }

    if (showPriorityDialog) {
        var currentOrder by remember { mutableStateOf(providerPriority.split(",")) }
        val listState = rememberLazyListState()
        val reorderableState = rememberReorderableLazyListState(
            lazyListState = listState,
            onMove = { from, to ->
                currentOrder = currentOrder.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                }
            }
        )

        DefaultDialog(
            onDismiss = { showPriorityDialog = false },
            title = { Text("Lyrics Provider Priority") },
            buttons = {
                TextButton(onClick = { showPriorityDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
                TextButton(onClick = {
                    onProviderPriorityChange(currentOrder.joinToString(","))
                    showPriorityDialog = false
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(currentOrder, key = { it }) { provider ->
                    ReorderableItem(state = reorderableState, key = provider) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                                .draggableHandle(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painterResource(R.drawable.drag_handle), null)
                            Spacer(Modifier.width(16.dp))
                            Text(provider, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }

    SettingsPage(
        title = stringResource(R.string.content),
        navController = navController,
        scrollBehavior = scrollBehavior
    ) {
        // General settings
        SettingsGeneralCategory(
            title = stringResource(R.string.general),
            items = listOf(
                {ListPreference(
                    title = { Text(stringResource(R.string.content_language)) },
                    icon = { Icon(painterResource(R.drawable.language), null) },
                    selectedValue = contentLanguage,
                    values = listOf(SYSTEM_DEFAULT) + LanguageCodeToName.keys.toList(),
                    valueText = {
                        LanguageCodeToName.getOrElse(it) { stringResource(R.string.system_default) }
                    },
                    onValueSelected = onContentLanguageChange,
                )},
                {ListPreference(
                    title = { Text(stringResource(R.string.content_country)) },
                    icon = { Icon(painterResource(R.drawable.location_on), null) },
                    selectedValue = contentCountry,
                    values = listOf(SYSTEM_DEFAULT) + CountryCodeToName.keys.toList(),
                    valueText = {
                        CountryCodeToName.getOrElse(it) { stringResource(R.string.system_default) }
                    },
                    onValueSelected = onContentCountryChange,
                )},

                // Hide explicit content
                {SwitchPreference(
                    title = { Text(stringResource(R.string.hide_explicit)) },
                    icon = { Icon(painterResource(R.drawable.explicit), null) },
                    checked = hideExplicit,
                    onCheckedChange = onHideExplicitChange,
                )},

                {NotificationPermissionPreference()},
            )
        )

        // Proxy settings
        SettingsGeneralCategory(
            title = stringResource(R.string.proxy),
            items = listOf(
                {SwitchPreference(
                    title = { Text(stringResource(R.string.enable_proxy)) },
                    icon = { Icon(painterResource(R.drawable.wifi_proxy), null) },
                    checked = proxyEnabled,
                    onCheckedChange = onProxyEnabledChange,
                )},
                {if (proxyEnabled) {
                    Column {
                        ListPreference(
                            title = { Text(stringResource(R.string.proxy_type)) },
                            selectedValue = proxyType,
                            values = listOf(Proxy.Type.HTTP, Proxy.Type.SOCKS),
                            valueText = { it.name },
                            onValueSelected = onProxyTypeChange,
                        )
                        EditTextPreference(
                            title = { Text(stringResource(R.string.proxy_url)) },
                            value = proxyUrl,
                            onValueChange = onProxyUrlChange,
                        )
                    }
                }}
            )
        )

        // Lyrics settings
        SettingsGeneralCategory(
            title = stringResource(R.string.lyrics),
            items = listOf(
                {PreferenceEntry(
                    title = { Text("Lyrics Provider Priority") },
                    description = providerPriority.replace(",", " > "),
                    icon = { Icon(painterResource(R.drawable.list), null) },
                    onClick = { showPriorityDialog = true }
                )},
                {SwitchPreference(
                    title = { Text("Auto-Romanize Lyrics") },
                    icon = { Icon(painterResource(R.drawable.translate), null) },
                    checked = romanizeLyrics,
                    onCheckedChange = onRomanizeLyricsChange,
                )},
                {SwitchPreference(
                    title = { Text("Enable LrcLib") },
                    icon = { Icon(painterResource(R.drawable.lyrics), null) },
                    checked = enableLrclib,
                    onCheckedChange = onEnableLrclibChange,
                )},
                {SwitchPreference(
                    title = { Text("Enable KuGou") },
                    icon = { Icon(painterResource(R.drawable.lyrics), null) },
                    checked = enableKugou,
                    onCheckedChange = onEnableKugouChange,
                )},
                {SwitchPreference(
                    title = { Text("Enable LyricsPlus") },
                    icon = { Icon(painterResource(R.drawable.lyrics), null) },
                    checked = enableLyricsPlus,
                    onCheckedChange = onEnableLyricsPlusChange,
                )},
                {SwitchPreference(
                    title = { Text("Enable Paxsenix") },
                    icon = { Icon(painterResource(R.drawable.lyrics), null) },
                    checked = enablePaxsenix,
                    onCheckedChange = onEnablePaxsenixChange,
                )},
            )
        )

        // Misc settings
        SettingsGeneralCategory(
            title = stringResource(R.string.misc),
            items = listOf(
                {EditTextPreference(
                    title = { Text(stringResource(R.string.top_length)) },
                    icon = { Icon(painterResource(R.drawable.trending_up), null) },
                    value = lengthTop,
                    isInputValid = { it.toIntOrNull()?.let { num -> num > 0 } == true },
                    onValueChange = onLengthTopChange,
                )},
                {ListPreference(
                    title = { Text(stringResource(R.string.set_quick_picks)) },
                    icon = { Icon(painterResource(R.drawable.home_outlined), null) },
                    selectedValue = quickPicks,
                    values = listOf(QuickPicks.QUICK_PICKS, QuickPicks.LAST_LISTEN),
                    valueText = {
                        when (it) {
                            QuickPicks.QUICK_PICKS -> stringResource(R.string.quick_picks)
                            QuickPicks.LAST_LISTEN -> stringResource(R.string.last_song_listened)
                        }
                    },
                    onValueSelected = onQuickPicksChange,
                )},
                {SliderPreference(
                    title = { Text(stringResource(R.string.history_duration)) },
                    icon = { Icon(painterResource(R.drawable.history), null) },
                    value = historyDuration,
                    onValueChange = onHistoryDurationChange,
                )},
            )
        )
    }
}
