package com.example.engine

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.model.CustomTypographyClass

data class AutoCompleteSuggestion(
    val id: String,
    val displayLabel: String,
    val prefixMatch: String,
    val insertSnippet: String,
    val caretOffsetFromInsert: Int = 0, // offset inside inserted snippet where caret should rest
    val category: String = "HTML",
    val description: String = ""
)

object CodeAutoCompleteEngine {

    val BASE_SUGGESTIONS = listOf(
        // Blogger jump break & structural
        AutoCompleteSuggestion(
            id = "sug_jump_break",
            displayLabel = "<!--more--> (Jump Break)",
            prefixMatch = "<!--m",
            insertSnippet = "<!--more-->\n",
            category = "Blogger",
            description = "Blogger post read-more divider"
        ),
        AutoCompleteSuggestion(
            id = "sug_comment",
            displayLabel = "<!-- Comment -->",
            prefixMatch = "<!--",
            insertSnippet = "<!--  -->",
            caretOffsetFromInsert = 5,
            category = "HTML",
            description = "HTML comment block"
        ),

        // OsunHive UI Heading & Lead
        AutoCompleteSuggestion(
            id = "sug_oh_h2",
            displayLabel = "<h2 class=\"oh-h2\"> (Major Heading)",
            prefixMatch = "<h2",
            insertSnippet = "<h2 class=\"oh-h2\"></h2>\n",
            caretOffsetFromInsert = 20,
            category = "OsunHive UI",
            description = "Major heading with gold bottom accent"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_h3",
            displayLabel = "<h3 class=\"oh-h3\"> (Subheading)",
            prefixMatch = "<h3",
            insertSnippet = "<h3 class=\"oh-h3\"></h3>\n",
            caretOffsetFromInsert = 20,
            category = "OsunHive UI",
            description = "Subheading with modern typography"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_lead",
            displayLabel = "<p class=\"oh-lead\"> (Lead Paragraph)",
            prefixMatch = "<p",
            insertSnippet = "<p class=\"oh-lead\"></p>\n",
            caretOffsetFromInsert = 19,
            category = "OsunHive UI",
            description = "Featured introductory paragraph"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_dropcap",
            displayLabel = "<span class=\"oh-dropcap\"> (Drop Cap)",
            prefixMatch = "drop",
            insertSnippet = "<span class=\"oh-dropcap\">A</span>",
            caretOffsetFromInsert = 25,
            category = "OsunHive UI",
            description = "Large editorial vintage drop letter"
        ),

        // OsunHive UI Alerts
        AutoCompleteSuggestion(
            id = "sug_oh_alert_info",
            displayLabel = "oh-alert-info (Blue Callout)",
            prefixMatch = "oh-alert",
            insertSnippet = "<div class=\"oh-alert oh-alert-info\"><span class=\"oh-alert-icon\">ℹ️</span><div class=\"oh-alert-text\"><strong>Note:</strong> </div></div>\n",
            caretOffsetFromInsert = 113,
            category = "OsunHive Alerts",
            description = "Informative blue note callout"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_alert_success",
            displayLabel = "oh-alert-success (Green Success)",
            prefixMatch = "oh-succ",
            insertSnippet = "<div class=\"oh-alert oh-alert-success\"><span class=\"oh-alert-icon\">✅</span><div class=\"oh-alert-text\"><strong>Success:</strong> </div></div>\n",
            caretOffsetFromInsert = 119,
            category = "OsunHive Alerts",
            description = "Green success message"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_alert_warning",
            displayLabel = "oh-alert-warning (Amber Notice)",
            prefixMatch = "oh-warn",
            insertSnippet = "<div class=\"oh-alert oh-alert-warning\"><span class=\"oh-alert-icon\">⚠️</span><div class=\"oh-alert-text\"><strong>Warning:</strong> </div></div>\n",
            caretOffsetFromInsert = 119,
            category = "OsunHive Alerts",
            description = "Amber caution callout"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_alert_danger",
            displayLabel = "oh-alert-danger (Red Warning)",
            prefixMatch = "oh-dang",
            insertSnippet = "<div class=\"oh-alert oh-alert-danger\"><span class=\"oh-alert-icon\">⛔</span><div class=\"oh-alert-text\"><strong>Caution:</strong> </div></div>\n",
            caretOffsetFromInsert = 118,
            category = "OsunHive Alerts",
            description = "Red critical notice"
        ),

        // OsunHive UI Buttons & Links
        AutoCompleteSuggestion(
            id = "sug_oh_btn_primary",
            displayLabel = "<a class=\"oh-btn\"> (Gold Button)",
            prefixMatch = "oh-btn",
            insertSnippet = "<a href=\"#\" class=\"oh-btn\">Action Button</a>",
            caretOffsetFromInsert = 27,
            category = "OsunHive Buttons",
            description = "Signal gold CTA button"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_btn_download",
            displayLabel = "oh-btn-download (Emerald CTA)",
            prefixMatch = "download",
            insertSnippet = "<a href=\"#\" class=\"oh-btn oh-btn-download\">Download File</a>",
            caretOffsetFromInsert = 43,
            category = "OsunHive Buttons",
            description = "High-CTR download button"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_btn_demo",
            displayLabel = "oh-btn-demo (Purple Live Demo)",
            prefixMatch = "demo",
            insertSnippet = "<a href=\"#\" class=\"oh-btn oh-btn-demo\" target=\"_blank\">Live Demo</a>",
            caretOffsetFromInsert = 55,
            category = "OsunHive Buttons",
            description = "Live demo action button"
        ),

        // OsunHive UI Interactive & Containers
        AutoCompleteSuggestion(
            id = "sug_oh_code_box",
            displayLabel = "oh-code-box (Code Snippet Box)",
            prefixMatch = "code",
            insertSnippet = "<div class=\"oh-code-box\"><div class=\"oh-code-header\"><span>HTML</span></div><pre class=\"oh-pre\"><code></code></pre></div>\n",
            caretOffsetFromInsert = 90,
            category = "OsunHive Code",
            description = "Dark terminal style code block"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_accordion",
            displayLabel = "oh-accordion (FAQ Accordion)",
            prefixMatch = "faq",
            insertSnippet = "<details class=\"oh-accordion\"><summary class=\"oh-summary\">Question title?</summary><div class=\"oh-content\"><p>Answer details here.</p></div></details>\n",
            caretOffsetFromInsert = 57,
            category = "OsunHive UI",
            description = "Expandable FAQ details accordion"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_table",
            displayLabel = "oh-table (Data Table)",
            prefixMatch = "table",
            insertSnippet = "<div class=\"oh-table-wrapper\"><table class=\"oh-table\"><thead><tr><th>Header 1</th><th>Header 2</th></tr></thead><tbody><tr><td>Data 1</td><td>Data 2</td></tr></tbody></table></div>\n",
            caretOffsetFromInsert = 73,
            category = "OsunHive UI",
            description = "Responsive striped data table"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_list_check",
            displayLabel = "oh-list-check (Feature Checklist)",
            prefixMatch = "list",
            insertSnippet = "<ul class=\"oh-list-check\">\n  <li>First feature</li>\n  <li>Second feature</li>\n</ul>\n",
            caretOffsetFromInsert = 33,
            category = "OsunHive UI",
            description = "Checkmark bullet list"
        ),
        AutoCompleteSuggestion(
            id = "sug_oh_backlink",
            displayLabel = "oh-footer-credit (OsunHive Backlink)",
            prefixMatch = "osun",
            insertSnippet = "<div class=\"oh-footer-credit\"><p>Formatted with <a href=\"https://www.osunhive.name.ng\" target=\"_blank\" rel=\"noopener\">OsunHive UI</a> • Join our Blogger community on <a href=\"https://t.me/Osunhive\" target=\"_blank\" rel=\"noopener\">Telegram @Osunhive</a></p></div>\n",
            caretOffsetFromInsert = 247,
            category = "Monetisation",
            description = "Traffic driving backlink to osunhive.name.ng & t.me/Osunhive"
        ),

        // Standard HTML
        AutoCompleteSuggestion(
            id = "sug_p",
            displayLabel = "<p></p> (Paragraph)",
            prefixMatch = "<p>",
            insertSnippet = "<p></p>\n",
            caretOffsetFromInsert = 3,
            category = "HTML"
        ),
        AutoCompleteSuggestion(
            id = "sug_b",
            displayLabel = "<strong></strong> (Bold)",
            prefixMatch = "<b",
            insertSnippet = "<strong></strong>",
            caretOffsetFromInsert = 8,
            category = "HTML"
        ),
        AutoCompleteSuggestion(
            id = "sug_i",
            displayLabel = "<em></em> (Italic)",
            prefixMatch = "<i",
            insertSnippet = "<em></em>",
            caretOffsetFromInsert = 4,
            category = "HTML"
        ),
        AutoCompleteSuggestion(
            id = "sug_u",
            displayLabel = "<u></u> (Underline)",
            prefixMatch = "<u",
            insertSnippet = "<u></u>",
            caretOffsetFromInsert = 3,
            category = "HTML"
        ),
        AutoCompleteSuggestion(
            id = "sug_a",
            displayLabel = "<a href=\"\"> (Link)",
            prefixMatch = "<a",
            insertSnippet = "<a href=\"https://\" target=\"_blank\" rel=\"noopener\"></a>",
            caretOffsetFromInsert = 50,
            category = "HTML"
        ),
        AutoCompleteSuggestion(
            id = "sug_mark",
            displayLabel = "<mark class=\"oh-mark\"> (Highlight)",
            prefixMatch = "<mark",
            insertSnippet = "<mark class=\"oh-mark\"></mark>",
            caretOffsetFromInsert = 22,
            category = "HTML"
        ),
        AutoCompleteSuggestion(
            id = "sug_blockquote",
            displayLabel = "<blockquote class=\"oh-blockquote\">",
            prefixMatch = "<block",
            insertSnippet = "<blockquote class=\"oh-blockquote\"><p>\"\"</p><cite class=\"oh-cite\">— Author</cite></blockquote>\n",
            caretOffsetFromInsert = 37,
            category = "HTML"
        ),
        AutoCompleteSuggestion(
            id = "sug_hr",
            displayLabel = "<hr class=\"oh-hr\" /> (Divider)",
            prefixMatch = "<hr",
            insertSnippet = "<hr class=\"oh-hr\" />\n",
            caretOffsetFromInsert = 21,
            category = "HTML"
        )
    )

