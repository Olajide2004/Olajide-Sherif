package com.example.engine

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

enum class DocumentFormattingPreset {
    OSUNHIVE_MAGAZINE,
    TECH_TUTORIAL,
    BLOGGER_CLEAN,
    LISTICLE_REVIEW
}

data class ConversionOptions(
    val preset: DocumentFormattingPreset = DocumentFormattingPreset.OSUNHIVE_MAGAZINE,
    val addDropCap: Boolean = true,
    val addLeadParagraph: Boolean = true,
    val addJumpBreak: Boolean = true,
    val addOsunhiveBacklink: Boolean = true,
    val detectHeadings: Boolean = true,
    val detectAlertBoxes: Boolean = true,
    val detectCodeBlocks: Boolean = true,
    val detectChecklists: Boolean = true
)

object DocumentToHtmlConverter {

    /**
     * Extracts text from document Uri (supports TXT, MD, HTML, and PDF text extraction).
     */
    fun extractTextFromUri(context: Context, uri: Uri): Pair<String, String> {
        var fileName = "Document"
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        } catch (_: Exception) {}

        val lowerName = fileName.lowercase()
        val text = try {
            if (lowerName.endsWith(".pdf")) {
                extractTextFromPdfStream(context, uri)
            } else {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).readText()
                } ?: ""
            }
        } catch (e: Exception) {
            "Error reading document: ${e.localizedMessage}"
        }

        return Pair(fileName, text)
    }

    /**
     * Lightweight PDF stream text extractor that decodes text tokens and stream blocks.
     */
    private fun extractTextFromPdfStream(context: Context, uri: Uri): String {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return ""
            val raw = String(bytes, Charsets.ISO_8859_1)

            val extractedLines = mutableListOf<String>()

            // Find BT ... ET blocks in PDF content streams
            val btPattern = Pattern.compile("BT\\s*(.*?)\\s*ET", Pattern.DOTALL)
            val matcher = btPattern.matcher(raw)

            var foundAny = false
            while (matcher.find()) {
                val block = matcher.group(1) ?: continue
                // Extract strings in parentheses: (Sample text) Tj or [(S)(a)(m)] TJ
                val textPattern = Pattern.compile("\\(([^)]+)\\)")
                val textMatcher = textPattern.matcher(block)
                val lineBuilder = StringBuilder()
                while (textMatcher.find()) {
                    val part = textMatcher.group(1)
                    if (!part.isNullOrBlank() && !part.startsWith("\\")) {
                        lineBuilder.append(part).append(" ")
                    }
                }
                val line = lineBuilder.toString().trim()
                if (line.isNotEmpty()) {
                    extractedLines.add(line)
                    foundAny = true
                }
            }

            if (foundAny) {
                extractedLines.joinToString("\n\n")
            } else {
                // Fallback: extract printable strings
                val fallbackLines = mutableListOf<String>()
                val stringPattern = Pattern.compile("\\(([A-Za-z0-9 ,.;:!?'\"\\-_/()]{4,})\\)")
                val fbMatcher = stringPattern.matcher(raw)
                while (fbMatcher.find()) {
                    fbMatcher.group(1)?.let { fallbackLines.add(it) }
                }
                if (fallbackLines.isNotEmpty()) {
                    fallbackLines.joinToString("\n")
                } else {
                    "Could not extract direct text stream from PDF. Please copy and paste the document text directly."
                }
            }
        } catch (e: Exception) {
            "PDF extraction fallback: ${e.localizedMessage}"
        }
    }

    /**
     * Converts raw document text into clean, high-performance Blogger HTML with OsunHive UI typography.
     */
    fun convertDocumentToBloggerHtml(
        rawText: String,
        documentTitle: String = "Formatted Post",
        options: ConversionOptions = ConversionOptions()
    ): String {
        if (rawText.isBlank()) return ""

        val lines = rawText.lines()
        val blocks = mutableListOf<String>()
        var inCodeBlock = false
        val currentCodeLines = mutableListOf<String>()
        var paragraphIndex = 0
        var isFirstParagraph = true

        val postTitle = cleanTitle(documentTitle)

        // Title header
        blocks.add("<!-- Post Title: $postTitle -->")
        blocks.add("<!-- Blogger Labels: OsunHiveUI, Blogging, ContentCreation -->\n")

        for (line in lines) {
            val trimmed = line.trim()

            // Handle code block boundaries
            if (options.detectCodeBlocks && trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    // Close code block
                    val codeContent = currentCodeLines.joinToString("\n")
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                    blocks.add(
                        """<div class="oh-code-box"><div class="oh-code-header"><span>CODE</span></div><pre class="oh-pre"><code>$codeContent</code></pre></div>"""
                    )
                    currentCodeLines.clear()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                }
                continue
            }

            if (inCodeBlock) {
                currentCodeLines.add(line)
                continue
            }

            if (trimmed.isEmpty()) continue

            // Markdown Headings or detected title lines
            if (options.detectHeadings && (trimmed.startsWith("# ") || trimmed.startsWith("## "))) {
                val headingText = trimmed.removePrefix("#").removePrefix("#").trim()
                blocks.add("""<h2 class="oh-h2">$headingText</h2>""")
                continue
            } else if (options.detectHeadings && (trimmed.startsWith("### ") || trimmed.startsWith("#### "))) {
                val headingText = trimmed.removePrefix("###").removePrefix("#").trim()
                blocks.add("""<h3 class="oh-h3">$headingText</h3>""")
                continue
            }

            // Detect Warning / Note alert lines
            if (options.detectAlertBoxes && isAlertLine(trimmed)) {
                val alertType = getAlertType(trimmed)
                val alertText = stripAlertPrefix(trimmed)
                val icon = when (alertType) {
                    "oh-alert-success" -> "✅"
                    "oh-alert-warning" -> "⚠️"
                    "oh-alert-danger" -> "⛔"
                    else -> "ℹ️"
                }
                blocks.add(
                    """<div class="oh-alert $alertType"><span class="oh-alert-icon">$icon</span><div class="oh-alert-text">$alertText</div></div>"""
                )
                continue
            }

            // Detect Blockquotes
            if (trimmed.startsWith("> ")) {
                val quoteText = trimmed.removePrefix(">").trim()
                blocks.add(
                    """<blockquote class="oh-blockquote"><p>"$quoteText"</p></blockquote>"""
                )
                continue
            }

            // Detect Bullet points or Checklists
            if (trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("• ")) {
                val itemText = trimmed.substring(2).trim()
                val listClass = if (options.detectChecklists) "oh-list-check" else "oh-list"
                blocks.add(
                    """<ul class="$listClass"><li>$itemText</li></ul>"""
                )
                continue
            }

            // Detect Numbered list items
            if (trimmed.matches("^\\d+\\.\\s+.*".toRegex())) {
                val itemText = trimmed.replaceFirst("^\\d+\\.\\s+".toRegex(), "")
                blocks.add(
                    """<ol class="oh-list-num"><li>$itemText</li></ol>"""
                )
                continue
            }

            // Standard Paragraph
            var pContent = escapeAndFormatInline(trimmed)

            if (isFirstParagraph) {
                if (options.addLeadParagraph) {
                    if (options.addDropCap && pContent.isNotEmpty()) {
                        val firstChar = pContent.first().uppercaseChar()
                        val rest = pContent.substring(1)
                        pContent = """<span class="oh-dropcap">$firstChar</span>$rest"""
                    }
                    blocks.add("""<p class="oh-lead">$pContent</p>""")
                } else {
                    blocks.add("""<p>$pContent</p>""")
                }

                // Add Blogger Jump Break right after introductory paragraph
                if (options.addJumpBreak) {
                    blocks.add("<!--more-->\n")
                }

                isFirstParagraph = false
            } else {
                blocks.add("""<p>$pContent</p>""")
            }

            paragraphIndex++
        }

        // Add Preset-specific enhancements
        when (options.preset) {
            DocumentFormattingPreset.TECH_TUTORIAL -> {
                blocks.add(
                    """<div class="oh-btn-group" style="text-align: center; margin: 28px 0;"><a href="#" class="oh-btn oh-btn-download">Download Tutorial Assets</a> <a href="#" class="oh-btn oh-btn-demo">View Live Demo</a></div>"""
                )
            }
            DocumentFormattingPreset.LISTICLE_REVIEW -> {
                blocks.add(
                    """<div class="oh-card" style="background:#f8fafc; border:1px solid #e2e8f0; border-radius:10px; padding:18px; margin:20px 0;"><h3 class="oh-h3" style="margin-top:0;">Summary Verdict</h3><p>⭐ <strong>Rating:</strong> 4.9 / 5.0 • <span class="oh-badge oh-badge-green">Verified</span></p><a href="#" class="oh-btn">Get Started Now</a></div>"""
                )
            }
            else -> {}
        }

        // Monetisation & Traffic generation backlink footer
        if (options.addOsunhiveBacklink) {
            blocks.add(
                """<div class="oh-footer-credit"><p>Formatted with <a href="https://www.osunhive.name.ng" target="_blank" rel="noopener">OsunHive UI</a> • Join our Blogger community on <a href="https://t.me/Osunhive" target="_blank" rel="noopener">Telegram @Osunhive</a></p></div>"""
            )
        }

        return blocks.joinToString("\n\n")
    }

    private fun cleanTitle(raw: String): String {
        return raw.substringBeforeLast('.')
            .replace('_', ' ')
            .replace('-', ' ')
            .trim()
            .ifEmpty { "Formatted Post" }
    }

    private fun isAlertLine(line: String): Boolean {
        val lower = line.lowercase()
        return lower.startsWith("note:") ||
                lower.startsWith("warning:") ||
                lower.startsWith("caution:") ||
                lower.startsWith("important:") ||
                lower.startsWith("tip:") ||
                lower.startsWith("success:") ||
                lower.startsWith("danger:")
    }

    private fun getAlertType(line: String): String {
        val lower = line.lowercase()
        return when {
            lower.startsWith("success:") -> "oh-alert-success"
            lower.startsWith("warning:") || lower.startsWith("caution:") -> "oh-alert-warning"
            lower.startsWith("danger:") -> "oh-alert-danger"
            else -> "oh-alert-info"
        }
    }

    private fun stripAlertPrefix(line: String): String {
        return line.replaceFirst("^(?i)(note|warning|caution|important|tip|success|danger):\\s*".toRegex(), "")
    }

    private fun escapeAndFormatInline(text: String): String {
        var res = text
        // Bold **text**
        res = res.replace("\\*\\*(.*?)\\*\\*".toRegex(), "<strong>$1</strong>")
        // Italic *text*
        res = res.replace("(?<!\\*)\\*(?!\\*)(.*?)\\*".toRegex(), "<em>$1</em>")
        // Links [anchor](url)
        res = res.replace("\\[(.*?)\\]\\((.*?)\\)".toRegex(), "<a href=\"$2\" target=\"_blank\" rel=\"noopener\">$1</a>")
        return res
    }
}
