package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.engine.AdSensePresets
import com.example.ui.theme.SignalGold

private const val CHROME_DESKTOP_UA =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AdSenseWebViewPane(
    initialUrl: String = AdSensePresets.DEFAULT_ADSENSE_URL,
    onInsertSnippetIntoPost: (String) -> Unit,
    onCopySnippet: (String) -> Unit,
    onOpenExternalChrome: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageTitle by remember { mutableStateOf("Google AdSense") }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var loadingProgress by remember { mutableFloatStateOf(0f) }
    var isDesktopMode by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var showAdGenerator by remember { mutableStateOf(false) }

    var publisherId by remember { mutableStateOf("ca-pub-1234567890123456") }
    var slotId by remember { mutableStateOf("9876543210") }

    Column(modifier = modifier.fillMaxSize()) {
        // Web Navigation Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { webViewInstance?.goBack() },
                        enabled = canGoBack,
                        modifier = Modifier.size(32.dp).testTag("btn_adsense_back")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = { webViewInstance?.goForward() },
                        enabled = canGoForward,
                        modifier = Modifier.size(32.dp).testTag("btn_adsense_forward")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward", modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = { webViewInstance?.reload() },
                        modifier = Modifier.size(32.dp).testTag("btn_adsense_reload")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload", modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = {
                            currentUrl = AdSensePresets.DEFAULT_ADSENSE_URL
                            webViewInstance?.loadUrl(currentUrl)
                        },
                        modifier = Modifier.size(32.dp).testTag("btn_adsense_home")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "AdSense Home", modifier = Modifier.size(16.dp))
                    }

                    // Title & URL Box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Column {
                            Text(
                                text = pageTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentUrl,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Desktop Mode Toggle
                    IconButton(
                        onClick = {
                            isDesktopMode = !isDesktopMode
                            webViewInstance?.let { wv ->
                                wv.settings.userAgentString = if (isDesktopMode) CHROME_DESKTOP_UA else null
                                wv.settings.useWideViewPort = isDesktopMode
                                wv.settings.loadWithOverviewMode = isDesktopMode
                                wv.reload()
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (isDesktopMode) Icons.Default.Computer else Icons.Default.Smartphone,
                            contentDescription = "Toggle Desktop Mode",
                            modifier = Modifier.size(16.dp),
                            tint = if (isDesktopMode) SignalGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Open in Chrome Tab
                    IconButton(
                        onClick = { onOpenExternalChrome(currentUrl) },
                        modifier = Modifier.size(32.dp).testTag("btn_open_adsense_chrome")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Chrome", modifier = Modifier.size(16.dp), tint = SignalGold)
                    }
                }

                if (loadingProgress in 0.01f..0.99f) {
                    LinearProgressIndicator(
                        progress = { loadingProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = SignalGold,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        // Quick Ad Units Generator Drawer Toggle Bar
        Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = SignalGold, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AdSense Ad Unit Code Generator", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                FilledTonalButton(
                    onClick = { showAdGenerator = !showAdGenerator },
                    modifier = Modifier.height(26.dp).testTag("btn_toggle_ad_generator"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(if (showAdGenerator) "Hide Generator" else "Insert Ad Snippets", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(if (showAdGenerator) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Expandable Ad Unit Generator
        AnimatedVisibility(visible = showAdGenerator) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = publisherId,
                            onValueChange = { publisherId = it },
                            label = { Text("Publisher ID (ca-pub-)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.3f).height(50.dp)
                        )
                        OutlinedTextField(
                            value = slotId,
                            onValueChange = { slotId = it },
                            label = { Text("Ad Slot ID", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select AdSense Format to Insert into Editor Draft:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    for (unit in AdSensePresets.UNITS) {
                        val generatedCode = remember(unit, publisherId, slotId) {
                            unit.templateGenerator(publisherId, slotId)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(unit.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(unit.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { onCopySnippet(generatedCode) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                    }

                                    Button(
                                        onClick = { onInsertSnippetIntoPost(generatedCode) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A)),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(26.dp).testTag("btn_insert_${unit.id}")
                                    ) {
                                        Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Insert", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // In-App Browser View loading https://www.google.com/adsense/
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            allowFileAccess = false
                            allowContentAccess = false
                        }

                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadingProgress = newProgress / 100f
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                if (!title.isNullOrBlank()) {
                                    pageTitle = title
                                }
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                currentUrl = url ?: AdSensePresets.DEFAULT_ADSENSE_URL
                                canGoBack = view?.canGoBack() == true
                                canGoForward = view?.canGoForward() == true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                currentUrl = url ?: AdSensePresets.DEFAULT_ADSENSE_URL
                                canGoBack = view?.canGoBack() == true
                                canGoForward = view?.canGoForward() == true
                                loadingProgress = 1f
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }

                        loadUrl(currentUrl)
                        webViewInstance = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("webview_adsense")
            )
        }
    }
}