    fun getSuggestions(
        currentText: String,
        selectionStart: Int,
        customClasses: List<CustomTypographyClass>
    ): List<AutoCompleteSuggestion> {
        val wordAtCursor = extractWordPrefix(currentText, selectionStart).lowercase()

        // Combine base suggestions with user custom classes
        val customSuggestions = customClasses.map { cls ->
            AutoCompleteSuggestion(
                id = "custom_${cls.id}",
                displayLabel = ".${cls.className} (${cls.name})",
                prefixMatch = cls.className.lowercase(),
                insertSnippet = cls.htmlTemplate.replace("{{text}}", "").replace("{{title}}", cls.name) + "\n",
                caretOffsetFromInsert = 0,
                category = "Custom Classes",
                description = cls.description
            )
        }

        val all = BASE_SUGGESTIONS + customSuggestions

        if (wordAtCursor.isEmpty()) {
            // Return top popular suggestions when idle
            return all.take(8)
        }

        // Rank suggestions matching word at cursor
        val matches = all.filter { sug ->
            sug.prefixMatch.lowercase().contains(wordAtCursor) ||
            sug.displayLabel.lowercase().contains(wordAtCursor) ||
            sug.id.lowercase().contains(wordAtCursor)
        }

        return if (matches.isNotEmpty()) matches else all.take(6)
    }

