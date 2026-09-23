package com.example

import android.os.Bundle
import android.system.Os
import java.io.File
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AutoTypeControlBar
import com.example.ui.components.BatchCopyDialog
import com.example.ui.components.BloggerMonetizationHubDialog
import com.example.ui.components.BloggerWebViewPane
import com.example.ui.components.ChromeBookmarkletDialog
import com.example.ui.components.CustomClassManagerDialog
import com.example.ui.components.DocumentToHtmlDialog
import com.example.ui.components.EditorView
import com.example.ui.components.FileAutoTypeBatchPasteDialog
import com.example.ui.components.KeyboardSetupBanner
import com.example.ui.components.KeyboardSetupDialog
import com.example.ui.components.PlusUiTypographyDialog
import com.example.ui.components.TemplatesAndFilesDialog
import com.example.ui.components.TypewriterKeyboard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SignalGold
import com.example.viewmodel.AppMode
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    companion object {
        init {
            try {
                Os.unsetenv("LIBGL_ALWAYS_SOFTWARE")
            } catch (_: Throwable) {
            }
        }
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEnvironmentAndCache()
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                BloggerAutoTyperApp(viewModel = viewModel)
            }
        }
    }

    private fun initEnvironmentAndCache() {
        try {
            Os.unsetenv("LIBGL_ALWAYS_SOFTWARE")
        } catch (_: Throwable) {
        }

        try {
            val baseCache = File(cacheDir, "WebView/Default/HTTP Cache")
            File(baseCache, "Code Cache/js").mkdirs()
            File(baseCache, "Code Cache/wasm").mkdirs()
            File(baseCache, "Cache_Data").mkdirs()
        } catch (_: Throwable) {
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloggerAutoTyperApp(viewModel: MainViewModel) {
    val editorValue by viewModel.editorValue.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val loadedFileInfo by viewModel.loadedFileInfo.collectAsState()
    val keyboardLayout by viewModel.keyboardLayout.collectAsState()
    val config by viewModel.config.collectAsState()

    val appMode by viewModel.appMode.collectAsState()
    val lastKeywords by viewModel.lastKeywords.collectAsState()
    val isWebAutoTyping by viewModel.isWebAutoTyping.collectAsState()
    val customClasses by viewModel.customClasses.collectAsState()

    // Typer engine telemetry
    val autoTypeState by viewModel.autoTyperEngine.state.collectAsState()
    val activeChar by viewModel.autoTyperEngine.activeChar.collectAsState()
    val countdownSec by viewModel.autoTyperEngine.countdownSec.collectAsState()
    val typedCount by viewModel.autoTyperEngine.typedCount.collectAsState()
    val totalCount by viewModel.autoTyperEngine.totalCount.collectAsState()
    val progressFraction by viewModel.autoTyperEngine.progressFraction.collectAsState()
    val elapsedSeconds by viewModel.autoTyperEngine.elapsedSeconds.collectAsState()
    val estimatedRemainingSeconds by viewModel.autoTyperEngine.estimatedRemainingSeconds.collectAsState()

    // Dialog visibility states
    var showTemplatesDialog by remember { mutableStateOf(false) }
    var showFileAutoTypeBatchDialog by remember { mutableStateOf(false) }
    var showPlusUiDialog by remember { mutableStateOf(false) }
    var showBatchCopyDialog by remember { mutableStateOf(false) }
    var showChromeBookmarkletDialog by remember { mutableStateOf(false) }
    var showKeyboardSetupDialog by remember { mutableStateOf(false) }
    var showCustomClassDialog by remember { mutableStateOf(false) }
    var showDocFormatterDialog by remember { mutableStateOf(false) }
    var showMonetizationDialog by remember { mutableStateOf(false) }
    var showKeyboard by remember { mutableStateOf(true) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.loadFileFromUri(it) }
    }

    // Document to HTML picker launcher (PDF, Word, Doc, TXT)
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.convertAndLoadDocument(it) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(SignalGold, androidx.compose.foundation.shape.RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "B",
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFF1C2B2A),
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Blogger Auto Typer",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (appMode == AppMode.BLOGGER_LIVE) "draft.blogger.com Direct Injection" else "Plus UI 3.7.0 Code Workbench",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFileAutoTypeBatchDialog = true },
                        modifier = Modifier.testTag("action_file_autotype_batch")
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = "File Auto-Typing & Batch Paste", tint = SignalGold)
                    }
                    IconButton(
                        onClick = { showPlusUiDialog = true },
                        modifier = Modifier.testTag("action_plus_ui_shortcodes")
                    ) {
                        Icon(Icons.Default.FormatPaint, contentDescription = "Plus UI 3.7.0 Shortcodes", tint = SignalGold)
                    }
                    IconButton(
                        onClick = { showDocFormatterDialog = true },
                        modifier = Modifier.testTag("action_doc_formatter")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "PDF / Docs Formatter", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = { showMonetizationDialog = true },
                        modifier = Modifier.testTag("action_monetization")
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Monetization & Traffic", tint = SignalGold)
                    }
                    IconButton(
                        onClick = { showKeyboardSetupDialog = true },
                        modifier = Modifier.testTag("action_keyboard_setup")
                    ) {
                        Icon(Icons.Default.Keyboard, contentDescription = "Enable Default Phone Keyboard")
                    }
                    IconButton(
                        onClick = { showChromeBookmarkletDialog = true },
                        modifier = Modifier.testTag("action_chrome_script")
                    ) {
                        Icon(Icons.Default.Code, contentDescription = "Chrome Bookmarklet / Script")
                    }
                    IconButton(
                        onClick = { showTemplatesDialog = true },
                        modifier = Modifier.testTag("action_load_file")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "Load Template")
                    }
                    IconButton(
                        onClick = { showBatchCopyDialog = true },
                        modifier = Modifier.testTag("action_batch_copy")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Blogger Batch Copy")
                    }
                    IconButton(
                        onClick = { viewModel.openBloggerInChromeTab() },
                        modifier = Modifier.testTag("action_open_blogger")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open draft.blogger.com in Chrome Tab", tint = SignalGold)
                    }
                    IconButton(
                        onClick = { viewModel.sharePostContent() },
                        modifier = Modifier.testTag("action_share")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Post")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mode Switcher Tabs
            TabRow(
                selectedTabIndex = if (appMode == AppMode.WORKBENCH) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = { tabPositions ->
                    val tabIdx = if (appMode == AppMode.WORKBENCH) 0 else 1
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabIdx]),
                        color = SignalGold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Tab(
                    selected = appMode == AppMode.WORKBENCH,
                    onClick = { viewModel.setAppMode(AppMode.WORKBENCH) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Post Workbench", fontWeight = if (appMode == AppMode.WORKBENCH) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.testTag("tab_workbench")
                )
                Tab(
                    selected = appMode == AppMode.BLOGGER_LIVE,
                    onClick = { viewModel.setAppMode(AppMode.BLOGGER_LIVE) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp), tint = SignalGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("draft.blogger.com", fontWeight = if (appMode == AppMode.BLOGGER_LIVE) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.testTag("tab_blogger_live")
                )
            }

            if (appMode == AppMode.WORKBENCH) {
                // Banner to enable typewriter keyboard as phone default
                KeyboardSetupBanner(
                    onOpenSetup = { showKeyboardSetupDialog = true },
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Top Section: Typer controls bar
                AutoTypeControlBar(
                    state = autoTypeState,
                    countdownSec = countdownSec,
                    typedCount = typedCount,
                    totalCount = totalCount,
                    progressFraction = progressFraction,
                    elapsedSeconds = elapsedSeconds,
                    estimatedRemainingSeconds = estimatedRemainingSeconds,
                    config = config,
                    onConfigChange = { viewModel.updateConfig(it) },
                    onStart = { viewModel.startAutoType() },
                    onPause = { viewModel.pauseAutoType() },
                    onResume = { viewModel.resumeAutoType() },
                    onStop = { viewModel.stopAutoType() },
                    onReset = { viewModel.resetAutoType() },
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Middle Section: Large Editor and Live Preview
                EditorView(
                    textFieldValue = editorValue,
                    onValueChange = { viewModel.onEditorChange(it) },
                    canUndo = canUndo,
                    canRedo = canRedo,
                    onUndo = { viewModel.undo() },
                    onRedo = { viewModel.redo() },
                    onInjectKeywordsClick = { showFileAutoTypeBatchDialog = true },
                    onOpenFileAutoTypeAndBatchPaste = { showFileAutoTypeBatchDialog = true },
                    onClearClick = { viewModel.clearEditor() },
                    onCopyAllClick = { viewModel.copyToClipboard(editorValue.text) },
                    loadedFileInfo = loadedFileInfo,
                    onOpenOsunhiveUi = { showPlusUiDialog = true },
                    customClasses = customClasses,
                    onApplyAutoComplete = { viewModel.applyAutoCompleteSuggestion(it) },
                    onApplyFormattingTag = { tag, attr -> viewModel.applyFormattingTag(tag, attr) },
                    onOpenCustomClasses = { showCustomClassDialog = true },
                    onOpenDocumentFormatter = { showDocFormatterDialog = true },
                    onOpenMonetizationHub = { showMonetizationDialog = true },
                    onOpenChromeTab = { viewModel.openBloggerInChromeTab() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 6.dp)
                )
            } else {
                // Live Blogger.com in-app browser with direct auto-typing and injection
                BloggerWebViewPane(
                    url = "https://draft.blogger.com/",
                    postText = editorValue.text,
                    keywordsText = lastKeywords,
                    isAutoTypingActive = isWebAutoTyping,
                    onStartWebAutoType = { wv -> viewModel.startWebAutoType(wv) },
                    onStopWebAutoType = { viewModel.stopWebAutoType() },
                    onOpenExternalChrome = { viewModel.openBloggerInChrome() },
                    onShowChromeBookmarklet = { showChromeBookmarkletDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 6.dp)
                )
            }

            // Toggle Keyboard visibility bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Typewriter Workbench Keyboard",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilledTonalButton(
                    onClick = { showKeyboard = !showKeyboard },
                    modifier = Modifier.height(28.dp).testTag("button_toggle_keyboard"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(
                        Icons.Default.Keyboard,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (showKeyboard) "Hide Keys" else "Show Keys",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Bottom Section: Tactile Typewriter Keyboard
            if (showKeyboard) {
                TypewriterKeyboard(
                    activeChar = activeChar,
                    currentLayout = keyboardLayout,
                    onLayoutChange = { viewModel.setKeyboardLayout(it) },
                    onKeyPress = { viewModel.typeText(it) },
                    onBackspace = { viewModel.backspace() },
                    onEnter = { viewModel.enter() },
                    onSpace = { viewModel.space() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Modal Dialogs
    if (showFileAutoTypeBatchDialog) {
        FileAutoTypeBatchPasteDialog(
            currentEditorText = editorValue.text,
            onDismiss = { showFileAutoTypeBatchDialog = false },
            onStartAutoType = { text, cfg ->
                viewModel.startAutoTypeCustomText(text, cfg)
                showFileAutoTypeBatchDialog = false
            },
            onBatchPasteToEditor = { chunk ->
                viewModel.insertHtmlAtCaret(chunk)
            },
            autoTypeState = autoTypeState,
            autoTypeProgress = progressFraction,
            onPauseAutoType = { viewModel.pauseAutoType() },
            onResumeAutoType = { viewModel.resumeAutoType() },
            onStopAutoType = { viewModel.stopAutoType() }
        )
    }

    if (showBatchCopyDialog) {
        BatchCopyDialog(
            fullText = editorValue.text,
            onDismiss = { showBatchCopyDialog = false },
            onCopyBatch = { batch ->
                viewModel.copyToClipboard(batch, "Blogger Post Batch")
            }
        )
    }

    if (showTemplatesDialog) {
        TemplatesAndFilesDialog(
            onDismiss = { showTemplatesDialog = false },
            onOpenFilePicker = {
                filePickerLauncher.launch(
                    arrayOf(
                        "text/*",
                        "text/html",
                        "text/plain",
                        "application/json",
                        "*/*"
                    )
                )
            },
            onSelectTemplate = { template ->
                viewModel.loadTemplate(template)
            }
        )
    }

    if (showChromeBookmarkletDialog) {
        ChromeBookmarkletDialog(
            currentPostContent = editorValue.text,
            onDismiss = { showChromeBookmarkletDialog = false },
            onCopyBookmarklet = { code ->
                viewModel.copyToClipboard(code, "Chrome Auto-Typer Bookmarklet")
            },
            onOpenChrome = {
                viewModel.openBloggerInChrome()
            }
        )
    }

    if (showKeyboardSetupDialog) {
        KeyboardSetupDialog(
            onDismiss = { showKeyboardSetupDialog = false }
        )
    }

    if (showPlusUiDialog) {
        PlusUiTypographyDialog(
            onDismiss = { showPlusUiDialog = false },
            onInsertSnippet = { snippet ->
                viewModel.typeText(snippet)
                showPlusUiDialog = false
            },
            onAutoTypeSnippet = { snippet ->
                viewModel.autoTypeSnippet(snippet)
                showPlusUiDialog = false
            }
        )
    }

    if (showCustomClassDialog) {
        CustomClassManagerDialog(
            customClasses = customClasses,
            onDismiss = { showCustomClassDialog = false },
            onAddClass = { viewModel.addCustomClass(it) },
            onDeleteClass = { viewModel.deleteCustomClass(it) },
            onInsertClass = { snippet ->
                viewModel.insertHtmlAtCaret(snippet)
                showCustomClassDialog = false
            },
            onAutoTypeClass = { snippet ->
                viewModel.autoTypeSnippet(snippet)
                showCustomClassDialog = false
            },
            onCopyCombinedCss = { viewModel.copyCombinedCss() }
        )
    }

    if (showDocFormatterDialog) {
        DocumentToHtmlDialog(
            onDismiss = { showDocFormatterDialog = false },
            onOpenFilePicker = {
                docPickerLauncher.launch(
                    arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "text/*",
                        "*/*"
                    )
                )
            },
            onInsertHtml = { html ->
                viewModel.insertHtmlAtCaret(html)
                showDocFormatterDialog = false
            },
            onAutoTypeHtml = { html ->
                viewModel.autoTypeSnippet(html)
                showDocFormatterDialog = false
            },
            onCopyHtml = { html ->
                viewModel.copyToClipboard(html, "Formatted Blogger HTML")
            }
        )
    }

    if (showMonetizationDialog) {
        BloggerMonetizationHubDialog(
            onDismiss = { showMonetizationDialog = false },
            onInsertSnippet = { snippet ->
                viewModel.insertHtmlAtCaret(snippet)
                showMonetizationDialog = false
            },
            onCopySnippet = { snippet ->
                viewModel.copyToClipboard(snippet, "Blogger Monetization & Video SEO")
            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

