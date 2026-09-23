package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.ui.theme.SignalGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromeBookmarkletDialog(
    currentPostContent: String,
    onDismiss: () -> Unit,
    onCopyBookmarklet: (String) -> Unit,
    onOpenChrome: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var copiedBookmarklet by remember { mutableStateOf(false) }
    var copiedScript by remember { mutableStateOf(false) }

    // Generates safe bookmarklet JavaScript
    val bookmarkletCode = remember(currentPostContent) {
        generateChromeBookmarkletCode(currentPostContent)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
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
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = Color(0xFF1C2B2A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Chrome Auto-Typer",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        )
                        Text(
                            text = "For draft.blogger.com in Google Chrome",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation Card
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "How to use directly in Google Chrome:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Tap 'Copy Chrome Bookmarklet' below.\n" +
                               "2. Open Chrome and go to draft.blogger.com.\n" +
                               "3. In Chrome's URL bar, type 'javascript:' and paste the copied code, then press Enter.\n" +
                               "4. A floating Typewriter HUD will appear on Blogger with your post ready to auto-type into any field!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bookmarklet Action Button
            ElevatedButton(
                onClick = {
                    onCopyBookmarklet(bookmarkletCode)
                    copiedBookmarklet = true
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = SignalGold,
                    contentColor = Color(0xFF1C2B2A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_copy_bookmarklet")
            ) {
                Icon(
                    if (copiedBookmarklet) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (copiedBookmarklet) "Bookmarklet Copied!" else "Copy Chrome Bookmarklet (1-Tap)",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Open Chrome directly button
            FilledTonalButton(
                onClick = onOpenChrome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_launch_chrome")
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open draft.blogger.com in Chrome")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Code Preview
            Text(
                text = "Bookmarklet Code Preview:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 140.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = bookmarkletCode,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun generateChromeBookmarkletCode(content: String): String {
    val cleanContent = content
        .replace("\\", "\\\\")
        .replace("`", "\\`")
        .replace("$", "\\$")

    return "javascript:(function(){" +
            "if(window._bloggerAutoTyperLoaded){alert('Auto-Typer already open!');return;}" +
            "window._bloggerAutoTyperLoaded=true;" +
            "var d=document.createElement('div');" +
            "d.id='blogger-autotyper-hud';" +
            "d.style.cssText='position:fixed;bottom:20px;right:20px;width:340px;background:#1C2B2A;color:#ECE7DC;padding:14px;border-radius:12px;box-shadow:0 8px 30px rgba(0,0,0,0.5);z-index:999999;font-family:monospace;font-size:12px;border:2px solid #E2A400;';" +
            "d.innerHTML='" +
            "<div style=\"display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;\">" +
            "<strong style=\"color:#E2A400;\">⌨️ Blogger Auto-Typer</strong>" +
            "<button id=\"bat-close\" style=\"background:none;border:none;color:#fff;cursor:pointer;font-size:16px;\">✕</button>" +
            "</div>" +
            "<div style=\"font-size:10px;color:#aaa;margin-bottom:6px;\">1. Click target editor in Blogger.<br>2. Click Start Typing.</div>" +
            "<textarea id=\"bat-text\" style=\"width:100%;height:70px;background:#2A3D3B;color:#ECE7DC;border:1px solid #444;border-radius:4px;font-size:11px;box-sizing:border-box;margin-bottom:8px;padding:4px;\">" +
            cleanContent.replace("\n", "\\n").replace("\"", "&quot;") +
            "</textarea>" +
            "<div style=\"display:flex;gap:6px;align-items:center;margin-bottom:8px;\">" +
            "<span>Speed:</span><input type=\"range\" id=\"bat-speed\" min=\"5\" max=\"80\" value=\"25\" style=\"flex:1;\">" +
            "<span id=\"bat-speed-val\">25 cps</span>" +
            "</div>" +
            "<div style=\"display:flex;gap:6px;\">" +
            "<button id=\"bat-start\" style=\"flex:1;background:#E2A400;color:#1C2B2A;border:none;padding:6px;border-radius:4px;font-weight:bold;cursor:pointer;\">▶ Start Typing</button>" +
            "<button id=\"bat-inject\" style=\"flex:1;background:#3B5452;color:#ECE7DC;border:none;padding:6px;border-radius:4px;cursor:pointer;\">⚡ Inject HTML</button>" +
            "</div>" +
            "<div id=\"bat-status\" style=\"font-size:10px;margin-top:6px;color:#80CBC4;\">Ready</div>';" +
            "document.body.appendChild(d);" +
            "var sp=document.getElementById('bat-speed'),spv=document.getElementById('bat-speed-val'),st=document.getElementById('bat-status'),tx=document.getElementById('bat-text');" +
            "sp.oninput=function(){spv.innerText=sp.value+' cps';};" +
            "document.getElementById('bat-close').onclick=function(){d.remove();window._bloggerAutoTyperLoaded=false;};" +
            "document.getElementById('bat-inject').onclick=function(){" +
            "var target=document.activeElement||document.querySelector('[contenteditable=true]')||document.querySelector('textarea');" +
            "if(target&&target.isContentEditable){document.execCommand('insertHTML',false,tx.value);st.innerText='Injected HTML successfully!';}" +
            "else if(target){target.value=tx.value;target.dispatchEvent(new Event('input'));st.innerText='Injected into field!';}" +
            "else{st.innerText='Error: click inside Blogger editor first';}" +
            "};" +
            "var timer=null;" +
            "document.getElementById('bat-start').onclick=function(){" +
            "if(timer){clearInterval(timer);timer=null;document.getElementById('bat-start').innerText='▶ Start Typing';st.innerText='Paused';return;}" +
            "var target=document.activeElement||document.querySelector('[contenteditable=true]')||document.querySelector('textarea');" +
            "if(!target){st.innerText='Click target field first!';return;}" +
            "var text=tx.value;var idx=0;var delay=Math.round(1000/parseInt(sp.value));" +
            "document.getElementById('bat-start').innerText='⏸ Pause';" +
            "st.innerText='Typing into Blogger...';" +
            "timer=setInterval(function(){" +
            "if(idx>=text.length){clearInterval(timer);timer=null;document.getElementById('bat-start').innerText='▶ Start Typing';st.innerText='Finished auto-typing!';return;}" +
            "var ch=text.charAt(idx++);" +
            "if(target.isContentEditable){if(ch==='\\n'){document.execCommand('insertParagraph',false);}else{document.execCommand('insertText',false,ch);}}" +
            "else{target.value+=ch;target.dispatchEvent(new Event('input'));}" +
            "st.innerText='Typing: '+idx+'/'+text.length;" +
            "},delay);" +
            "};" +
            "})();"
}