    private fun extractWordPrefix(text: String, pos: Int): String {
        if (pos <= 0 || pos > text.length) return ""
        var start = pos - 1
        while (start >= 0 && !text[start].isWhitespace()) {
            start--
        }
        return text.substring(start + 1, pos)
    }

    fun applySuggestion(
        current: TextFieldValue,
        suggestion: AutoCompleteSuggestion
    ): TextFieldValue {
        val text = current.text
        val pos = current.selection.start

        // Find prefix to replace
        var start = pos - 1
        while (start >= 0 && !text[start].isWhitespace() && text[start] != '<' && text[start] != '.') {
            start--
        }
        if (start < 0) start = 0
        if (start < pos && (text[start] == '<' || text[start] == '.')) {
            // Keep start at the boundary character or include it depending on suggestion
        }

        val replaceStart = if (start < pos) {
            // If prefix starts with '<' and suggestion starts with '<', replace from start
            if (suggestion.insertSnippet.startsWith("<") && text.getOrNull(start) == '<') {
                start
            } else {
                start + 1
            }
        } else pos

        val replaceEnd = current.selection.end.coerceAtLeast(pos)

        val before = text.substring(0, replaceStart)
        val after = text.substring(replaceEnd)
        val newText = before + suggestion.insertSnippet + after

        val newCaretPos = if (suggestion.caretOffsetFromInsert > 0) {
            (replaceStart + suggestion.caretOffsetFromInsert).coerceAtMost(newText.length)
        } else {
            (replaceStart + suggestion.insertSnippet.length).coerceAtMost(newText.length)
        }

        return TextFieldValue(newText, TextRange(newCaretPos))
    }
}
