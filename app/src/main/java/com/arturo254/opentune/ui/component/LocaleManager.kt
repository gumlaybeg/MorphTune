@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val localeManager = remember { LocaleManager.getInstance(context) }
    val hapticFeedback = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val currentLanguage by localeManager.currentLanguage.collectAsState()
    val changeState by localeManager.changeState.collectAsState()
    val availableLanguages by remember { derivedStateOf { localeManager.getAvailableLanguages() } }

    var selectedLanguageCode by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    val filteredLanguages = remember(availableLanguages, searchQuery) {
        if (searchQuery.isBlank()) {
            availableLanguages
        } else {
            availableLanguages.filter { language ->
                language.displayName.contains(searchQuery, ignoreCase = true) ||
                        language.nativeName.contains(searchQuery, ignoreCase = true) ||
                        language.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(selectedLanguageCode) {
        selectedLanguageCode?.let { languageCode ->
            if (localeManager.updateLanguage(languageCode)) {
                localeManager.restartApp(context)
            }
            selectedLanguageCode = null
        }
    }

    LaunchedEffect(filteredLanguages, currentLanguage) {
        val selectedIndex = filteredLanguages.indexOfFirst { it.code == currentLanguage }
        if (selectedIndex != -1) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            localeManager.resetChangeState()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .preventSheetSnapBack() // Handles horizontal and vertical scroll containment
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClear = {
                    searchQuery = ""
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                focusRequester = focusRequester,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )

            AnimatedVisibility(
                visible = changeState is LanguageChangeState.Changing ||
                        changeState is LanguageChangeState.Success,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ChangeStateIndicator(
                    isChanging = changeState is LanguageChangeState.Changing
                )
            }

            if (filteredLanguages.isEmpty()) {
                EmptySearchResult(modifier = Modifier.padding(vertical = 32.dp))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = filteredLanguages,
                        key = { it.code }
                    ) { language ->
                        val isSelected = language.code == currentLanguage
                        val isEnabled = changeState !is LanguageChangeState.Changing

                        LanguageItem(
                            language = language,
                            isSelected = isSelected,
                            isEnabled = isEnabled,
                            onClick = {
                                if (isEnabled && !isSelected) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    selectedLanguageCode = language.code
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
