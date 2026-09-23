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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.SignalGold

private const val CHROME_DESKTOP_UA =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BloggerWebViewPane(
    url: String = "https://www.osunhive.name.ng",
    postText: String,
    keywordsText: String,
    isAutoTypingActive: Boolean,
    onStartWebAutoType: (WebView) -> Unit,
    onStopWebAutoType: () -> Unit,
    onOpenExternalChrome: () -> Unit,
    onShowChromeBookmarklet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageTitle by remember { mutableStateOf("OsunHive") }
    var currentUrl by remember { mutableStateOf(url) }
    var loadingProgress by remember { mutableFloatStateOf(0f) }
    var isDesktopMode by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Ready to inject into osunhive.name.ng") }

    Column(modifier = modifier.fillMaxSize()) {
        // Top Web Action Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Column(modifier = Modifier.padding(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { webViewInstance?.goBack() },
                            enabled = canGoBack,
                            modifier = Modifier.size(32.dp).testTag("web_btn_back")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { webViewInstance?.goForward() },
                            enabled = canGoForward,
                            modifier = Modifier.size(32.dp).testTag("web_btn_forward")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { webViewInstance?.reload() },
                            modifier = Modifier.size(32.dp).testTag("web_btn_reload")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { webViewInstance?.loadUrl("https://www.osunhive.name.ng") },
                            modifier = Modifier.size(32.dp).testTag("web_btn_home")
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pageTitle.ifEmpty { "osunhive.name.ng" },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentUrl,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, fontFamily = FontFamily.Monospace),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Desktop/Mobile toggle
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
                            modifier = Modifier.size(32.dp).testTag("web_btn_toggle_ua")
                        ) {
                            Icon(
                                if (isDesktopMode) Icons.Default.Computer else Icons.Default.Smartphone,
                                contentDescription = if (isDesktopMode) "Desktop View (Active)" else "Mobile View",
                                tint = if (isDesktopMode) SignalGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Chrome Bookmarklet helper
                        IconButton(
                            onClick = onShowChromeBookmarklet,
                            modifier = Modifier.size(32.dp).testTag("web_btn_chrome_script")
                        ) {
                            Icon(Icons.Default.Code, contentDescription = "Chrome Script & Bookmarklet", tint = SignalGold, modifier = Modifier.size(18.dp))
                        }

                        // Open in Chrome app
                        IconButton(
                            onClick = onOpenExternalChrome,
                            modifier = Modifier.size(32.dp).testTag("web_btn_open_chrome")
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Chrome", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Quick Blogger Inject Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isAutoTypingActive) {
                        ElevatedButton(
                            onClick = {
                                webViewInstance?.let { wv ->
                                    statusMessage = "Auto-typing into active Blogger element..."
                                    onStartWebAutoType(wv)
                                }
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = SignalGold,
                                contentColor = Color(0xFF1C2B2A)
                            ),
                            modifier = Modifier.height(30.dp).weight(1.3f).testTag("web_btn_auto_type"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Auto-Type Here", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        ElevatedButton(
                            onClick = {
                                statusMessage = "Auto-typing stopped."
                                onStopWebAutoType()
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            modifier = Modifier.height(30.dp).weight(1.3f).testTag("web_btn_stop_type"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Stop Typing", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 1-Click Fast HTML Inject
                    FilledTonalButton(
                        onClick = {
                            webViewInstance?.let { wv ->
                                injectHtmlDirectly(wv, postText) { success ->
                                    statusMessage = if (success) "HTML injected into Blogger editor!" else "Click inside Blogger editor first"
                                }
                            }
                        },
                        modifier = Modifier.height(30.dp).weight(1.1f).testTag("web_btn_fast_inject"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Inject HTML", fontSize = 11.sp)
                    }

                    // 1-Click Inject Keywords
                    OutlinedButton(
                        onClick = {
                            webViewInstance?.let { wv ->
                                injectKeywordsDirectly(wv, keywordsText) { success ->
                                    statusMessage = if (success) "Keywords added to Blogger labels!" else "Click label field or editor first"
                                }
                            }
                        },
                        modifier = Modifier.height(30.dp).weight(1f).testTag("web_btn_inject_kw"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(12.dp), tint = SignalGold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Labels", fontSize = 11.sp)
                    }
                }

                // Live status helper
                Text(
                    text = "Tip: $statusMessage",
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Web Loading Progress
        AnimatedVisibility(visible = loadingProgress < 1f && loadingProgress > 0f) {
            LinearProgressIndicator(
                progress = { loadingProgress },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = SignalGold,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // WebView Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    // Pre-create WebView cache directories to prevent Chromium simple_index_file opendir errors
                    try {
                        val baseCache = java.io.File(ctx.cacheDir, "WebView/Default/HTTP Cache")
                        java.io.File(baseCache, "Code Cache/js").mkdirs()
                        java.io.File(baseCache, "Code Cache/wasm").mkdirs()
                        java.io.File(baseCache, "Cache_Data").mkdirs()
                    } catch (_: Throwable) {}

                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        // Configure web settings for clean Blogger compatibility
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = false
                            safeBrowsingEnabled = false
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            cacheMode = WebSettings.LOAD_DEFAULT

                            if (isDesktopMode) {
                                userAgentString = CHROME_DESKTOP_UA
                                useWideViewPort = true
                                loadWithOverviewMode = true
                            } else {
                                userAgentString = null
                                useWideViewPort = false
                                loadWithOverviewMode = false
                            }
                        }

                        // Enable cookies (needed for Google account login)
                        val wvInstance = this
                        CookieManager.getInstance().apply {
                            setAcceptCookie(true)
                            setAcceptThirdPartyCookies(wvInstance, true)
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadingProgress = newProgress / 100f
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                pageTitle = title ?: "Blogger"
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                currentUrl = url ?: ""
                                canGoBack = canGoBack()
                                canGoForward = canGoForward()
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                currentUrl = url ?: ""
                                canGoBack = canGoBack()
                                canGoForward = canGoForward()
                                loadingProgress = 1f
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false // Allow in-app navigation
                            }
                        }

                        webViewInstance = this
                        loadUrl(url)
                    }
                },
                update = { wv ->
                    webViewInstance = wv
                }
            )
        }
    }
}

/**
 * Injects HTML or text directly into Blogger's active element, contenteditable body, or textarea.
 */
fun injectHtmlDirectly(webView: WebView, htmlContent: String, onResult: (Boolean) -> Unit) {
    val escaped = htmlContent
        .replace("\\", "\\\\")
        .replace("`", "\\`")
        .replace("$", "\\$")

    val script = """
        (function() {
            try {
                // Find active element or Blogger editor iframe/contentEditable
                var active = document.activeElement;
                var editor = null;
                
                // Check if active element is an input, textarea or contenteditable
                if (active && (active.tagName === 'TEXTAREA' || active.tagName === 'INPUT' || active.isContentEditable)) {
                    editor = active;
                } else {
                    // Search for Blogger compose iframe
                    var iframes = document.querySelectorAll('iframe');
                    for (var i = 0; i < iframes.size || i < iframes.length; i++) {
                        try {
                            var doc = iframes[i].contentDocument || iframes[i].contentWindow.document;
                            var body = doc.body;
                            if (body && (body.isContentEditable || body.getAttribute('contenteditable') === 'true')) {
                                editor = body;
                                doc.execCommand('insertHTML', false, `$escaped`);
                                return true;
                            }
                        } catch(e) {}
                    }
                    // Search for Blogger main contentEditable
                    editor = document.querySelector('[contenteditable="true"]') || 
                             document.querySelector('.editable') ||
                             document.querySelector('textarea.quantumWizTextinputPapertextareaInput') ||
                             document.querySelector('textarea');
                }
                
                if (editor) {
                    editor.focus();
                    if (editor.isContentEditable) {
                        document.execCommand('insertHTML', false, `$escaped`);
                    } else if (editor.tagName === 'TEXTAREA' || editor.tagName === 'INPUT') {
                        var start = editor.selectionStart || 0;
                        var end = editor.selectionEnd || 0;
                        var val = editor.value || '';
                        editor.value = val.substring(0, start) + `$escaped` + val.substring(end);
                        editor.dispatchEvent(new Event('input', { bubbles: true }));
                        editor.dispatchEvent(new Event('change', { bubbles: true }));
                    }
                    return true;
                }
                return false;
            } catch(err) {
                return false;
            }
        })();
    """.trimIndent()

    webView.evaluateJavascript(script) { result ->
        onResult(result == "true")
    }
}

/**
 * Injects keywords into Blogger's Labels/Tags input or active element.
 */
fun injectKeywordsDirectly(webView: WebView, keywords: String, onResult: (Boolean) -> Unit) {
    val clean = keywords.trim()
        .replace("\\", "\\\\")
        .replace("`", "\\`")

    val script = """
        (function() {
            try {
                // Look for Blogger Labels input
                var labelInput = document.querySelector('input[aria-label*="Labels"]') ||
                                 document.querySelector('input[aria-label*="Tags"]') ||
                                 document.querySelector('input[placeholder*="Labels"]') ||
                                 document.activeElement;
                if (labelInput && (labelInput.tagName === 'INPUT' || labelInput.tagName === 'TEXTAREA')) {
                    labelInput.focus();
                    var cur = labelInput.value || '';
                    labelInput.value = (cur ? cur + ', ' : '') + `$clean`;
                    labelInput.dispatchEvent(new Event('input', { bubbles: true }));
                    labelInput.dispatchEvent(new Event('change', { bubbles: true }));
                    return true;
                } else if (document.activeElement && document.activeElement.isContentEditable) {
                    document.execCommand('insertText', false, `$clean`);
                    return true;
                }
                return false;
            } catch(e) {
                return false;
            }
        })();
    """.trimIndent()

    webView.evaluateJavascript(script) { res ->
        onResult(res == "true")
    }
}

/**
 * Types a single character into Blogger's active element or contenteditable editor.
 */
fun typeCharIntoWebView(webView: WebView, charStr: String, onComplete: () -> Unit = {}) {
    val escaped = when (charStr) {
        "\\" -> "\\\\"
        "\"" -> "\\\""
        "\n" -> "\\n"
        "\r" -> ""
        "\t" -> "\\t"
        else -> charStr
    }

    val script = """
        (function() {
            try {
                var el = document.activeElement;
                // If not focused on an input or editable, check Blogger editor iframe
                if (!el || (!el.isContentEditable && el.tagName !== 'TEXTAREA' && el.tagName !== 'INPUT')) {
                    var iframes = document.querySelectorAll('iframe');
                    for (var i = 0; i < iframes.length; i++) {
                        try {
                            var doc = iframes[i].contentDocument || iframes[i].contentWindow.document;
                            if (doc && doc.body && (doc.body.isContentEditable || doc.body.getAttribute('contenteditable') === 'true')) {
                                el = doc.body;
                                doc.execCommand('insertText', false, "$escaped");
                                return true;
                            }
                        } catch(e) {}
                    }
                    var mainEditable = document.querySelector('[contenteditable="true"]');
                    if (mainEditable) {
                        el = mainEditable;
                        el.focus();
                    }
                }
                
                if (el) {
                    if (el.isContentEditable) {
                        if ("$escaped" === "\\n") {
                            document.execCommand('insertParagraph', false);
                        } else {
                            document.execCommand('insertText', false, "$escaped");
                        }
                        return true;
                    } else if (el.tagName === 'TEXTAREA' || el.tagName === 'INPUT') {
                        var start = el.selectionStart || 0;
                        var end = el.selectionEnd || 0;
                        var val = el.value || '';
                        var insert = ("$escaped" === "\\n") ? "\n" : "$escaped";
                        el.value = val.substring(0, start) + insert + val.substring(end);
                        el.selectionStart = el.selectionEnd = start + insert.length;
                        el.dispatchEvent(new Event('input', { bubbles: true }));
                        return true;
                    }
                }
                return false;
            } catch(e) {
                return false;
            }
        })();
    """.trimIndent()

    webView.evaluateJavascript(script) {
        onComplete()
    }
}
