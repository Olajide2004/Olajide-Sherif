package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AutoCompleteSuggestion
import com.example.engine.CodeAutoCompleteEngine
import com.example.model.AutosaveState
import com.example.model.CustomTypographyClass
import com.example.model.LoadedFileInfo
import com.example.ui.theme.SignalGold

@Composable
fun EditorView(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onInjectKeywordsClick: () -> Unit = {},
    onClearClick: () -> Unit,
    onCopyAllClick: () -> Unit,
    loadedFileInfo: LoadedFileInfo?,
    onOpenOsunhiveUi: () -> Unit = {},
    onOpenFileAutoTypeAndBatchPaste: () -> Unit = {},
    onOpenFilePickerToCode: () -> Unit = {},
    autosaveState: AutosaveState = AutosaveState.Idle,
    onSaveDraftNow: () -> Unit = {},
    customClasses: List<CustomTypographyClass> = emptyList(),
    onApplyAutoComplete: (AutoCompleteSuggestion) -> Unit = {},
    onApplyFormattingTag: (String, String) -> Unit = { _, _ -> },
    onApplyFloatingFormatting: (String) -> Unit = {},
    onOpenCustomClasses: () -> Unit = {},
    onOpenDocumentFormatter: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    onOpenChromeTab: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var editorFontSize by remember { mutableFloatStateOf(14f) }
    var showLineNumbers by remember { mutableStateOf(true) }
    var showSearchReplace by remember { mutableStateOf(false) }
    var showFloatingToolbar by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }

    val text = textFieldValue.text
    val wordCount = remember(text) {
        if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
    }
    val lineCount = remember(text) {
        if (text.isEmpty()) 0 else text.count { it == '\n' } + 1
    }
    val charCount = text.length
    val caretPos = textFieldValue.selection.start

    val autoCompleteSuggestions = remember(text, caretPos, customClasses) {
        CodeAutoCompleteEngine.getSuggestions(text, caretPos, customClasses)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("editor_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Control Bar: Title, Autosave status, File info & Primary Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = SignalGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OsunHive Code Workbench",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    // Autosave Status Badge
                    Surface(
                        onClick = onSaveDraftNow,
                        shape = RoundedCornerShape(10.dp),
                        color = when (autosaveState) {
                            is AutosaveState.Saving -> Color(0xFF3B2D0A)
                            is AutosaveState.Saved -> Color(0xFF10281E)
                            else -> MaterialTheme.colorScheme.surface
                        },
                        border = BorderStroke(
                            1.dp,
                            when (autosaveState) {
                                is AutosaveState.Saving -> SignalGold.copy(alpha = 0.7f)
                                is AutosaveState.Saved -> Color(0xFF22C55E).copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            }
                        ),
                        modifier = Modifier.testTag("badge_autosave")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        when (autosaveState) {
                                            is AutosaveState.Saving -> SignalGold
                                            is AutosaveState.Saved -> Color(0xFF22C55E)
                                            else -> MaterialTheme.colorScheme.outline
                                        },
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (autosaveState) {
                                    is AutosaveState.Saving -> "Saving..."
                                    is AutosaveState.Saved -> "Autosaved"
                                    else -> "Autosave ON"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when (autosaveState) {
                                    is AutosaveState.Saving -> SignalGold
                                    is AutosaveState.Saved -> Color(0xFF4ADE80)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${wordCount}w • ${charCount}c • ${lineCount}L",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Undo & Redo
                    IconButton(
                        onClick = onUndo,
                        enabled = canUndo,
                        modifier = Modifier.size(28.dp).testTag("action_undo")
                    ) {
                        Icon(
                            Icons.Default.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(16.dp),
                            tint = if (canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }

                    IconButton(
                        onClick = onRedo,
                        enabled = canRedo,
                        modifier = Modifier.size(28.dp).testTag("action_redo")
                    ) {
                        Icon(
                            Icons.Default.Redo,
                            contentDescription = "Redo",
                            modifier = Modifier.size(16.dp),
                            tint = if (canRedo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }

                    // Search & Replace Toggle
                    IconButton(
                        onClick = { showSearchReplace = !showSearchReplace },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search & Replace",
                            modifier = Modifier.size(16.dp),
                            tint = if (showSearchReplace) SignalGold else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Line Numbers Toggle
                    IconButton(
                        onClick = { showLineNumbers = !showLineNumbers },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text(
                            text = "#",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showLineNumbers) SignalGold else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Clear & Copy All
                    IconButton(onClick = onCopyAllClick, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy All", modifier = Modifier.size(16.dp))
                    }

                    IconButton(onClick = onClearClick, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Quick Tools Bar: Auto-Typing, File Picker to Code & OsunHive UI Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Direct File Picker to Code
                FilterChip(
                    selected = false,
                    onClick = onOpenFilePickerToCode,
                    label = { Text("📂 Open File to Code", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // File to Auto-Typing & Batch Paste Trigger
                FilterChip(
                    selected = true,
                    onClick = onOpenFileAutoTypeAndBatchPaste,
                    label = { Text("⚡ File Auto-Type / Batch Paste", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SignalGold,
                        selectedLabelColor = Color(0xFF1C2B2A)
                    )
                )

                // OsunHive UI Classes Dialog Trigger
                FilterChip(
                    selected = false,
                    onClick = onOpenOsunhiveUi,
                    label = { Text("OsunHive UI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Video Category Quick Chip
                FilterChip(
                    selected = false,
                    onClick = {
                        onApplyFormattingTag(
                            "<div class=\"videoYt\"><iframe src=\"https://www.youtube.com/embed/",
                            "\" allowfullscreen></iframe></div>\n"
                        )
                    },
                    label = { Text("🎬 Video (16:9)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Download Box Quick Chip
                FilterChip(
                    selected = false,
                    onClick = {
                        onApplyFormattingTag(
                            "<div class=\"dlBox\">\n  <div class=\"fT\" data-text=\"ZIP\"></div>\n  <div class=\"fN\"><span>File.zip</span><span class=\"fS\">12 MB</span></div>\n  <a class=\"button safeL\" href=\"",
                            "\" aria-label=\"Download\"><i class=\"icon dl\"></i></a>\n</div>\n"
                        )
                    },
                    label = { Text("📥 DlBox", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Safelink Quick Chip
                FilterChip(
                    selected = false,
                    onClick = { onApplyFormattingTag("<a class=\"button safeL\" href=\"", "\">Download</a>") },
                    label = { Text("🔒 SafeL", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Button Quick Chip
                FilterChip(
                    selected = false,
                    onClick = { onApplyFormattingTag("<a class=\"button\" href=\"", "\">Link</a>") },
                    label = { Text("🔘 Button", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Alert Info Quick Chip
                FilterChip(
                    selected = false,
                    onClick = { onApplyFormattingTag("<div class=\"alert info\"><strong>Info</strong> ", "</div>\n") },
                    label = { Text("⚠️ Alert", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Code Pre Quick Chip
                FilterChip(
                    selected = false,
                    onClick = { onApplyFormattingTag("<pre><code class=\"language-html\">", "</code></pre>\n") },
                    label = { Text("💻 Code", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Spoiler / Accordion Quick Chip
                FilterChip(
                    selected = false,
                    onClick = { onApplyFormattingTag("<details class=\"sp\"><summary>Click to reveal</summary><p>", "</p></details>\n") },
                    label = { Text("📑 Spoiler", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Steps List Quick Chip
                FilterChip(
                    selected = false,
                    onClick = { onApplyFormattingTag("<ol class=\"steps\">\n  <li>", "</li>\n</ol>\n") },
                    label = { Text("🔢 Steps", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Table Quick Chip
                FilterChip(
                    selected = false,
                    onClick = {
                        onApplyFormattingTag(
                            "<div class=\"table bordered stripped\">\n  <table>\n    <thead><tr><th>Name</th><th>Size</th></tr></thead>\n    <tbody><tr><td>",
                            "</td><td>12 MB</td></tr></tbody>\n  </table>\n</div>\n"
                        )
                    },
                    label = { Text("📊 Table", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Tabs Quick Chip
                FilterChip(
                    selected = false,
                    onClick = {
                        onApplyFormattingTag(
                            "<div class=\"tabs\">\n  <input id=\"t-0\" type=\"radio\" name=\"tabs1\" checked=\"checked\"/>\n  <input id=\"t-1\" type=\"radio\" name=\"tabs1\"/>\n  <div><label for=\"t-0\" data-text=\"Tab 1\"></label><label for=\"t-1\" data-text=\"Tab 2\"></label></div>\n  <div class=\"c-0\">",
                            "</div>\n  <div class=\"c-1\">Content 2</div>\n</div>\n"
                        )
                    },
                    label = { Text("🗂️ Tabs", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }

            // HTML Formatting Actions Row (Bold, Italic, H2, DropCap, Code, Kbd, etc.)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { onApplyFormattingTag("<b>", "</b>") }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.FormatBold, contentDescription = "Bold", modifier = Modifier.size(15.dp))
                }
                IconButton(onClick = { onApplyFormattingTag("<i>", "</i>") }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.FormatItalic, contentDescription = "Italic", modifier = Modifier.size(15.dp))
                }
                IconButton(onClick = { onApplyFormattingTag("<h2>", "</h2>\n") }, modifier = Modifier.size(28.dp)) {
                    Text("H2", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                IconButton(onClick = { onApplyFormattingTag("<p class=\"pIndent\">", "</p>\n") }, modifier = Modifier.size(28.dp)) {
                    Text("pIndent", fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
                }
                IconButton(onClick = { onApplyFormattingTag("<span class=\"dropCap\">", "</span>") }, modifier = Modifier.size(28.dp)) {
                    Text("DropCap", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SignalGold)
                }
                IconButton(onClick = { onApplyFormattingTag("<code>", "</code>") }, modifier = Modifier.size(28.dp)) {
                    Text("<code>", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
                IconButton(onClick = { onApplyFormattingTag("<kbd>", "</kbd>") }, modifier = Modifier.size(28.dp)) {
                    Text("<kbd>", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
                IconButton(onClick = { onApplyFormattingTag("<a class=\"extL\" href=\"https://", "\">Link</a>") }, modifier = Modifier.size(28.dp)) {
                    Text("extL", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SignalGold)
                }
                IconButton(onClick = { onApplyFormattingTag("<blockquote class=\"s1\"><p>", "</p><span>Author</span></blockquote>\n") }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.FormatQuote, contentDescription = "Quote", modifier = Modifier.size(15.dp))
                }
                IconButton(onClick = { onApplyFormattingTag("<hr/>\n", "") }, modifier = Modifier.size(28.dp)) {
                    Text("<hr/>", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Font Size controls
                Text("A-", fontSize = 11.sp, modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                IconButton(
                    onClick = { if (editorFontSize > 10f) editorFontSize -= 1f },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("-", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Text("${editorFontSize.toInt()}sp", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                IconButton(
                    onClick = { if (editorFontSize < 24f) editorFontSize += 1f },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("+", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Search & Replace Bar (Collapsible)
            if (showSearchReplace) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Find...", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f).height(38.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    OutlinedTextField(
                        value = replaceQuery,
                        onValueChange = { replaceQuery = it },
                        placeholder = { Text("Replace...", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f).height(38.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    TextButton(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                val replaced = textFieldValue.text.replace(searchQuery, replaceQuery)
                                onValueChange(TextFieldValue(replaced))
                            }
                        },
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Replace All", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SignalGold)
                    }

                    IconButton(onClick = { showSearchReplace = false }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Search", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Auto-complete suggestion chips bar
            if (autoCompleteSuggestions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SUGGESTIONS:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SignalGold,
                        fontFamily = FontFamily.Monospace
                    )

                    autoCompleteSuggestions.forEach { suggestion ->
                        FilterChip(
                            selected = false,
                            onClick = { onApplyAutoComplete(suggestion) },
                            label = {
                                Text(
                                    text = suggestion.displayLabel,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Main Code Editor Area with Floating Formatting Toolbar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Line Numbers Gutter + Monospace Code Editor
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF121918)) // Workbench Dark Paper Palette
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    if (showLineNumbers) {
                        val numbersString = remember(lineCount) { (1..lineCount.coerceAtLeast(1)).joinToString("\n") }
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .fillMaxHeight()
                                .padding(end = 6.dp)
                        ) {
                            Text(
                                text = numbersString,
                                color = Color(0xFF5A6B6A),
                                fontFamily = FontFamily.Monospace,
                                fontSize = editorFontSize.sp,
                                lineHeight = (editorFontSize * 1.55f).sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF23302E))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("main_code_editor_field"),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFE8ECEB),
                            fontFamily = FontFamily.Monospace,
                            fontSize = editorFontSize.sp,
                            lineHeight = (editorFontSize * 1.55f).sp,
                            letterSpacing = 0.2.sp
                        ),
                        cursorBrush = SolidColor(SignalGold),
                        decorationBox = { innerTextField ->
                            if (textFieldValue.text.isEmpty()) {
                                Text(
                                    text = "OsunHive Code Workbench ready.\nUse OsunHive UI buttons above or Typewriter Keyboard to compose post HTML...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF5E6E6D),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = editorFontSize.sp,
                                        lineHeight = (editorFontSize * 1.55f).sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // Floating Formatting Toolbar (Bold, Italic, Bullets, Numbers, Underline, H2, Code, Link, Save)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = showFloatingToolbar,
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { it / 2 }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xF2162220),
                            tonalElevation = 8.dp,
                            shadowElevation = 8.dp,
                            border = BorderStroke(1.dp, SignalGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .testTag("floating_formatting_toolbar")
                        ) {
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Formatting handle badge
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(SignalGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.FormatPaint,
                                    contentDescription = null,
                                    tint = SignalGold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))

                            // Bold Action
                            IconButton(
                                onClick = { onApplyFloatingFormatting("bold") },
                                modifier = Modifier.size(32.dp).testTag("floating_btn_bold")
                            ) {
                                Icon(Icons.Default.FormatBold, contentDescription = "Bold (<b>)", tint = Color.White, modifier = Modifier.size(18.dp))
                            }

                            // Italic Action
                            IconButton(
                                onClick = { onApplyFloatingFormatting("italic") },
                                modifier = Modifier.size(32.dp).testTag("floating_btn_italic")
                            ) {
                                Icon(Icons.Default.FormatItalic, contentDescription = "Italic (<i>)", tint = Color.White, modifier = Modifier.size(18.dp))
                            }

                            // Bullet Points Action
                            IconButton(
                                onClick = { onApplyFloatingFormatting("bullet") },
                                modifier = Modifier.size(32.dp).testTag("floating_btn_bullets")
                            ) {
                                Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet Points (<ul>)", tint = SignalGold, modifier = Modifier.size(18.dp))
                            }

                            // Numbered List Action
                            IconButton(
                                onClick = { onApplyFloatingFormatting("numbered") },
                                modifier = Modifier.size(32.dp).testTag("floating_btn_numbered")
                            ) {
                                Text("1.", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                            }

                            // Underline Action
                            IconButton(
                                onClick = { onApplyFloatingFormatting("underline") },
                                modifier = Modifier.size(32.dp).testTag("floating_btn_underline")
                            ) {
                                Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline (<u>)", tint = Color.White, modifier = Modifier.size(18.dp))
                            }

                            // Heading 2 Action
                            IconButton(
                                onClick = { onApplyFloatingFormatting("h2") },
                                modifier = Modifier.size(32.dp).testTag("floating_btn_h2")
                            ) {
                                Text("H2", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SignalGold)
                            }

                            // Code Action
                            IconButton(
                                onClick = { onApplyFloatingFormatting("code") },
                                modifier = Modifier.size(32.dp).testTag("floating_btn_code")
                            ) {
                                Icon(Icons.Default.Code, contentDescription = "Code (<code>)", tint = Color.White, modifier = Modifier.size(16.dp))
                            }

                            // Link Action
                            IconButton(
                                onClick = { onApplyFloatingFormatting("link") },
                                modifier = Modifier.size(32.dp).testTag("floating_btn_link")
                            ) {
                                Text("Link", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = SignalGold)
                            }

                            // Instant Save Draft Action
                            IconButton(
                                onClick = onSaveDraftNow,
                                modifier = Modifier.size(32.dp).testTag("floating_btn_save_draft")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Save Draft", tint = Color(0xFF4ADE80), modifier = Modifier.size(16.dp))
                            }

                            // Minimize/Hide Floating Toolbar
                            IconButton(
                                onClick = { showFloatingToolbar = false },
                                modifier = Modifier.size(28.dp).testTag("floating_btn_hide")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Hide Toolbar", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Small pill to restore floating toolbar if hidden
            if (!showFloatingToolbar) {
                SmallFloatingActionButton(
                        onClick = { showFloatingToolbar = true },
                        containerColor = SignalGold,
                        contentColor = Color(0xFF1C2B2A),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .testTag("floating_btn_restore")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Format", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

