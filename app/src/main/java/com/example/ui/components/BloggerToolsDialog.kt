package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatchCopyInfo
import com.example.model.BloggerPresets
import com.example.model.SnippetItem
import com.example.ui.theme.SignalGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeywordInjectorDialog(
    onDismiss: () -> Unit,
    onInjectKeywords: (keywords: String, mode: InjectionMode) -> Unit
) {
    var keywordText by remember { mutableStateOf("auto typing keywords, blogger html, content injection, android blogging") }
    var selectedMode by remember { mutableStateOf(InjectionMode.AT_CARET) }

    val presetKeywords = listOf(
        "auto typing keywords",
        "blogger html injection",
        "seo meta tags",
        "android tools 2026",
        "blog content automation",
        "h2 focus keyword",
        "tr_bq quote block",
        "blogger labels"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tag, contentDescription = null, tint = SignalGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inject Keywords", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Quickly inject keywords into your Blogger post HTML, headers, or metadata.",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = keywordText,
                    onValueChange = { keywordText = it },
                    label = { Text("Keywords (comma-separated)", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_keywords_field"),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tap to add keyword:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    presetKeywords.forEach { kw ->
                        FilterChip(
                            selected = keywordText.contains(kw),
                            onClick = {
                                if (!keywordText.contains(kw)) {
                                    keywordText = if (keywordText.isBlank()) kw else "$keywordText, $kw"
                                }
                            },
                            label = { Text(kw, fontSize = 10.sp, fontFamily = FontFamily.Monospace) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Injection Mode:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    InjectionMode.values().forEach { mode ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMode = mode },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedMode == mode) SignalGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (selectedMode == mode) androidx.compose.foundation.BorderStroke(1.5.dp, SignalGold) else null,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mode.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onInjectKeywords(keywordText, selectedMode)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A)),
                modifier = Modifier.testTag("button_confirm_inject")
            ) {
                Text("Inject Now", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = FontFamily.Monospace)
            }
        }
    )
}

enum class InjectionMode(val title: String) {
    AT_CARET("Insert keywords at current caret position"),
    REPLACE_PLACEHOLDERS("Replace {{keywords}} & {{tags}} in post"),
    WRAP_HIGHLIGHT("Wrap keywords in <mark> highlight tags"),
    GENERATE_BLOGGER_LABELS("Generate Blogger Post Labels & Meta Block")
}

@Composable
fun BatchCopyDialog(
    fullText: String,
    onDismiss: () -> Unit,
    onCopyBatch: (String) -> Unit
) {
    var batchSize by remember { mutableIntStateOf(1000) }
    var batchIndex by remember { mutableIntStateOf(0) }

    val batches = remember(fullText, batchSize) {
        if (fullText.isEmpty()) listOf("")
        else {
            val list = mutableListOf<String>()
            var start = 0
            while (start < fullText.length) {
                var end = (start + batchSize).coerceAtMost(fullText.length)
                // Attempt to not break tags or words if possible
                if (end < fullText.length) {
                    val lastSpace = fullText.lastIndexOf(' ', end)
                    if (lastSpace > start + (batchSize / 2)) {
                        end = lastSpace
                    }
                }
                list.add(fullText.substring(start, end))
                start = end
            }
            list
        }
    }

    val total = batches.size
    val currentBatchText = batches.getOrElse(batchIndex) { "" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SignalGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Blogger Safe Batch Copy", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Blogger's web editor can freeze or truncate posts when pasting huge blocks of HTML. Copy in safe batches to paste sequentially without losing formatting.",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Batch Size:", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(500, 1000, 2500).forEach { size ->
                            FilterChip(
                                selected = batchSize == size,
                                onClick = {
                                    batchSize = size
                                    batchIndex = 0
                                },
                                label = { Text("$size", fontSize = 10.sp, fontFamily = FontFamily.Monospace) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                // Stepper bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (batchIndex > 0) batchIndex-- },
                            enabled = batchIndex > 0
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                        }
                        Text(
                            text = "Batch ${batchIndex + 1} of $total (${currentBatchText.length} chars)",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SignalGold
                        )
                        IconButton(
                            onClick = { if (batchIndex < total - 1) batchIndex++ },
                            enabled = batchIndex < total - 1
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Preview of batch
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 140.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = currentBatchText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCopyBatch(currentBatchText)
                    if (batchIndex < total - 1) {
                        batchIndex++
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A)),
                modifier = Modifier.testTag("button_copy_batch")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (batchIndex < total - 1) "Copy Batch & Next" else "Copy Final Batch",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontFamily = FontFamily.Monospace)
            }
        }
    )
}

@Composable
fun TemplatesAndFilesDialog(
    onDismiss: () -> Unit,
    onOpenFilePicker: () -> Unit,
    onSelectTemplate: (SnippetItem) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = SignalGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Load Content or Template", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pick file from storage button
                Button(
                    onClick = {
                        onOpenFilePicker()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("button_open_file_picker_dialog"),
                    colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select File (.html, .txt, .json)", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Or choose a Blogger Post Template:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                BloggerPresets.TEMPLATES.forEach { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectTemplate(template)
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = template.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${template.content.take(100)}...",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = FontFamily.Monospace)
            }
        }
    )
}
