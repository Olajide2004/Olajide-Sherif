package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.provider.OpenableColumns
import com.example.model.AutoTypeConfig
import com.example.model.AutoTypeState
import com.example.ui.theme.SignalGold
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun FileAutoTypeBatchPasteDialog(
    currentEditorText: String,
    onDismiss: () -> Unit,
    onStartAutoType: (String, AutoTypeConfig) -> Unit,
    onBatchPasteToEditor: (String) -> Unit,
    autoTypeState: AutoTypeState,
    autoTypeProgress: Float,
    onPauseAutoType: () -> Unit,
    onResumeAutoType: () -> Unit,
    onStopAutoType: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Auto Typing, 1: Auto Batch Paste

    // Loaded file text & name
    var loadedFileName by remember { mutableStateOf("Current Editor Content") }
    var fileContent by remember { mutableStateOf(currentEditorText) }

    // Auto-Typing Configuration
    var typingSpeedCps by remember { mutableFloatStateOf(20f) }
    var jitterPercent by remember { mutableFloatStateOf(15f) }
    var countdownSec by remember { mutableIntStateOf(3) }

    // Batch Paste Configuration
    var batchSizeOption by remember { mutableIntStateOf(800) } // characters per batch
    var delayBetweenBatchesMs by remember { mutableFloatStateOf(1500f) } // 1.5s
    var currentBatchIndex by remember { mutableIntStateOf(0) }
    var isBatchPastingActive by remember { mutableStateOf(false) }

    val batches = remember(fileContent, batchSizeOption) {
        if (fileContent.isEmpty()) emptyList()
        else fileContent.chunked(batchSizeOption.coerceAtLeast(100))
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {}

                var resolvedName = "imported_file.txt"
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIdx != -1 && cursor.moveToFirst()) {
                        resolvedName = cursor.getString(nameIdx) ?: resolvedName
                    }
                }

                context.contentResolver.openInputStream(it)?.use { stream ->
                    val reader = BufferedReader(InputStreamReader(stream, Charsets.UTF_8))
                    val content = reader.readText()
                    fileContent = content
                    loadedFileName = resolvedName
                    currentBatchIndex = 0
                    Toast.makeText(context, "Loaded $loadedFileName (${content.length} chars)", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("file_autotype_batch_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "File Auto-Typing & Batch Paste",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Automate file typing & sequential batch pasting for Blogger",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Loaded File Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📄 $loadedFileName",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${fileContent.length} chars • ${fileContent.count { it == '\n' } + 1} lines • ${batches.size} batches",
                                fontSize = 11.sp,
                                color = SignalGold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = {
                                    if (fileContent.isNotEmpty()) {
                                        onBatchPasteToEditor(fileContent)
                                        Toast.makeText(context, "Loaded into editor workbench", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("To Editor", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                                colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A)),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open File", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs: Auto-Typing vs Auto Batch Paste
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
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
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("File to Auto-Typing", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("File to Auto Batch Paste", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (selectedTab == 0) {
                        // --- TAB 0: FILE TO AUTO-TYPING ---
                        Text(
                            text = "Auto-Type File Content",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Simulates real human tactile typewriter keystrokes character-by-character into the post workbench.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        // Speed Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Typing Speed", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("${typingSpeedCps.toInt()} chars/sec", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SignalGold)
                        }
                        Slider(
                            value = typingSpeedCps,
                            onValueChange = { typingSpeedCps = it },
                            valueRange = 5f..80f,
                            colors = SliderDefaults.colors(thumbColor = SignalGold, activeTrackColor = SignalGold)
                        )

                        // Jitter Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Human Rhythm Jitter", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("${jitterPercent.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SignalGold)
                        }
                        Slider(
                            value = jitterPercent,
                            onValueChange = { jitterPercent = it },
                            valueRange = 0f..50f,
                            colors = SliderDefaults.colors(thumbColor = SignalGold, activeTrackColor = SignalGold)
                        )

                        // Countdown delay chips
                        Text("Pre-Typing Countdown Delay", fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(0, 1, 3, 5).forEach { sec ->
                                FilterChip(
                                    selected = countdownSec == sec,
                                    onClick = { countdownSec = sec },
                                    label = { Text(if (sec == 0) "Immediate" else "${sec}s Delay", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SignalGold,
                                        selectedLabelColor = Color(0xFF1C2B2A)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress state when running
                        if (autoTypeState == AutoTypeState.TYPING || autoTypeState == AutoTypeState.PAUSED) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (autoTypeState == AutoTypeState.TYPING) "Typing in progress..." else "Paused",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = SignalGold
                                        )
                                        Text(
                                            text = "${(autoTypeProgress * 100).toInt()}%",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { autoTypeProgress },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = SignalGold,
                                        trackColor = MaterialTheme.colorScheme.surface
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (autoTypeState == AutoTypeState.TYPING) {
                                            OutlinedButton(
                                                onClick = onPauseAutoType,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Pause")
                                            }
                                        } else {
                                            Button(
                                                onClick = onResumeAutoType,
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A))
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Resume")
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = onStopAutoType,
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Stop")
                                        }
                                    }
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    val cfg = AutoTypeConfig(
                                        speedCharsPerSec = typingSpeedCps.toInt(),
                                        jitterPercent = jitterPercent.toInt(),
                                        countdownSeconds = countdownSec
                                    )
                                    onStartAutoType(fileContent, cfg)
                                    Toast.makeText(context, "Started auto-typing file", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A))
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Auto-Typing File", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                    } else {
                        // --- TAB 1: FILE TO AUTO BATCH PASTE CODE ---
                        Text(
                            text = "Auto Batch Paste Large Code Files",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Splits massive HTML/CSS/JS or article code into safe batch blocks to prevent web editor freezes or truncation.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        // Chunk Size selector
                        Text("Batch Chunk Size", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(400, 800, 1500, 3000).forEach { size ->
                                FilterChip(
                                    selected = batchSizeOption == size,
                                    onClick = {
                                        batchSizeOption = size
                                        currentBatchIndex = 0
                                    },
                                    label = { Text("${size} chars", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SignalGold,
                                        selectedLabelColor = Color(0xFF1C2B2A)
                                    )
                                )
                            }
                        }

                        // Delay between batches
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delay Between Batches", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("${String.format("%.1f", delayBetweenBatchesMs / 1000f)}s", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SignalGold)
                        }
                        Slider(
                            value = delayBetweenBatchesMs,
                            onValueChange = { delayBetweenBatchesMs = it },
                            valueRange = 500f..4000f,
                            colors = SliderDefaults.colors(thumbColor = SignalGold, activeTrackColor = SignalGold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Batch Status Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Batch ${if (batches.isEmpty()) 0 else (currentBatchIndex + 1)} of ${batches.size}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = SignalGold
                                    )
                                    Text(
                                        text = "${batches.getOrNull(currentBatchIndex)?.length ?: 0} chars in this batch",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Current Batch Preview Box
                                val currentBatchSnippet = batches.getOrNull(currentBatchIndex) ?: ""
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                        .border(0.5.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = currentBatchSnippet.ifEmpty { "No more batches or empty file." },
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 4
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Single Step Paste
                                    Button(
                                        onClick = {
                                            if (currentBatchIndex < batches.size) {
                                                val chunk = batches[currentBatchIndex]
                                                onBatchPasteToEditor(chunk)
                                                Toast.makeText(context, "Pasted Batch ${currentBatchIndex + 1}/${batches.size}", Toast.LENGTH_SHORT).show()
                                                if (currentBatchIndex < batches.size - 1) {
                                                    currentBatchIndex++
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(42.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A))
                                    ) {
                                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Paste Batch (${currentBatchIndex + 1})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Next / Skip Batch
                                    OutlinedButton(
                                        onClick = {
                                            if (currentBatchIndex < batches.size - 1) {
                                                currentBatchIndex++
                                            } else {
                                                currentBatchIndex = 0
                                            }
                                        },
                                        modifier = Modifier.height(42.dp)
                                    ) {
                                        Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Next Batch", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
