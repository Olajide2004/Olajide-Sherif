package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AutoCompleteSuggestion
import com.example.engine.CodeAutoCompleteEngine
import com.example.model.CustomTypographyClass
import com.example.model.LoadedFileInfo
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SignalGold

@Composable
fun EditorView(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onInjectKeywordsClick: () -> Unit,
    onClearClick: () -> Unit,
    onCopyAllClick: () -> Unit,
    loadedFileInfo: LoadedFileInfo?,
    onOpenOsunhiveUi: () -> Unit = {},
    customClasses: List<CustomTypographyClass> = emptyList(),
    onApplyAutoComplete: (AutoCompleteSuggestion) -> Unit = {},
    onApplyFormattingTag: (String, String) -> Unit = { _, _ -> },
    onOpenCustomClasses: () -> Unit = {},
    onOpenDocumentFormatter: () -> Unit = {},
    onOpenMonetizationHub: () -> Unit = {},
    onOpenChromeTab: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: HTML/Code, 1: Compose (Visual), 2: Rendered Preview
    var editorFontSize by remember { mutableFloatStateOf(14f) }
    var showLineNumbers by remember { mutableStateOf(false) }

    val text = textFieldValue.text
    val charCount = text.length
    val wordCount = remember(text) {
        if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
    }
    val lineCount = remember(text) {
        if (text.isEmpty()) 0 else text.count { it == '\n' } + 1
    }
    val readTimeMin = remember(wordCount) { (wordCount / 200).coerceAtLeast(1) }
    val caretPos = textFieldValue.selection.start

    // Dynamic autocomplete suggestions based on current caret and text
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
            // Header Bar: 3 Tabs & File info & Undo/Redo actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // View toggle tabs (HTML Code vs Compose Visual vs Rendered Preview)
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.weight(1f),
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SignalGold
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("HTML Code", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
                        modifier = Modifier.testTag("tab_editor_html")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Compose (Visual)", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
                        modifier = Modifier.testTag("tab_editor_compose")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Preview", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
                        modifier = Modifier.testTag("tab_editor_preview")
                    )
                }

                // Editor Quick Action Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onUndo,
                        enabled = canUndo,
                        modifier = Modifier.size(28.dp).testTag("button_undo")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(16.dp),
                            tint = if (canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    IconButton(
                        onClick = onRedo,
                        enabled = canRedo,
                        modifier = Modifier.size(28.dp).testTag("button_redo")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            modifier = Modifier.size(16.dp),
                            tint = if (canRedo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    IconButton(
                        onClick = onOpenCustomClasses,
                        modifier = Modifier.size(28.dp).testTag("button_custom_classes")
                    ) {
                        Icon(Icons.Default.FormatPaint, contentDescription = "Custom Classes", tint = SignalGold, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onOpenDocumentFormatter,
                        modifier = Modifier.size(28.dp).testTag("button_doc_formatter")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "PDF / Docs Formatter", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onOpenMonetizationHub,
                        modifier = Modifier.size(28.dp).testTag("button_monetization_hub")
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Monetization Hub", tint = SignalGold, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onCopyAllClick,
                        modifier = Modifier.size(28.dp).testTag("button_copy_all")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy all", modifier = Modifier.size(15.dp))
                    }
                    IconButton(
                        onClick = onClearClick,
                        modifier = Modifier.size(28.dp).testTag("button_clear")
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear editor", tint = AlertRed.copy(alpha = 0.8f), modifier = Modifier.size(15.dp))
                    }
                }
            }

            // OsunHive Traffic & Monetization Top Pill Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .clickable { onOpenMonetizationHub() }
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = SignalGold, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "OsunHive UI • Free Blogger Themes & Monetization (osunhive.name.ng)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF1F5F9)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("t.me/Osunhive ↗", fontSize = 10.sp, color = SignalGold, fontWeight = FontWeight.Bold)
                }
            }

            // Loaded file badge if available
            if (loadedFileInfo != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SignalGold.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DataObject,
                        contentDescription = null,
                        tint = SignalGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${loadedFileInfo.name} (${loadedFileInfo.type}) • ${loadedFileInfo.characterCount} chars",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Blogger Compose Formatting Toolbar (Visible in Compose Mode)
            if (selectedTab == 1) {
                BloggerComposeToolbar(
                    onApplyTag = onApplyFormattingTag,
                    onOpenOsunhiveUi = onOpenOsunhiveUi,
                    onOpenCustomClasses = onOpenCustomClasses,
                    customClasses = customClasses
                )
            }

            // Auto-Complete Code Strip (Visible in HTML/Code Mode)
            if (selectedTab == 0) {
                CodeAutoCompleteStrip(
                    suggestions = autoCompleteSuggestions,
                    onSelect = onApplyAutoComplete
                )
            }

            HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Body Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // HTML / Code Workbench Field - comfortable, eye-safe code typing
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                if (showLineNumbers) {
                                    val numbersString = remember(lineCount) { (1..lineCount.coerceAtLeast(1)).joinToString("\n") }
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .fillMaxHeight()
                                            .padding(end = 4.dp)
                                    ) {
                                        Text(
                                            text = numbersString,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = editorFontSize.sp,
                                            lineHeight = (editorFontSize * 1.55f).sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(0.8.dp)
                                            .fillMaxHeight()
                                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                BasicTextField(
                                    value = textFieldValue,
                                    onValueChange = onValueChange,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .testTag("main_editor_field"),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = editorFontSize.sp,
                                        lineHeight = (editorFontSize * 1.55f).sp,
                                        letterSpacing = 0.3.sp
                                    ),
                                    cursorBrush = SolidColor(SignalGold),
                                    decorationBox = { innerTextField ->
                                        if (textFieldValue.text.isEmpty()) {
                                            Text(
                                                text = "Ready to code. Use auto-complete chips above or typewriter keyboard to compose Blogger HTML...",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = editorFontSize.sp
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }
                    }
                    1 -> {
                        // Compose (Visual Mode): tuned for comfortable long-form prose and essay writing
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "Visual Long-Form Mode • Select text & apply typography classes above",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                if (showLineNumbers) {
                                    val numbersString = remember(lineCount) { (1..lineCount.coerceAtLeast(1)).joinToString("\n") }
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .fillMaxHeight()
                                            .padding(end = 4.dp)
                                    ) {
                                        Text(
                                            text = numbersString,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = editorFontSize.sp,
                                            lineHeight = (editorFontSize * 1.55f).sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(0.8.dp)
                                            .fillMaxHeight()
                                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                BasicTextField(
                                    value = textFieldValue,
                                    onValueChange = onValueChange,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .testTag("compose_editor_field"),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontFamily = FontFamily.Default,
                                        fontSize = (editorFontSize + 0.5f).sp,
                                        lineHeight = ((editorFontSize + 0.5f) * 1.55f).sp,
                                        letterSpacing = 0.2.sp
                                    ),
                                    cursorBrush = SolidColor(SignalGold),
                                    decorationBox = { innerTextField ->
                                        if (textFieldValue.text.isEmpty()) {
                                            Text(
                                                text = "Compose your Blogspot post here. Highlight text and tap Bold, Heading, Alert, or OsunHive UI classes above to format...",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                                    fontSize = (editorFontSize + 0.5f).sp,
                                                    lineHeight = ((editorFontSize + 0.5f) * 1.55f).sp
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }
                    }
                    else -> {
                        // Blogger Rendered Preview
                        BloggerRenderedPreview(
                            htmlContent = text,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Footer Status Bar with live counters, typography sizing, line numbers & Chrome tab shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$charCount ch • $wordCount w (~${readTimeMin}m read)",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Line numbers toggle
                    Box(
                        modifier = Modifier
                            .clickable { showLineNumbers = !showLineNumbers }
                            .background(
                                if (showLineNumbers) SignalGold.copy(alpha = 0.25f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .border(0.5.dp, if (showLineNumbers) SignalGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "L:$lineCount",
                            fontSize = 9.5.sp,
                            fontWeight = if (showLineNumbers) FontWeight.Bold else FontWeight.Normal,
                            color = if (showLineNumbers) SignalGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Font Size Stepper for comfortable reading
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 2.dp, vertical = 1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable { if (editorFontSize > 11f) editorFontSize -= 1f }
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text("A-", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(
                            "${editorFontSize.toInt()}sp",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                        Box(
                            modifier = Modifier
                                .clickable { if (editorFontSize < 22f) editorFontSize += 1f }
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text("A+", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clickable { onOpenChromeTab() }
                            .background(SignalGold.copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = SignalGold, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Chrome", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Auto-Complete Code Strip displayed above code workbench
 */
@Composable
private fun CodeAutoCompleteStrip(
    suggestions: List<AutoCompleteSuggestion>,
    onSelect: (AutoCompleteSuggestion) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(scrollState)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SignalGold, modifier = Modifier.size(12.dp))
        Text("Auto-Complete:", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)

        for (sug in suggestions) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                    .clickable { onSelect(sug) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = sug.displayLabel,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Blogger Compose Formatting Toolbar with OsunHive UI & Custom Classes
 */
@Composable
private fun BloggerComposeToolbar(
    onApplyTag: (String, String) -> Unit,
    onOpenOsunhiveUi: () -> Unit,
    onOpenCustomClasses: () -> Unit,
    customClasses: List<CustomTypographyClass>
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .horizontalScroll(scrollState)
            .padding(horizontal = 4.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Headings
        ComposeFormatChip(text = "Major H2", onClick = { onApplyTag("h2", "class=\"oh-h2\"") })
        ComposeFormatChip(text = "Sub H3", onClick = { onApplyTag("h3", "class=\"oh-h3\"") })
        ComposeFormatChip(text = "Lead Para", onClick = { onApplyTag("p", "class=\"oh-lead\"") })

        // Inline formatting
        ComposeFormatIcon(icon = Icons.Default.FormatBold, desc = "Bold", onClick = { onApplyTag("strong", "") })
        ComposeFormatIcon(icon = Icons.Default.FormatItalic, desc = "Italic", onClick = { onApplyTag("em", "") })
        ComposeFormatIcon(icon = Icons.Default.FormatUnderlined, desc = "Underline", onClick = { onApplyTag("u", "") })
        ComposeFormatIcon(icon = Icons.Default.FormatStrikethrough, desc = "Strikethrough", onClick = { onApplyTag("s", "") })

        // Blogger jump break & elements
        ComposeFormatChip(text = "<!--more-->", isGold = true, onClick = { onApplyTag("!--more--", "") })
        ComposeFormatIcon(icon = Icons.Default.FormatQuote, desc = "Quote", onClick = { onApplyTag("blockquote", "class=\"oh-blockquote\"") })
        ComposeFormatChip(text = "Highlight", onClick = { onApplyTag("mark", "class=\"oh-mark\"") })
        ComposeFormatChip(text = "Divider", onClick = { onApplyTag("hr", "") })

        // OsunHive UI Alerts
        ComposeFormatChip(text = "ℹ️ Alert", onClick = { onApplyTag("div", "class=\"oh-alert oh-alert-info\"") })
        ComposeFormatChip(text = "✅ Success", onClick = { onApplyTag("div", "class=\"oh-alert oh-alert-success\"") })
        ComposeFormatChip(text = "⚠️ Warning", onClick = { onApplyTag("div", "class=\"oh-alert oh-alert-warning\"") })
        ComposeFormatChip(text = "CTA Button", onClick = { onApplyTag("a", "href=\"#\" class=\"oh-btn\"") })

        // Custom Classes Button
        Box(
            modifier = Modifier
                .background(SignalGold, RoundedCornerShape(4.dp))
                .clickable(onClick = onOpenCustomClasses)
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text("+ Custom Classes (${customClasses.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2B2A))
        }

        // Full Osunhive dialog
        Box(
            modifier = Modifier
                .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                .clickable(onClick = onOpenOsunhiveUi)
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text("OsunHive UI CSS ▾", fontSize = 10.sp, color = SignalGold, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ComposeFormatChip(
    text: String,
    onClick: () -> Unit,
    isGold: Boolean = false
) {
    Box(
        modifier = Modifier
            .background(
                if (isGold) SignalGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = if (isGold) FontWeight.Bold else FontWeight.Medium,
            color = if (isGold) SignalGold else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ComposeFormatIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    desc: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = desc, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun BloggerRenderedPreview(
    htmlContent: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (htmlContent.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Preview is empty. Load or type HTML content to see rendered Blogger post view.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
        } else {
            // Strip or parse common HTML markers for clean live preview
            val lines = remember(htmlContent) { htmlContent.split("\n") }

            for (line in lines) {
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("<!--") && trimmed.endsWith("-->") -> {
                        // Blogger comment / labels / meta
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F0FE), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = trimmed.removeSurrounding("<!--", "-->").trim(),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF174EA6)
                            )
                        }
                    }
                    // Osunhive UI Alerts
                    trimmed.contains("oh-alert-info") -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEBF5FB), RoundedCornerShape(8.dp))
                                .border(width = 2.dp, color = Color(0xFF2980B9), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text("ℹ️", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = stripTags(trimmed),
                                    fontSize = 13.sp,
                                    color = Color(0xFF1B4F72),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    trimmed.contains("oh-alert-success") -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEAFAF1), RoundedCornerShape(8.dp))
                                .border(width = 2.dp, color = Color(0xFF27AE60), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text("✅", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = stripTags(trimmed),
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E8449),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    trimmed.contains("oh-alert-warning") -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEF9E7), RoundedCornerShape(8.dp))
                                .border(width = 2.dp, color = Color(0xFFF39C12), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text("⚠️", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = stripTags(trimmed),
                                    fontSize = 13.sp,
                                    color = Color(0xFF7D6608),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    trimmed.contains("oh-alert-danger") -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFDEDEC), RoundedCornerShape(8.dp))
                                .border(width = 2.dp, color = Color(0xFFE74C3C), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text("⛔", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = stripTags(trimmed),
                                    fontSize = 13.sp,
                                    color = Color(0xFF78281F),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    // Osunhive UI Buttons
                    trimmed.contains("oh-btn") -> {
                        val isDownload = trimmed.contains("oh-btn-download")
                        val isDemo = trimmed.contains("oh-btn-demo")
                        val isOutline = trimmed.contains("oh-btn-outline")
                        val bgColor = when {
                            isDownload -> Color(0xFF10B981)
                            isDemo -> Color(0xFF8B5CF6)
                            isOutline -> Color.Transparent
                            else -> SignalGold
                        }
                        val textColor = if (isOutline) SignalGold else if (isDownload || isDemo) Color.White else Color(0xFF1C2B2A)

                        Box(
                            modifier = Modifier
                                .background(bgColor, RoundedCornerShape(6.dp))
                                .then(if (isOutline) Modifier.border(1.5.dp, SignalGold, RoundedCornerShape(6.dp)) else Modifier)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stripTags(trimmed).ifEmpty { if (isDownload) "Download" else if (isDemo) "Demo" else "Action Button" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                    // Osunhive UI Code Box
                    trimmed.contains("oh-code-box") || trimmed.contains("<pre") -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("CODE SNIPPET", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SignalGold, fontFamily = FontFamily.Monospace)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stripTags(trimmed),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                    // Osunhive UI Accordion
                    trimmed.contains("oh-accordion") || trimmed.contains("<details") -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .border(1.dp, SignalGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("▶", fontSize = 10.sp, color = SignalGold, modifier = Modifier.padding(end = 6.dp))
                                    Text(
                                        text = stripTags(trimmed),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    // Osunhive UI DropCap
                    trimmed.contains("oh-dropcap") -> {
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                            val dropCapLetter = trimmed.substringAfter("oh-dropcap\">", "").substringBefore("</span>", "").ifEmpty { "A" }
                            val restOfText = stripTags(trimmed).removePrefix(dropCapLetter)
                            Text(
                                text = dropCapLetter,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Serif,
                                color = SignalGold,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = restOfText,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    trimmed.startsWith("<h2>") || trimmed.contains("</h2>") -> {
                        Column(modifier = Modifier.padding(top = 4.dp)) {
                            Text(
                                text = stripTags(trimmed),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (trimmed.contains("oh-h2")) SignalGold else Color(0xFF1A73E8)
                            )
                            if (trimmed.contains("oh-h2")) {
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(3.dp)
                                        .background(SignalGold, RoundedCornerShape(2.dp))
                                        .padding(top = 2.dp)
                                )
                            }
                        }
                    }
                    trimmed.startsWith("<h3>") || trimmed.contains("</h3>") -> {
                        Text(
                            text = stripTags(trimmed),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    trimmed.contains("<blockquote") || trimmed.contains("</blockquote>") -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                .border(width = 3.dp, color = SignalGold, shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = stripTags(trimmed),
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    trimmed.startsWith("<hr") -> {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    trimmed.isNotEmpty() -> {
                        // Render styled text with highlights
                        val annotated = buildAnnotatedString {
                            var remaining = trimmed
                            // Simple highlighting of <mark>
                            val markStart = remaining.indexOf("<mark")
                            if (markStart != -1) {
                                val textBefore = remaining.substring(0, markStart)
                                append(stripTags(textBefore))

                                val contentStart = remaining.indexOf(">", markStart)
                                val markEnd = remaining.indexOf("</mark>", contentStart)
                                if (contentStart != -1 && markEnd != -1) {
                                    val markText = remaining.substring(contentStart + 1, markEnd)
                                    withStyle(
                                        SpanStyle(
                                            background = Color(0xFFFFF9C4),
                                            color = Color(0xFF1C2B2A),
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(markText)
                                    }
                                    remaining = remaining.substring(markEnd + 7)
                                }
                            }
                            append(stripTags(remaining))
                        }
                        Text(
                            text = annotated,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private fun stripTags(html: String): String {
    return html.replace(Regex("<[^>]*>"), "")
}
