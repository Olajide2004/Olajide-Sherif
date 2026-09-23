package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.ConversionOptions
import com.example.engine.DocumentFormattingPreset
import com.example.engine.DocumentToHtmlConverter
import com.example.ui.theme.SignalGold

@Composable
fun DocumentToHtmlDialog(
    onDismiss: () -> Unit,
    onOpenFilePicker: () -> Unit,
    onInsertHtml: (String) -> Unit,
    onAutoTypeHtml: (String) -> Unit,
    onCopyHtml: (String) -> Unit,
    initialDocumentText: String = "",
    initialDocumentName: String = "My Document"
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Input/File, 1: Live Preview
    var documentTitle by remember { mutableStateOf(initialDocumentName) }
    var rawText by remember { mutableStateOf(initialDocumentText) }

    var selectedPreset by remember { mutableStateOf(DocumentFormattingPreset.OSUNHIVE_MAGAZINE) }
    var addDropCap by remember { mutableStateOf(true) }
    var addLeadParagraph by remember { mutableStateOf(true) }
    var addJumpBreak by remember { mutableStateOf(true) }
    var addOsunhiveBacklink by remember { mutableStateOf(true) }
    var detectAlertBoxes by remember { mutableStateOf(true) }
    var detectCodeBlocks by remember { mutableStateOf(true) }

    val formattedHtml = remember(
        rawText,
        documentTitle,
        selectedPreset,
        addDropCap,
        addLeadParagraph,
        addJumpBreak,
        addOsunhiveBacklink,
        detectAlertBoxes,
        detectCodeBlocks
    ) {
        if (rawText.isBlank()) ""
        else {
            DocumentToHtmlConverter.convertDocumentToBloggerHtml(
                rawText = rawText,
                documentTitle = documentTitle,
                options = ConversionOptions(
                    preset = selectedPreset,
                    addDropCap = addDropCap,
                    addLeadParagraph = addLeadParagraph,
                    addJumpBreak = addJumpBreak,
                    addOsunhiveBacklink = addOsunhiveBacklink,
                    detectAlertBoxes = detectAlertBoxes,
                    detectCodeBlocks = detectCodeBlocks
                )
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .testTag("dialog_document_formatter"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SignalGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = SignalGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "PDF & Docs to Blogger Formatter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Convert PDF, Docs, and text to OsunHive UI HTML",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tabs: Source Input vs Converted HTML
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SignalGold
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("1. Document Input & Rules", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("2. Formatted HTML Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedTab == 0) {
                    // Input & Options Tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // File picker banner
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Import Document from Device", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Pick PDF, Word (.doc/.docx), TXT, or Markdown (.md)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = onOpenFilePicker,
                                    colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A)),
                                    modifier = Modifier.testTag("btn_pick_doc_file")
                                ) {
                                    Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pick File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Document Title
                        OutlinedTextField(
                            value = documentTitle,
                            onValueChange = { documentTitle = it },
                            label = { Text("Post Title", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Raw Text Editor / Pasting area
                        OutlinedTextField(
                            value = rawText,
                            onValueChange = { rawText = it },
                            label = { Text("Document Content / Paste Text Here", fontSize = 11.sp) },
                            placeholder = { Text("Paste document text, paragraphs, chapters, or notes here...", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        )

                        // Preset Style Selector
                        Text("OsunHive Typography Preset", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            DocumentPresetChip(
                                label = "Magazine & Blog",
                                selected = selectedPreset == DocumentFormattingPreset.OSUNHIVE_MAGAZINE,
                                onClick = { selectedPreset = DocumentFormattingPreset.OSUNHIVE_MAGAZINE },
                                modifier = Modifier.weight(1f)
                            )
                            DocumentPresetChip(
                                label = "Tech Tutorial",
                                selected = selectedPreset == DocumentFormattingPreset.TECH_TUTORIAL,
                                onClick = { selectedPreset = DocumentFormattingPreset.TECH_TUTORIAL },
                                modifier = Modifier.weight(1f)
                            )
                            DocumentPresetChip(
                                label = "Clean Blogger",
                                selected = selectedPreset == DocumentFormattingPreset.BLOGGER_CLEAN,
                                onClick = { selectedPreset = DocumentFormattingPreset.BLOGGER_CLEAN },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Formatting Rules Checkboxes
                        Text("Formatting Rules", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                CheckboxOption(
                                    label = "Add Drop Cap to first letter",
                                    checked = addDropCap,
                                    onCheckedChange = { addDropCap = it }
                                )
                                CheckboxOption(
                                    label = "Format first paragraph as Lead (.oh-lead)",
                                    checked = addLeadParagraph,
                                    onCheckedChange = { addLeadParagraph = it }
                                )
                                CheckboxOption(
                                    label = "Insert Blogger Jump Break (<!--more-->)",
                                    checked = addJumpBreak,
                                    onCheckedChange = { addJumpBreak = it }
                                )
                                CheckboxOption(
                                    label = "Detect Note / Warning lines and style as OsunHive Alerts",
                                    checked = detectAlertBoxes,
                                    onCheckedChange = { detectAlertBoxes = it }
                                )
                                CheckboxOption(
                                    label = "Detect code blocks and wrap in .oh-code-box",
                                    checked = detectCodeBlocks,
                                    onCheckedChange = { detectCodeBlocks = it }
                                )
                                CheckboxOption(
                                    label = "Include OsunHive UI traffic backlink (osunhive.name.ng)",
                                    checked = addOsunhiveBacklink,
                                    onCheckedChange = { addOsunhiveBacklink = it }
                                )
                            }
                        }
                    }
                } else {
                    // Preview Tab
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Generated Blogger HTML (${formattedHtml.length} chars)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = formattedHtml.ifEmpty { "Enter document text or pick a file to view formatted HTML." },
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onCopyHtml(formattedHtml) },
                        enabled = formattedHtml.isNotEmpty(),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy HTML", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = { onInsertHtml(formattedHtml) },
                        enabled = formattedHtml.isNotEmpty(),
                        modifier = Modifier.weight(1.2f).height(38.dp).testTag("btn_insert_doc_html")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Insert in Post", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    ElevatedButton(
                        onClick = { onAutoTypeHtml(formattedHtml) },
                        enabled = formattedHtml.isNotEmpty(),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A)),
                        modifier = Modifier.weight(1.3f).height(38.dp).testTag("btn_autotype_doc_html")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-Type HTML", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentPresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (selected) SignalGold else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color(0xFF1C2B2A) else MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
private fun CheckboxOption(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 12.sp)
    }
}
