package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyboardLayout
import com.example.ui.theme.SignalGold

@Composable
fun TypewriterKeyboard(
    activeChar: Char?,
    currentLayout: KeyboardLayout,
    onLayoutChange: (KeyboardLayout) -> Unit,
    onKeyPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isShifted by remember { mutableStateOf(false) }
    var isCapsLock by remember { mutableStateOf(false) }

    val activeCharString = activeChar?.toString()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        // Layout selector strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = currentLayout == KeyboardLayout.QWERTY,
                onClick = { onLayoutChange(KeyboardLayout.QWERTY) },
                label = { Text("QWERTY", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                leadingIcon = { Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SignalGold,
                    selectedLabelColor = Color(0xFF1C2B2A)
                )
            )
            FilterChip(
                selected = currentLayout == KeyboardLayout.SYMBOLS,
                onClick = { onLayoutChange(KeyboardLayout.SYMBOLS) },
                label = { Text("123 & Sym", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SignalGold,
                    selectedLabelColor = Color(0xFF1C2B2A)
                )
            )
            FilterChip(
                selected = currentLayout == KeyboardLayout.BLOGGER_TAGS,
                onClick = { onLayoutChange(KeyboardLayout.BLOGGER_TAGS) },
                label = { Text("Blogger HTML", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                leadingIcon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SignalGold,
                    selectedLabelColor = Color(0xFF1C2B2A)
                )
            )
            FilterChip(
                selected = currentLayout == KeyboardLayout.PLUS_UI,
                onClick = { onLayoutChange(KeyboardLayout.PLUS_UI) },
                label = { Text("OsunHive UI", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                leadingIcon = { Icon(Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(14.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SignalGold,
                    selectedLabelColor = Color(0xFF1C2B2A)
                )
            )
        }

        when (currentLayout) {
            KeyboardLayout.QWERTY -> {
                QwertyLayout(
                    isShifted = isShifted || isCapsLock,
                    activeChar = activeCharString,
                    onKeyClick = {
                        onKeyPress(it)
                        if (isShifted && !isCapsLock) isShifted = false
                    },
                    onShiftClick = {
                        if (isShifted) {
                            if (isCapsLock) {
                                isCapsLock = false
                                isShifted = false
                            } else {
                                isCapsLock = true
                            }
                        } else {
                            isShifted = true
                        }
                    },
                    isCapsLock = isCapsLock,
                    onBackspace = onBackspace,
                    onEnter = onEnter,
                    onSpace = onSpace,
                    onSwitchToSym = { onLayoutChange(KeyboardLayout.SYMBOLS) }
                )
            }
            KeyboardLayout.SYMBOLS -> {
                SymbolsLayout(
                    activeChar = activeCharString,
                    onKeyClick = onKeyPress,
                    onBackspace = onBackspace,
                    onEnter = onEnter,
                    onSpace = onSpace,
                    onSwitchToQwerty = { onLayoutChange(KeyboardLayout.QWERTY) }
                )
            }
            KeyboardLayout.BLOGGER_TAGS -> {
                BloggerTagsLayout(
                    onInsertSnippet = onKeyPress,
                    onBackspace = onBackspace,
                    onEnter = onEnter,
                    onSpace = onSpace
                )
            }
            KeyboardLayout.PLUS_UI -> {
                PlusUiKeyboardLayout(
                    onInsertSnippet = onKeyPress,
                    onBackspace = onBackspace,
                    onEnter = onEnter,
                    onSpace = onSpace
                )
            }
        }
    }
}

@Composable
private fun QwertyLayout(
    isShifted: Boolean,
    activeChar: String?,
    onKeyClick: (String) -> Unit,
    onShiftClick: () -> Unit,
    isCapsLock: Boolean,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onSwitchToSym: () -> Unit
) {
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

    // Row 1
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        row1.forEach { char ->
            val display = if (isShifted) char.uppercase() else char
            val isActive = activeChar?.equals(char, ignoreCase = true) == true
            TypewriterKey(
                label = display,
                isActive = isActive,
                modifier = Modifier.weight(1f),
                onClick = { onKeyClick(display) }
            )
        }
    }

    // Row 2
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        row2.forEach { char ->
            val display = if (isShifted) char.uppercase() else char
            val isActive = activeChar?.equals(char, ignoreCase = true) == true
            TypewriterKey(
                label = display,
                isActive = isActive,
                modifier = Modifier.weight(1f),
                onClick = { onKeyClick(display) }
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }

    // Row 3 (Shift, letters, Backspace)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TypewriterKey(
            label = if (isCapsLock) "CAPS" else if (isShifted) "▲" else "⇧",
            isActive = isShifted || isCapsLock,
            isSpecial = true,
            modifier = Modifier.weight(1.5f),
            onClick = onShiftClick
        )

        row3.forEach { char ->
            val display = if (isShifted) char.uppercase() else char
            val isActive = activeChar?.equals(char, ignoreCase = true) == true
            TypewriterKey(
                label = display,
                isActive = isActive,
                modifier = Modifier.weight(1f),
                onClick = { onKeyClick(display) }
            )
        }

        TypewriterKey(
            label = "⌫",
            isSpecial = true,
            modifier = Modifier.weight(1.5f),
            onClick = onBackspace
        )
    }

    // Row 4 (123, comma, Space, dot, Enter)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TypewriterKey(
            label = "?123",
            isSpecial = true,
            modifier = Modifier.weight(1.6f),
            onClick = onSwitchToSym
        )
        TypewriterKey(
            label = ",",
            modifier = Modifier.weight(0.9f),
            isActive = activeChar == ",",
            onClick = { onKeyClick(",") }
        )
        TypewriterKey(
            label = "SPACE",
            isSpecial = true,
            modifier = Modifier.weight(3.8f),
            isActive = activeChar == " ",
            onClick = onSpace
        )
        TypewriterKey(
            label = ".",
            modifier = Modifier.weight(0.9f),
            isActive = activeChar == ".",
            onClick = { onKeyClick(".") }
        )
        TypewriterKey(
            label = "⏎",
            isSpecial = true,
            modifier = Modifier.weight(1.6f),
            isActive = activeChar == "\n",
            onClick = onEnter
        )
    }
}

@Composable
private fun SymbolsLayout(
    activeChar: String?,
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onSwitchToQwerty: () -> Unit
) {
    val row1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val row2 = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/")
    val row3 = listOf("<", ">", "\"", "'", ":", ";", "!", "?")

    Row(modifier = Modifier.fillMaxWidth()) {
        row1.forEach { sym ->
            TypewriterKey(
                label = sym,
                modifier = Modifier.weight(1f),
                isActive = activeChar == sym,
                onClick = { onKeyClick(sym) }
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        row2.forEach { sym ->
            TypewriterKey(
                label = sym,
                modifier = Modifier.weight(1f),
                isActive = activeChar == sym,
                onClick = { onKeyClick(sym) }
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        TypewriterKey(
            label = "ABC",
            isSpecial = true,
            modifier = Modifier.weight(1.5f),
            onClick = onSwitchToQwerty
        )
        row3.forEach { sym ->
            TypewriterKey(
                label = sym,
                modifier = Modifier.weight(1f),
                isActive = activeChar == sym,
                onClick = { onKeyClick(sym) }
            )
        }
        TypewriterKey(
            label = "⌫",
            isSpecial = true,
            modifier = Modifier.weight(1.5f),
            onClick = onBackspace
        )
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        TypewriterKey(
            label = "=",
            modifier = Modifier.weight(1.2f),
            isActive = activeChar == "=",
            onClick = { onKeyClick("=") }
        )
        TypewriterKey(
            label = "/",
            modifier = Modifier.weight(1.2f),
            isActive = activeChar == "/",
            onClick = { onKeyClick("/") }
        )
        TypewriterKey(
            label = "SPACE",
            isSpecial = true,
            modifier = Modifier.weight(3.5f),
            isActive = activeChar == " ",
            onClick = onSpace
        )
        TypewriterKey(
            label = "_",
            modifier = Modifier.weight(1.2f),
            isActive = activeChar == "_",
            onClick = { onKeyClick("_") }
        )
        TypewriterKey(
            label = "⏎",
            isSpecial = true,
            modifier = Modifier.weight(1.5f),
            isActive = activeChar == "\n",
            onClick = onEnter
        )
    }
}

@Composable
private fun BloggerTagsLayout(
    onInsertSnippet: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit
) {
    val tagsRow1 = listOf(
        "<p>" to "<p></p>",
        "<h2>" to "<h2></h2>",
        "<h3>" to "<h3></h3>",
        "<b>" to "<b></b>",
        "<i>" to "<i></i>",
        "<a>" to "<a href=\"https://\" target=\"_blank\"></a>"
    )
    val tagsRow2 = listOf(
        "<blockquote>" to "<blockquote class=\"tr_bq\"></blockquote>",
        "<img>" to "<div class=\"separator\"><img src=\"\" alt=\"\" /></div>",
        "<code>" to "<code></code>",
        "<mark>" to "<mark></mark>",
        "<hr>" to "<hr />",
        "<ul>" to "<ul>\n  <li></li>\n</ul>"
    )
    val tagsRow3 = listOf(
        "Labels:" to "<!-- Blogger Labels: Technology, Android -->\n",
        "<!-- -->" to "<!--  -->",
        "Title:" to "<!-- Post Title:  -->\n",
        "CTA Box" to "<div style=\"background:#e8f0fe;padding:12px;border-radius:6px;\"></div>"
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        tagsRow1.forEach { (label, code) ->
            TypewriterKey(
                label = label,
                modifier = Modifier.weight(1f),
                onClick = { onInsertSnippet(code) }
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        tagsRow2.forEach { (label, code) ->
            TypewriterKey(
                label = label,
                modifier = Modifier.weight(1f),
                onClick = { onInsertSnippet(code) }
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        tagsRow3.forEach { (label, code) ->
            TypewriterKey(
                label = label,
                modifier = Modifier.weight(1f),
                onClick = { onInsertSnippet(code) }
            )
        }
        TypewriterKey(
            label = "⌫",
            isSpecial = true,
            modifier = Modifier.weight(0.8f),
            onClick = onBackspace
        )
        TypewriterKey(
            label = "⏎",
            isSpecial = true,
            modifier = Modifier.weight(0.8f),
            onClick = onEnter
        )
    }
}

@Composable
private fun PlusUiKeyboardLayout(
    onInsertSnippet: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit
) {
    // Row 1: High Priority Video, Download Box, Safelink, DropCap, Indented Paragraph
    val row1 = listOf(
        "🎬 Video" to "<div class=\"videoYt\"><iframe src=\"https://www.youtube.com/embed/VIDEO_ID\" allowfullscreen></iframe></div>\n",
        "📥 DlBox" to "<div class=\"dlBox\"><div class=\"fT\" data-text=\"ZIP\"></div><div class=\"fN\"><span>File.zip</span><span class=\"fS\">12 MB</span></div><a class=\"button safeL\" href=\"#\" aria-label=\"Download\"><i class=\"icon dl\"></i></a></div>\n",
        "🔒 SafeL" to "<a class=\"button safeL\" href=\"REAL-URL\">Download</a>",
        "DropCap" to "<span class=\"dropCap\">T</span>",
        "pIndent" to "<p class=\"pIndent\">Paragraph text here.</p>\n"
    )

    // Row 2: OsunHive UI Alert & Note Callouts
    val row2 = listOf(
        "Alert Info" to "<div class=\"alert info\"><strong>Info</strong> Information text here.</div>\n",
        "Alert Success" to "<div class=\"alert success\"><strong>Success</strong> Action succeeded.</div>\n",
        "Alert Warn" to "<div class=\"alert warning\"><strong>Warning</strong> Caution text.</div>\n",
        "Alert Error" to "<div class=\"alert error\"><strong>Error</strong> Critical notice.</div>\n",
        "Note" to "<p class=\"note\">Editorial note.</p>\n"
    )

    // Row 3: OsunHive UI Buttons & Code
    val row3 = listOf(
        "Button" to "<a class=\"button\" href=\"URL\">Link</a>",
        "Outline" to "<a class=\"button ln\" href=\"URL\">Link</a>",
        "Dl Btn" to "<a class=\"button\" href=\"URL\"><i class=\"icon dl\"></i>Download</a>",
        "Btn Row" to "<div class=\"btnF\"><a class=\"button\" href=\"#\">Download</a><a class=\"button ln\" href=\"#\">Demo</a></div>\n",
        "Code" to "<pre><code class=\"language-html\">&lt;div&gt;&lt;/div&gt;</code></pre>\n"
    )

    // Row 4: Spoiler, Steps, Table, Tabs + Navigation
    val row4 = listOf(
        "Spoiler" to "<details class=\"sp\"><summary>Click to reveal</summary><p>Hidden text</p></details>\n",
        "Accordion" to "<details class=\"ac\"><summary>FAQ Question?</summary><p>Answer text</p></details>\n",
        "ToC" to "<details class=\"sp toc\"><summary>Table of Contents</summary><div class=\"aToc\"></div></details>\n",
        "Steps" to "<ol class=\"steps\"><li>Step 1</li><li>Step 2</li></ol>\n",
        "Table" to "<div class=\"table bordered stripped\"><table><thead><tr><th>Title</th></tr></thead><tbody><tr><td>Data</td></tr></tbody></table></div>\n"
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        row1.forEach { (label, code) ->
            TypewriterKey(
                label = label,
                modifier = Modifier.weight(1f),
                onClick = { onInsertSnippet(code) }
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        row2.forEach { (label, code) ->
            TypewriterKey(
                label = label,
                modifier = Modifier.weight(1f),
                onClick = { onInsertSnippet(code) }
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        row3.forEach { (label, code) ->
            TypewriterKey(
                label = label,
                modifier = Modifier.weight(1f),
                onClick = { onInsertSnippet(code) }
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        row4.forEach { (label, code) ->
            TypewriterKey(
                label = label,
                modifier = Modifier.weight(1.1f),
                onClick = { onInsertSnippet(code) }
            )
        }
        TypewriterKey(
            label = "SPACE",
            isSpecial = true,
            modifier = Modifier.weight(1.3f),
            onClick = onSpace
        )
        TypewriterKey(
            label = "⌫",
            isSpecial = true,
            modifier = Modifier.weight(0.9f),
            onClick = onBackspace
        )
        TypewriterKey(
            label = "⏎",
            isSpecial = true,
            modifier = Modifier.weight(0.9f),
            onClick = onEnter
        )
    }
}
