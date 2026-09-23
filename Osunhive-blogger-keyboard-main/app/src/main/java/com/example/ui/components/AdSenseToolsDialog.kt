package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.SignalGold

@Composable
fun AdSenseToolsDialog(
    onDismiss: () -> Unit,
    onInjectAdUnit: (pubId: String, slotId: String, format: String) -> Unit,
    onNavigateToAdSense: (String) -> Unit,
    onCopyToClipboard: (String) -> Unit
) {
    var publisherId by remember { mutableStateOf("ca-pub-1234567890123456") }
    var adSlotId by remember { mutableStateOf("9876543210") }
    var selectedFormat by remember { mutableStateOf("auto") } // auto, in-article, in-feed, display
    var copiedAdsTxt by remember { mutableStateOf(false) }
    var copiedAdCode by remember { mutableStateOf(false) }

    val cleanPub = if (publisherId.startsWith("ca-pub-")) publisherId else "ca-pub-$publisherId"
    val cleanPubNumber = publisherId.replace("ca-pub-", "").replace("pub-", "").trim()
    val adsTxtContent = "google.com, pub-$cleanPubNumber, DIRECT, f08c47fec0942fa0"

    val generatedAdCode = when (selectedFormat) {
        "in-article" -> """
<!-- Google AdSense In-Article Ad Unit -->
<div class="oh-ad-article" style="text-align:center; margin:20px 0;">
  <script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=$cleanPub" crossorigin="anonymous"></script>
  <ins class="adsbygoogle"
       style="display:block; text-align:center;"
       data-ad-layout="in-article"
       data-ad-format="fluid"
       data-ad-client="$cleanPub"
       data-ad-slot="${adSlotId.ifEmpty { "1234567890" }}"></ins>
  <script>
       (adsbygoogle = window.adsbygoogle || []).push({});
  </script>
</div>
<!-- /Google AdSense In-Article -->
""".trimIndent()
        "in-feed" -> """
<!-- Google AdSense In-Feed Ad Unit -->
<div class="oh-ad-feed" style="margin:16px 0;">
  <script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=$cleanPub" crossorigin="anonymous"></script>
  <ins class="adsbygoogle"
       style="display:block"
       data-ad-format="fluid"
       data-ad-layout-key="-fb+5w+4e-db+86"
       data-ad-client="$cleanPub"
       data-ad-slot="${adSlotId.ifEmpty { "1234567890" }}"></ins>
  <script>
       (adsbygoogle = window.adsbygoogle || []).push({});
  </script>
</div>
<!-- /Google AdSense In-Feed -->
""".trimIndent()
        else -> """
<!-- Google AdSense Responsive Display Banner -->
<div class="oh-ad-banner" style="text-align:center; margin:16px 0; min-height:90px;">
  <script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=$cleanPub" crossorigin="anonymous"></script>
  <ins class="adsbygoogle"
       style="display:block"
       data-ad-client="$cleanPub"
       data-ad-slot="${adSlotId.ifEmpty { "1234567890" }}"
       data-ad-format="auto"
       data-full-width-responsive="true"></ins>
  <script>
       (adsbygoogle = window.adsbygoogle || []).push({});
  </script>
</div>
<!-- /Google AdSense Banner -->
""".trimIndent()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("adsense_tools_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
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
                                .background(SignalGold, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Google AdSense Workbench",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "Monetize Blogger Posts & Generate Ad Units",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("button_close_adsense_tools")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                // AdSense Quick Links
                Text(
                    text = "AdSense Navigation",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            onNavigateToAdSense("https://adsense.google.com/")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).testTag("btn_goto_adsense_home"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AdSense Console", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            onNavigateToAdSense("https://www.google.com/adsense/new/u/0/pub-overview")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).testTag("btn_goto_adsense_sites"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = SignalGold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sites & Ads Overview", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input fields
                Text(
                    text = "Configure AdSense Parameters",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = publisherId,
                    onValueChange = { publisherId = it },
                    label = { Text("Publisher ID (ca-pub-XXXXXXXXXXXXXXXX)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_adsense_pub_id"),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = adSlotId,
                    onValueChange = { adSlotId = it },
                    label = { Text("Ad Slot ID (10-digit number from AdSense)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_adsense_slot_id"),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Format selector chips
                Text(
                    text = "Select Ad Unit Format",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val formats = listOf(
                        "auto" to "Responsive",
                        "in-article" to "In-Article",
                        "in-feed" to "In-Feed"
                    )
                    for ((fmtKey, fmtLabel) in formats) {
                        val isSelected = selectedFormat == fmtKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) SignalGold else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedFormat = fmtKey }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fmtLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons: Inject or Copy Ad Code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onInjectAdUnit(cleanPub, adSlotId, selectedFormat)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SignalGold),
                        modifier = Modifier.weight(1.3f).testTag("button_inject_adsense_code")
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.surface)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Inject Into Post", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.surface)
                    }

                    OutlinedButton(
                        onClick = {
                            onCopyToClipboard(generatedAdCode)
                            copiedAdCode = true
                        },
                        modifier = Modifier.weight(1f).testTag("button_copy_adsense_code")
                    ) {
                        Icon(if (copiedAdCode) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (copiedAdCode) "Copied" else "Copy Code", fontSize = 11.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ads.txt Generator Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Blogger Custom ads.txt Entry",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            FilledTonalButton(
                                onClick = {
                                    onCopyToClipboard(adsTxtContent)
                                    copiedAdsTxt = true
                                },
                                modifier = Modifier.height(28.dp).testTag("button_copy_ads_txt"),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Icon(if (copiedAdsTxt) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (copiedAdsTxt) "Copied" else "Copy ads.txt", fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = adsTxtContent,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Paste in Blogger -> Settings -> Monetization -> Enable custom ads.txt",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
