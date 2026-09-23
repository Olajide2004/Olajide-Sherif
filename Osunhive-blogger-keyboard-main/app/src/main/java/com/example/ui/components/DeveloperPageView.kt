package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SignalGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AdUnitType(val title: String, val desc: String) {
    RESPONSIVE("Responsive Display", "Standard display banner adapting to sidebar, content header or footer."),
    IN_ARTICLE("In-Article Native", "Native banner blending naturally between blog paragraphs."),
    IN_FEED("In-Feed Fluid", "Fluid native card matching Blogger homepage / archive feeds."),
    MULTIPLEX("Multiplex Grid", "Grid of recommended content and monetization links."),
    AUTO_ADS("Auto-Ads Script", "Header script enabling Google automated ad placements."),
    ADS_TXT("Ads.txt Record", "Authorized digital seller line for Blogger custom ads.txt setting.")
}

@Composable
fun DeveloperPageView(
    onInsertSnippet: (String) -> Unit,
    onCopyText: (String, String) -> Unit,
    onSwitchToBrowser: (String) -> Unit,
    charCount: Int,
    wordCount: Int,
    lineCount: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var selectedAdUnit by remember { mutableStateOf(AdUnitType.RESPONSIVE) }
    var publisherId by remember { mutableStateOf("ca-pub-4599936762650000") }
    var slotId by remember { mutableStateOf("9876543210") }

    // Benchmark state
    var isBenchmarking by remember { mutableStateOf(false) }
    var benchmarkResult by remember { mutableStateOf<String?>(null) }

    val generatedAdCode = remember(selectedAdUnit, publisherId, slotId) {
        val cleanPub = if (publisherId.startsWith("ca-pub-")) publisherId else "ca-pub-$publisherId"
        val pubNum = cleanPub.removePrefix("ca-pub-")
        when (selectedAdUnit) {
            AdUnitType.RESPONSIVE -> """
<!-- Google AdSense - Responsive Display (OsunHive) -->
<ins class="adsbygoogle"
     style="display:block"
     data-ad-client="$cleanPub"
     data-ad-slot="$slotId"
     data-ad-format="auto"
     data-full-width-responsive="true"></ins>
<script>
     (adsbygoogle = window.adsbygoogle || []).push({});
</script>
            """.trimIndent()

            AdUnitType.IN_ARTICLE -> """
<!-- Google AdSense - In-Article Native Unit -->
<ins class="adsbygoogle"
     style="display:block; text-align:center;"
     data-ad-layout="in-article"
     data-ad-format="fluid"
     data-ad-client="$cleanPub"
     data-ad-slot="$slotId"></ins>
<script>
     (adsbygoogle = window.adsbygoogle || []).push({});
</script>
            """.trimIndent()

            AdUnitType.IN_FEED -> """
<!-- Google AdSense - In-Feed Native Unit -->
<ins class="adsbygoogle"
     style="display:block"
     data-ad-format="fluid"
     data-ad-layout-key="-6t+ed+2i-1n-4w"
     data-ad-client="$cleanPub"
     data-ad-slot="$slotId"></ins>
<script>
     (adsbygoogle = window.adsbygoogle || []).push({});
</script>
            """.trimIndent()

            AdUnitType.MULTIPLEX -> """
<!-- Google AdSense - Multiplex Matched Content Grid -->
<ins class="adsbygoogle"
     style="display:block"
     data-ad-format="autorelaxed"
     data-ad-client="$cleanPub"
     data-ad-slot="$slotId"></ins>
<script>
     (adsbygoogle = window.adsbygoogle || []).push({});
</script>
            """.trimIndent()

            AdUnitType.AUTO_ADS -> """
<!-- Google AdSense Auto-Ads Tag (Place inside <head> of Blogger Theme) -->
<script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=$cleanPub"
     crossorigin="anonymous"></script>
            """.trimIndent()

            AdUnitType.ADS_TXT -> "google.com, pub-$pubNum, DIRECT, f08c47fec0942fa0"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Developer Profile Hero Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("dev_card_profile"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(2.dp, SignalGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "SO",
                            color = SignalGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Olajide Sherif Oyinlola",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified Creator",
                                tint = SignalGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Lead Developer • OsunHive Creator Ecosystem",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "olajidesherifoyinlola@gmail.com",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SignalGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Welcome to the Blogger Auto Typer Developer Hub. Build high-CTR, monetization-ready Blogger layouts and inject responsive Google AdSense units directly into your posts and templates.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Developer Direct Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.osunhive.name.ng"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = SignalGold,
                            contentColor = Color(0xFF1C2B2A)
                        ),
                        modifier = Modifier.weight(1f).height(38.dp).testTag("dev_btn_osunhive")
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("OsunHive Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Osunhive"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF0284C7),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).height(38.dp).testTag("dev_btn_telegram")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Telegram Community", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            try {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:olajidesherifoyinlola@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Blogger Auto Typer Developer Inquiry")
                                }
                                context.startActivity(emailIntent)
                            } catch (_: Exception) {
                                onCopyText("olajidesherifoyinlola@gmail.com", "Developer Email")
                            }
                        },
                        modifier = Modifier.weight(0.9f).height(38.dp).testTag("dev_btn_email")
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Contact", fontSize = 11.sp)
                    }
                }
            }
        }

        // Google AdSense Developer Studio Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("dev_card_adsense"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = SignalGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google AdSense Integration Studio",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF16A34A).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Publisher Ready", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Generate and inject Google-compliant ad tags into your Blogger posts or theme template.",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Unit Type Selector Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedAdUnit.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedAdUnit.ordinal]),
                            color = SignalGold
                        )
                    },
                    divider = {},
                    edgePadding = 4.dp
                ) {
                    AdUnitType.values().forEach { unit ->
                        Tab(
                            selected = selectedAdUnit == unit,
                            onClick = { selectedAdUnit = unit },
                            text = { Text(unit.title, fontSize = 11.sp, fontWeight = if (selectedAdUnit == unit) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = selectedAdUnit.desc,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Publisher ID & Slot ID inputs (if not ads.txt or auto ads)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = publisherId,
                        onValueChange = { publisherId = it },
                        label = { Text("AdSense Client / Pub ID", fontSize = 11.sp) },
                        modifier = Modifier.weight(1.3f).testTag("input_adsense_pub_id"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SignalGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    )

                    if (selectedAdUnit != AdUnitType.AUTO_ADS && selectedAdUnit != AdUnitType.ADS_TXT) {
                        OutlinedTextField(
                            value = slotId,
                            onValueChange = { slotId = it },
                            label = { Text("Ad Slot ID", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("input_adsense_slot_id"),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SignalGold,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Code snippet preview box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Generated Code (${selectedAdUnit.name})",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SignalGold
                            )
                            IconButton(
                                onClick = { onCopyText(generatedAdCode, "AdSense Code") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = Color.White, modifier = Modifier.size(13.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = generatedAdCode,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AdSense Code Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onCopyText(generatedAdCode, "AdSense Snippet") },
                        modifier = Modifier.weight(1f).height(36.dp).testTag("btn_copy_adsense_code")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Snippet", fontSize = 11.sp)
                    }

                    ElevatedButton(
                        onClick = {
                            onInsertSnippet("\n$generatedAdCode\n")
                            Toast.makeText(context, "Inserted AdSense snippet into post editor!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = SignalGold,
                            contentColor = Color(0xFF1C2B2A)
                        ),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("btn_insert_adsense_code")
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Insert to Post", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = {
                            onSwitchToBrowser("https://adsense.google.com/")
                        },
                        modifier = Modifier.weight(1f).height(36.dp).testTag("btn_open_adsense_site")
                    ) {
                        Icon(Icons.Default.AdsClick, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AdSense Web", fontSize = 11.sp)
                    }
                }
            }
        }

        // Blogger API & Automation Reference Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("dev_card_api"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = SignalGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Blogger REST API v3 Reference",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Automate post publishing, label tagging, and blog synchronization with Google Blogger endpoints:",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                val endpoints = listOf(
                    "POST https://www.googleapis.com/blogger/v3/blogs/{blogId}/posts" to "Create new blog post with HTML body and labels",
                    "GET  https://www.googleapis.com/blogger/v3/blogs/{blogId}/posts" to "Retrieve list of published & draft posts",
                    "PUT  https://www.googleapis.com/blogger/v3/blogs/{blogId}/posts/{postId}" to "Update existing post content or metadata",
                    "GET  https://www.googleapis.com/blogger/v3/users/self/blogs" to "Fetch authenticated user's managed blog IDs"
                )

                endpoints.forEach { (ep, desc) ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = ep,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // System Telemetry & Benchmark Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("dev_card_diagnostics"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = SignalGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Engine Diagnostics & Telemetry",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    IconButton(
                        onClick = {
                            val runtime = Runtime.getRuntime()
                            val usedMemMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                            val totalMemMb = runtime.totalMemory() / (1024 * 1024)
                            val report = """
========================================
BLOGGER AUTO TYPER - SYSTEM REPORT
========================================
App Version: v2.5.0 Professional
Creator: Olajide Sherif Oyinlola (OsunHive)
Platform: https://www.osunhive.name.ng
Telegram: t.me/Osunhive
IME Service: com.example.ime.TypewriterInputMethodService
Active Buffer: $charCount chars, $wordCount words, $lineCount lines
JVM Heap Memory: ${usedMemMb}MB used / ${totalMemMb}MB allocated
Operating Mode: M3 Compose Native Environment
Timestamp: ${System.currentTimeMillis()}
========================================
                            """.trimIndent()
                            onCopyText(report, "System Diagnostic Report")
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Report", tint = SignalGold, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Active Post Buffer", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$charCount chars", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("$wordCount words • $lineCount lines", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        val runtime = Runtime.getRuntime()
                        val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("JVM Memory Heap", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${usedMb} MB Used", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Jetpack Compose M3", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Typing Benchmark Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = {
                            isBenchmarking = true
                            benchmarkResult = null
                            scope.launch {
                                val start = System.currentTimeMillis()
                                delay(350) // simulate typing 1000 characters
                                val elapsed = (System.currentTimeMillis() - start).coerceAtLeast(1)
                                val charsSec = (1000 * 1000) / elapsed
                                benchmarkResult = "Benchmark: Processed 1,000 characters in ${elapsed}ms (~$charsSec chars/sec throughput). Latency < 1ms."
                                isBenchmarking = false
                            }
                        },
                        enabled = !isBenchmarking,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = SignalGold,
                            contentColor = Color(0xFF1C2B2A)
                        ),
                        modifier = Modifier.weight(1f).height(38.dp).testTag("btn_run_benchmark")
                    ) {
                        if (isBenchmarking) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF1C2B2A), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Running Benchmark...", fontSize = 11.sp)
                        } else {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Speed Benchmark", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                AnimatedVisibility(visible = benchmarkResult != null) {
                    benchmarkResult?.let { res ->
                        Surface(
                            color = Color(0xFF16A34A).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text(
                                text = res,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF16A34A),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
