package com.example.engine

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.model.CustomTypographyClass

data class AutoCompleteSuggestion(
    val id: String,
    val displayLabel: String,
    val prefixMatch: String,
    val insertSnippet: String,
    val caretOffsetFromInsert: Int = 0,
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
        AutoCompleteSuggestion(
            id = "sug_h2",
            displayLabel = "<h2> Subheading",
            prefixMatch = "<h",
            insertSnippet = "<h2></h2>",
            caretOffsetFromInsert = 4,
            category = "HTML",
            description = "Level 2 HTML Heading"
        ),
        AutoCompleteSuggestion(
            id = "sug_h3",
            displayLabel = "<h3> Sub-subheading",
            prefixMatch = "<h",
            insertSnippet = "<h3></h3>",
            caretOffsetFromInsert = 4,
            category = "HTML",
            description = "Level 3 HTML Heading"
        ),

        // OsunHive UI Video & Media
        AutoCompleteSuggestion(
            id = "sug_video_yt",
            displayLabel = "videoYt (YouTube 16:9)",
            prefixMatch = "video",
            insertSnippet = "<div class=\"videoYt\"><iframe src=\"https://www.youtube.com/embed/VIDEO_ID\" allowfullscreen></iframe></div>\n",
            caretOffsetFromInsert = 63,
            category = "Images and Video",
            description = "Responsive YouTube video embed"
        ),
        AutoCompleteSuggestion(
            id = "sug_zm_img",
            displayLabel = "zmImg (Zoomable Image)",
            prefixMatch = "zm",
            insertSnippet = "<span class=\"zmImg\"><img src=\"IMAGE-URL\" alt=\"Description\"/></span>",
            caretOffsetFromInsert = 29,
            category = "Images and Video",
            description = "Click-to-zoom interactive image"
        ),
        AutoCompleteSuggestion(
            id = "sug_grid_img",
            displayLabel = "psImg (Grid 2-Col Images)",
            prefixMatch = "psimg",
            insertSnippet = "<div class=\"psImg\"><img src=\"URL_1\" alt=\"1\"/><img src=\"URL_2\" alt=\"2\"/></div>\n",
            caretOffsetFromInsert = 28,
            category = "Images and Video",
            description = "Two images displayed side by side"
        ),

        // OsunHive UI Download Box & Safelink
        AutoCompleteSuggestion(
            id = "sug_dlbox",
            displayLabel = "dlBox (Download Card)",
            prefixMatch = "dlb",
            insertSnippet = "<div class=\"dlBox\">\n  <div class=\"fT\" data-text=\"ZIP\"></div>\n  <div class=\"fN\">\n    <span>Package.zip</span>\n    <span class=\"fS\">15 MB</span>\n  </div>\n  <a class=\"button safeL\" href=\"DOWNLOAD-URL\" aria-label=\"Download\"><i class=\"icon dl\"></i></a>\n</div>\n",
            caretOffsetFromInsert = 160,
            category = "Download Box",
            description = "OsunHive UI file download box with safelink"
        ),
        AutoCompleteSuggestion(
            id = "sug_safelink",
            displayLabel = "safeL (Safelink Button)",
            prefixMatch = "safel",
            insertSnippet = "<a class=\"button safeL\" href=\"REAL-URL\"><i class=\"icon dl\"></i>Download</a>",
            caretOffsetFromInsert = 29,
            category = "Safelink",
            description = "Safelink protected countdown button"
        ),

        // OsunHive UI Text and Layout
        AutoCompleteSuggestion(
            id = "sug_dropcap",
            displayLabel = "dropCap (Editorial Initial)",
            prefixMatch = "drop",
            insertSnippet = "<span class=\"dropCap\">T</span>",
            caretOffsetFromInsert = 22,
            category = "Text and Layout",
            description = "Large editorial initial letter"
        ),
        AutoCompleteSuggestion(
            id = "sug_pindent",
            displayLabel = "pIndent (Indented Paragraph)",
            prefixMatch = "pind",
            insertSnippet = "<p class=\"pIndent\">Your paragraph begins here.</p>\n",
            caretOffsetFromInsert = 19,
            category = "Text and Layout",
            description = "First-line indented narrative paragraph"
        ),
        AutoCompleteSuggestion(
            id = "sug_extl",
            displayLabel = "extL (External Link Arrow)",
            prefixMatch = "extl",
            insertSnippet = "<a class=\"extL\" href=\"https://\" target=\"_blank\" rel=\"noopener\">Link Text</a>",
            caretOffsetFromInsert = 29,
            category = "Text and Layout",
            description = "External link styled with arrow icon"
        ),

        // OsunHive UI Buttons
        AutoCompleteSuggestion(
            id = "sug_button_solid",
            displayLabel = "button (Solid Button)",
            prefixMatch = "btn",
            insertSnippet = "<a class=\"button\" href=\"URL\">Get Started</a>",
            caretOffsetFromInsert = 24,
            category = "Buttons",
            description = "OsunHive UI primary theme button"
        ),
        AutoCompleteSuggestion(
            id = "sug_button_outline",
            displayLabel = "button ln (Outline Button)",
            prefixMatch = "btnln",
            insertSnippet = "<a class=\"button ln\" href=\"URL\">Learn More</a>",
            caretOffsetFromInsert = 27,
            category = "Buttons",
            description = "Bordered outline button"
        ),
        AutoCompleteSuggestion(
            id = "sug_btnf_row",
            displayLabel = "btnF (Dual Button Row)",
            prefixMatch = "btnf",
            insertSnippet = "<div class=\"btnF\"><a class=\"button\" href=\"#\"><i class=\"icon dl\"></i>Download</a><a class=\"button ln\" href=\"#\"><i class=\"icon demo\"></i>Demo</a></div>\n",
            caretOffsetFromInsert = 41,
            category = "Buttons",
            description = "Paired action buttons row"
        ),

        // OsunHive UI Alerts & Notes
        AutoCompleteSuggestion(
            id = "sug_alert_info",
            displayLabel = "alert info (Blue Notice)",
            prefixMatch = "alert",
            insertSnippet = "<div class=\"alert info\"><strong>Info</strong> Essential information.</div>\n",
            caretOffsetFromInsert = 44,
            category = "Alerts and Notes",
            description = "Informative blue note callout"
        ),
        AutoCompleteSuggestion(
            id = "sug_alert_success",
            displayLabel = "alert success (Green Box)",
            prefixMatch = "alert",
            insertSnippet = "<div class=\"alert success\"><strong>Success</strong> Done successfully.</div>\n",
            caretOffsetFromInsert = 50,
            category = "Alerts and Notes",
            description = "Green success message"
        ),
        AutoCompleteSuggestion(
            id = "sug_alert_warning",
            displayLabel = "alert warning (Amber Box)",
            prefixMatch = "alert",
            insertSnippet = "<div class=\"alert warning\"><strong>Warning</strong> Please take note.</div>\n",
            caretOffsetFromInsert = 50,
            category = "Alerts and Notes",
            description = "Amber warning callout"
        ),
        AutoCompleteSuggestion(
            id = "sug_alert_error",
            displayLabel = "alert error (Red Alert)",
            prefixMatch = "alert",
            insertSnippet = "<div class=\"alert error\"><strong>Error</strong> Critical notice.</div>\n",
            caretOffsetFromInsert = 46,
            category = "Alerts and Notes",
            description = "Red cautionary callout"
        ),
        AutoCompleteSuggestion(
            id = "sug_note",
            displayLabel = "note (* Icon Note)",
            prefixMatch = "note",
            insertSnippet = "<p class=\"note\">Editorial note to clarify details.</p>\n",
            caretOffsetFromInsert = 16,
            category = "Alerts and Notes",
            description = "Asterisk icon note"
        ),

        // OsunHive UI Code & Highlight
        AutoCompleteSuggestion(
            id = "sug_pre_code",
            displayLabel = "pre code (Dark Code Block)",
            prefixMatch = "pre",
            insertSnippet = "<pre><code class=\"language-html\">\n\n</code></pre>\n",
            caretOffsetFromInsert = 34,
            category = "Code",
            description = "Syntax highlighted code block"
        ),
        AutoCompleteSuggestion(
            id = "sug_code_comment",
            displayLabel = "pre data-comment (Code Header)",
            prefixMatch = "pre",
            insertSnippet = "<pre data-comment=\"filename.ext\"><code>\n\n</code></pre>\n",
            caretOffsetFromInsert = 39,
            category = "Code",
            description = "Code block showing filename header"
        ),

        // OsunHive UI Spoilers & ToC
        AutoCompleteSuggestion(
            id = "sug_sp_spoiler",
            displayLabel = "details.sp (Click to Reveal)",
            prefixMatch = "spoil",
            insertSnippet = "<details class=\"sp\"><summary>Click to reveal</summary><p>Hidden details.</p></details>\n",
            caretOffsetFromInsert = 55,
            category = "Spoiler, Accordion, ToC",
            description = "Collapsible spoiler container"
        ),
        AutoCompleteSuggestion(
            id = "sug_accordion",
            displayLabel = "details.ac (FAQ Accordion)",
            prefixMatch = "accord",
            insertSnippet = "<details class=\"ac\"><summary>Frequently Asked Question?</summary><p>Answer text.</p></details>\n",
            caretOffsetFromInsert = 66,
            category = "Spoiler, Accordion, ToC",
            description = "Arrow toggle accordion"
        ),
        AutoCompleteSuggestion(
            id = "sug_toc_auto",
            displayLabel = "details.sp.toc (Auto ToC)",
            prefixMatch = "toc",
            insertSnippet = "<details class=\"sp toc\"><summary>Table of Contents</summary><div class=\"aToc\"></div></details>\n",
            caretOffsetFromInsert = 94,
            category = "Spoiler, Accordion, ToC",
            description = "Auto-generated Table of Contents"
        ),

        // OsunHive UI Lists
        AutoCompleteSuggestion(
            id = "sug_steps",
            displayLabel = "ol.steps (Numbered Steps)",
            prefixMatch = "steps",
            insertSnippet = "<ol class=\"steps\">\n  <li>First step</li>\n  <li>Second step</li>\n</ol>\n",
            caretOffsetFromInsert = 25,
            category = "Lists",
            description = "Numbered circular badges connected with line"
        ),
        AutoCompleteSuggestion(
            id = "sug_pros",
            displayLabel = "ul.pros (Green Check List)",
            prefixMatch = "pros",
            insertSnippet = "<ul class=\"pros\">\n  <li>Advantage 1</li>\n  <li>Advantage 2</li>\n</ul>\n",
            caretOffsetFromInsert = 24,
            category = "Lists",
            description = "Plus marker list"
        ),

        // OsunHive UI Tables
        AutoCompleteSuggestion(
            id = "sug_table_striped",
            displayLabel = "table.bordered.stripped (Styled Table)",
            prefixMatch = "table",
            insertSnippet = "<div class=\"table bordered stripped\">\n  <table>\n    <thead><tr><th>Item</th><th>Detail</th></tr></thead>\n    <tbody><tr><td>Row 1</td><td>Value 1</td></tr></tbody>\n  </table>\n</div>\n",
            caretOffsetFromInsert = 106,
            category = "Tables",
            description = "Responsive striped table"
        ),

        // OsunHive UI Tabs
        AutoCompleteSuggestion(
            id = "sug_tabs_css",
            displayLabel = "div.tabs (Switchable Radio Tabs)",
            prefixMatch = "tabs",
            insertSnippet = "<div class=\"tabs\">\n  <input id=\"t-0\" type=\"radio\" name=\"t1\" checked=\"checked\"/>\n  <input id=\"t-1\" type=\"radio\" name=\"t1\"/>\n  <div><label for=\"t-0\" data-text=\"Tab 1\"></label><label for=\"t-1\" data-text=\"Tab 2\"></label></div>\n  <div class=\"c-0\">Content 1</div>\n  <div class=\"c-1\">Content 2</div>\n</div>\n",
            caretOffsetFromInsert = 227,
            category = "Tabs",
            description = "CSS radio switchable tabs"
        )
    )

    fun getSuggestions(
        currentText: String,
        caretPosition: Int,
        customClasses: List<CustomTypographyClass> = emptyList()
    ): List<AutoCompleteSuggestion> {
        val safeCaret = caretPosition.coerceIn(0, currentText.length)
        val textBeforeCaret = currentText.substring(0, safeCaret)
        val lastWord = textBeforeCaret.substringAfterLast(' ').substringAfterLast('\n').substringAfterLast('\t')

        if (lastWord.length < 2) return emptyList()

        val customSuggestions = customClasses.map { cls ->
            AutoCompleteSuggestion(
                id = "custom_${cls.id}",
                displayLabel = "${cls.name} (.${cls.className})",
                prefixMatch = cls.className,
                insertSnippet = cls.htmlTemplate,
                caretOffsetFromInsert = cls.htmlTemplate.length,
                category = cls.category,
                description = cls.description
            )
        }

        val all = BASE_SUGGESTIONS + customSuggestions
        val query = lastWord.lowercase()

        return all.filter {
            it.prefixMatch.lowercase().contains(query) ||
            it.displayLabel.lowercase().contains(query) ||
            it.category.lowercase().contains(query)
        }.take(8)
    }

    fun applySuggestion(
        currentFieldValue: TextFieldValue,
        suggestion: AutoCompleteSuggestion
    ): TextFieldValue {
        val text = currentFieldValue.text
        val caret = currentFieldValue.selection.start.coerceIn(0, text.length)
        val textBefore = text.substring(0, caret)
        val lastDelimiterIndex = maxOf(
            textBefore.lastIndexOf(' '),
            textBefore.lastIndexOf('\n'),
            textBefore.lastIndexOf('\t'),
            textBefore.lastIndexOf('<')
        )

        val replaceStart = if (lastDelimiterIndex >= 0 && lastDelimiterIndex < caret) {
            if (textBefore[lastDelimiterIndex] == '<') lastDelimiterIndex else lastDelimiterIndex + 1
        } else {
            0
        }

        val newText = text.substring(0, replaceStart) + suggestion.insertSnippet + text.substring(caret)
        val targetCaret = (replaceStart + suggestion.caretOffsetFromInsert).coerceIn(0, newText.length)

        return TextFieldValue(
            text = newText,
            selection = TextRange(targetCaret)
        )
    }
}
