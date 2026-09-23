package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.AdSensePresets
import com.example.engine.AutoTyperEngine
import com.example.engine.AutoCompleteSuggestion
import com.example.engine.BloggerChromeTabHelper
import com.example.engine.ClipboardHistoryManager
import com.example.engine.CodeAutoCompleteEngine
import com.example.engine.ConversionOptions
import com.example.engine.DocumentToHtmlConverter
import com.example.engine.DraftAutosaveManager
import com.example.engine.TypewriterFeedback
import com.example.engine.WordCountAnalyzer
import com.example.engine.WordCountMetrics
import com.example.model.AutoTypeConfig
import com.example.model.AutoTypeState
import com.example.model.AutosaveState
import com.example.model.BloggerPresets
import com.example.model.ClipboardEntry
import com.example.model.CustomClassPresets
import com.example.model.CustomTypographyClass
import com.example.model.KeyboardLayout
import com.example.model.LoadedFileInfo
import com.example.model.SavedDraft
import com.example.model.SnippetItem
import com.example.ui.components.InjectionMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

enum class AppMode {
    WORKBENCH,
    BLOGGER_LIVE,
    ADSENSE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()
    val feedback = TypewriterFeedback(context)

    private val _appMode = MutableStateFlow(AppMode.WORKBENCH)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    private val _lastKeywords = MutableStateFlow("SEO Tips, Blogger, Content Marketing")
    val lastKeywords: StateFlow<String> = _lastKeywords.asStateFlow()

    // Active webView reference for direct web typing
    var attachedWebView: android.webkit.WebView? = null

    private val _isWebAutoTyping = MutableStateFlow(false)
    val isWebAutoTyping: StateFlow<Boolean> = _isWebAutoTyping.asStateFlow()

    private var webAutoTypeJob: kotlinx.coroutines.Job? = null

    private val _editorValue = MutableStateFlow(
        TextFieldValue(BloggerPresets.TEMPLATES.first().content)
    )
    val editorValue: StateFlow<TextFieldValue> = _editorValue.asStateFlow()

    private val undoStack = mutableListOf<TextFieldValue>()
    private val redoStack = mutableListOf<TextFieldValue>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    private val _customClasses = MutableStateFlow<List<CustomTypographyClass>>(CustomClassPresets.INITIAL_CUSTOM_CLASSES)
    val customClasses: StateFlow<List<CustomTypographyClass>> = _customClasses.asStateFlow()

    private val _loadedFileInfo = MutableStateFlow<LoadedFileInfo?>(
        LoadedFileInfo("Blogger SEO Post Template.html", "HTML", BloggerPresets.TEMPLATES.first().content.length, BloggerPresets.TEMPLATES.first().content.count { it == '\n' } + 1)
    )
    val loadedFileInfo: StateFlow<LoadedFileInfo?> = _loadedFileInfo.asStateFlow()

    private val _keyboardLayout = MutableStateFlow(KeyboardLayout.QWERTY)
    val keyboardLayout: StateFlow<KeyboardLayout> = _keyboardLayout.asStateFlow()

    private val _config = MutableStateFlow(AutoTypeConfig())
    val config: StateFlow<AutoTypeConfig> = _config.asStateFlow()

    // Auto-save Draft Manager
    val draftAutosaveManager: DraftAutosaveManager = DraftAutosaveManager(context, viewModelScope)
    val autosaveState: StateFlow<AutosaveState> = draftAutosaveManager.autosaveState
    val savedDrafts: StateFlow<List<SavedDraft>> = draftAutosaveManager.drafts

    // Clipboard History Manager
    val clipboardHistoryManager: ClipboardHistoryManager = ClipboardHistoryManager(context)
    val clipboardHistory: StateFlow<List<ClipboardEntry>> = clipboardHistoryManager.history

    // AutoTyper Engine
    val autoTyperEngine: AutoTyperEngine = AutoTyperEngine(
        scope = viewModelScope,
        feedback = feedback,
        onCharTyped = { chunk, _ ->
            viewModelScope.launch {
                appendOrInsertChunk(chunk)
            }
        }
    )

    init {
        // Restore latest saved draft if present, otherwise default template
        val latestDraft = draftAutosaveManager.getLatestSavedContent()
        if (!latestDraft.isNullOrBlank()) {
            _editorValue.value = TextFieldValue(latestDraft, TextRange(latestDraft.length))
            _loadedFileInfo.value = LoadedFileInfo("Restored Draft.html", "HTML", latestDraft.length, latestDraft.count { it == '\n' } + 1)
        }
        // Initialize auto-typer source
        autoTyperEngine.setSourceText(_editorValue.value.text, startFromBeginning = true)
    }

    fun onEditorChange(newValue: TextFieldValue) {
        pushUndo(_editorValue.value)
        _editorValue.value = newValue
        draftAutosaveManager.onContentChanged(newValue.text)
    }

    private fun pushUndo(state: TextFieldValue) {
        undoStack.add(state)
        if (undoStack.size > 100) {
            undoStack.removeAt(0)
        }
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.size - 1)
            redoStack.add(_editorValue.value)
            _editorValue.value = prev
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = true
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.size - 1)
            undoStack.add(_editorValue.value)
            _editorValue.value = next
            _canUndo.value = true
            _canRedo.value = redoStack.isNotEmpty()
        }
    }

    fun setKeyboardLayout(layout: KeyboardLayout) {
        _keyboardLayout.value = layout
    }

    fun updateConfig(newConfig: AutoTypeConfig) {
        _config.value = newConfig
    }

    fun startAutoType() {
        val currentText = _editorValue.value.text
        if (currentText.isEmpty()) {
            Toast.makeText(context, "Editor is empty. Load a file or template first.", Toast.LENGTH_SHORT).show()
            return
        }
        autoTyperEngine.setSourceText(currentText, startFromBeginning = true)
        // Clear editor so typewriter fills it dynamically
        pushUndo(_editorValue.value)
        _editorValue.value = TextFieldValue("", TextRange(0))
        autoTyperEngine.start(_config.value)
    }

    fun autoTypeSnippet(snippet: String) {
        if (snippet.isEmpty()) return
        autoTyperEngine.setSourceText(snippet, startFromBeginning = true)
        autoTyperEngine.start(_config.value)
    }

    fun pauseAutoType() {
        autoTyperEngine.pause()
    }

    fun resumeAutoType() {
        autoTyperEngine.resume(_config.value)
    }

    fun stopAutoType() {
        autoTyperEngine.stop()
    }

    fun resetAutoType() {
        autoTyperEngine.reset()
    }

    private fun appendOrInsertChunk(chunk: String) {
        val current = _editorValue.value
        val newText = current.text + chunk
        _editorValue.value = TextFieldValue(newText, TextRange(newText.length))
    }

    fun setAppMode(mode: AppMode) {
        _appMode.value = mode
    }

    fun startWebAutoType(webView: android.webkit.WebView) {
        attachedWebView = webView
        webAutoTypeJob?.cancel()
        _isWebAutoTyping.value = true
        val text = _editorValue.value.text
        if (text.isEmpty()) {
            Toast.makeText(context, "Editor content is empty", Toast.LENGTH_SHORT).show()
            _isWebAutoTyping.value = false
            return
        }

        webAutoTypeJob = viewModelScope.launch {
            val baseDelay = (1000L / _config.value.speedCharsPerSec.coerceAtLeast(1)).coerceAtLeast(15L)
            var i = 0
            while (i < text.length && _isWebAutoTyping.value) {
                val ch = text[i].toString()
                com.example.ui.components.typeCharIntoWebView(webView, ch)
                feedback.playKeyFeedback(_config.value.soundEnabled, _config.value.hapticsEnabled)

                // Human jitter
                val jitterRange = (baseDelay * (_config.value.jitterPercent / 100f)).toLong()
                val actualDelay = if (jitterRange > 0) {
                    baseDelay + (-jitterRange..jitterRange).random()
                } else baseDelay

                kotlinx.coroutines.delay(actualDelay.coerceAtLeast(10L))
                i++
            }
            _isWebAutoTyping.value = false
            Toast.makeText(context, "Completed typing into Blogger!", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopWebAutoType() {
        _isWebAutoTyping.value = false
        webAutoTypeJob?.cancel()
        webAutoTypeJob = null
    }

    // Manual typing actions
    fun typeText(text: String) {
        feedback.playKeyFeedback(_config.value.soundEnabled, _config.value.hapticsEnabled)

        if (_appMode.value == AppMode.BLOGGER_LIVE && attachedWebView != null) {
            com.example.ui.components.typeCharIntoWebView(attachedWebView!!, text)
            return
        }

        val current = _editorValue.value
        val full = current.text
        val selStart = current.selection.min
        val selEnd = current.selection.max
        pushUndo(current)

        val newText = full.substring(0, selStart) + text + full.substring(selEnd)
        val newCursor = selStart + text.length
        _editorValue.value = TextFieldValue(newText, TextRange(newCursor))
    }

    fun backspace() {
        feedback.playKeyFeedback(_config.value.soundEnabled, _config.value.hapticsEnabled)

        if (_appMode.value == AppMode.BLOGGER_LIVE && attachedWebView != null) {
            attachedWebView?.evaluateJavascript(
                """
                (function() {
                    var el = document.activeElement;
                    if (el && (el.tagName === 'TEXTAREA' || el.tagName === 'INPUT')) {
                        var s = el.selectionStart;
                        if (s > 0) {
                            el.value = el.value.substring(0, s - 1) + el.value.substring(s);
                            el.selectionStart = el.selectionEnd = s - 1;
                            el.dispatchEvent(new Event('input', { bubbles: true }));
                        }
                    } else if (el && el.isContentEditable) {
                        document.execCommand('delete', false);
                    }
                })();
                """.trimIndent(), null
            )
            return
        }

        val current = _editorValue.value
        val full = current.text
        if (full.isEmpty()) return
        pushUndo(current)

        val selStart = current.selection.min
        val selEnd = current.selection.max

        if (selStart != selEnd) {
            val newText = full.substring(0, selStart) + full.substring(selEnd)
            _editorValue.value = TextFieldValue(newText, TextRange(selStart))
        } else if (selStart > 0) {
            val newText = full.substring(0, selStart - 1) + full.substring(selStart)
            _editorValue.value = TextFieldValue(newText, TextRange(selStart - 1))
        }
    }

    fun enter() {
        typeText("\n")
    }

    fun space() {
        typeText(" ")
    }

    fun clearEditor() {
        pushUndo(_editorValue.value)
        _editorValue.value = TextFieldValue("", TextRange(0))
        autoTyperEngine.setSourceText("", startFromBeginning = true)
        Toast.makeText(context, "Editor cleared", Toast.LENGTH_SHORT).show()
    }

    fun loadTemplate(template: SnippetItem) {
        pushUndo(_editorValue.value)
        _editorValue.value = TextFieldValue(template.content, TextRange(template.content.length))
        _loadedFileInfo.value = LoadedFileInfo(
            name = template.title,
            type = if (template.isHtml) "HTML" else "TXT",
            characterCount = template.content.length,
            lineCount = template.content.count { it == '\n' } + 1
        )
        autoTyperEngine.setSourceText(template.content, startFromBeginning = true)
        Toast.makeText(context, "Loaded ${template.title}", Toast.LENGTH_SHORT).show()
    }

    fun loadFileFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                var fileName = "Document"
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex)
                    }
                }

                val content = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BufferedReader(InputStreamReader(stream)).readText()
                } ?: ""

                if (content.isNotEmpty()) {
                    pushUndo(_editorValue.value)
                    _editorValue.value = TextFieldValue(content, TextRange(content.length))
                    val ext = fileName.substringAfterLast('.', "TXT").uppercase()
                    _loadedFileInfo.value = LoadedFileInfo(
                        name = fileName,
                        type = ext,
                        characterCount = content.length,
                        lineCount = content.count { it == '\n' } + 1
                    )
                    autoTyperEngine.setSourceText(content, startFromBeginning = true)
                    Toast.makeText(context, "Loaded $fileName (${content.length} chars)", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "File is empty", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun injectKeywords(keywords: String, mode: InjectionMode) {
        val current = _editorValue.value
        val full = current.text
        pushUndo(current)

        val cleanKeywords = keywords.trim()
        _lastKeywords.value = cleanKeywords
        val updatedText: String

        when (mode) {
            InjectionMode.AT_CARET -> {
                val selStart = current.selection.min
                val selEnd = current.selection.max
                updatedText = full.substring(0, selStart) + cleanKeywords + full.substring(selEnd)
            }
            InjectionMode.REPLACE_PLACEHOLDERS -> {
                updatedText = full
                    .replace("{{keywords}}", cleanKeywords)
                    .replace("{{keyword}}", cleanKeywords.split(',').firstOrNull()?.trim() ?: cleanKeywords)
                    .replace("{{tags}}", cleanKeywords)
            }
            InjectionMode.WRAP_HIGHLIGHT -> {
                val tags = cleanKeywords.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                var result = full
                tags.forEach { tag ->
                    result = result.replace(
                        tag,
                        "<mark style=\"background-color: #fff9c4; padding: 2px 4px;\">$tag</mark>"
                    )
                }
                updatedText = result
            }
            InjectionMode.GENERATE_BLOGGER_LABELS -> {
                val block = buildString {
                    append("\n<!-- ======================================= -->\n")
                    append("<!-- BLOGGER LABELS: $cleanKeywords -->\n")
                    append("<!-- META KEYWORDS: $cleanKeywords -->\n")
                    append("<div class=\"blogger-post-labels\" style=\"font-size: 12px; color: #5f6368; margin-top: 20px;\">\n")
                    cleanKeywords.split(',').forEach { kw ->
                        val k = kw.trim()
                        if (k.isNotEmpty()) {
                            append("  <span style=\"background:#e8f0fe;color:#1a73e8;padding:3px 8px;border-radius:12px;margin-right:6px;\">#$k</span>\n")
                        }
                    }
                    append("</div>\n<!-- ======================================= -->\n")
                }
                updatedText = full + block
            }
        }

        _editorValue.value = TextFieldValue(updatedText, TextRange(updatedText.length))
        autoTyperEngine.setSourceText(updatedText, startFromBeginning = false)
        Toast.makeText(context, "Keywords injected successfully!", Toast.LENGTH_SHORT).show()
    }

    fun copyToClipboard(text: String, label: String = "Blogger Post Content") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            clipboardHistoryManager.addClip(text)
            Toast.makeText(context, "Copied ${text.length} characters to clipboard", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Clipboard error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Draft Autosave & Revisions
    fun restoreDraft(draft: SavedDraft) {
        pushUndo(_editorValue.value)
        _editorValue.value = TextFieldValue(draft.content, TextRange(draft.content.length))
        _loadedFileInfo.value = LoadedFileInfo(
            name = draft.title,
            type = "HTML",
            characterCount = draft.charCount,
            lineCount = draft.content.count { it == '\n' } + 1
        )
        autoTyperEngine.setSourceText(draft.content, startFromBeginning = true)
        Toast.makeText(context, "Restored '${draft.title}'", Toast.LENGTH_SHORT).show()
    }

    fun saveManualDraftSnapshot(title: String) {
        val current = _editorValue.value.text
        if (current.isBlank()) {
            Toast.makeText(context, "Cannot save empty draft", Toast.LENGTH_SHORT).show()
            return
        }
        draftAutosaveManager.saveDraftNow(current, title = title, isAutoSave = false)
        Toast.makeText(context, "Saved snapshot: $title", Toast.LENGTH_SHORT).show()
    }

    fun deleteDraft(id: String) {
        draftAutosaveManager.deleteDraft(id)
        Toast.makeText(context, "Draft revision removed", Toast.LENGTH_SHORT).show()
    }

    fun clearAllDrafts() {
        draftAutosaveManager.clearAllDrafts()
        Toast.makeText(context, "All draft revisions cleared", Toast.LENGTH_SHORT).show()
    }

    // Clipboard History Actions
    fun togglePinClip(id: String) {
        clipboardHistoryManager.togglePin(id)
    }

    fun deleteClip(id: String) {
        clipboardHistoryManager.deleteClip(id)
    }

    fun clearClipboardHistory() {
        clipboardHistoryManager.clearAll()
        Toast.makeText(context, "Clipboard history cleared", Toast.LENGTH_SHORT).show()
    }

    fun copyClipToClipboard(entry: ClipboardEntry) {
        clipboardHistoryManager.copyToClipboard(entry)
        Toast.makeText(context, "Copied clip to clipboard", Toast.LENGTH_SHORT).show()
    }

    // Word Count Analytics
    fun getWordCountMetrics(): WordCountMetrics {
        return WordCountAnalyzer.analyze(_editorValue.value.text)
    }

    // Google AdSense Navigation and Insertion
    fun openAdSenseInChromeTab(url: String = AdSensePresets.DEFAULT_ADSENSE_URL) {
        BloggerChromeTabHelper.openBloggerInChromeTab(context, url)
    }

    fun insertAdSenseSnippet(snippet: String) {
        insertHtmlAtCaret("\n$snippet\n")
        Toast.makeText(context, "Inserted AdSense snippet into post", Toast.LENGTH_SHORT).show()
    }

    fun openBloggerDraft() {
        openBloggerInChromeTab()
    }

    fun openBloggerInChromeTab(url: String = "https://draft.blogger.com/go/create-post") {
        BloggerChromeTabHelper.openBloggerInChromeTab(context, url)
    }

    fun addCustomClass(customClass: CustomTypographyClass) {
        _customClasses.value = _customClasses.value + customClass
        Toast.makeText(context, "Added class .${customClass.className}", Toast.LENGTH_SHORT).show()
    }

    fun deleteCustomClass(classId: String) {
        _customClasses.value = _customClasses.value.filter { it.id != classId }
        Toast.makeText(context, "Removed custom class", Toast.LENGTH_SHORT).show()
    }

    fun copyCombinedCss() {
        val css = CustomClassPresets.generateFullCombinedCss(_customClasses.value)
        copyToClipboard(css, "Full Blogger CSS")
    }

    fun insertHtmlAtCaret(html: String) {
        val current = _editorValue.value
        val full = current.text
        pushUndo(current)
        val start = current.selection.min
        val end = current.selection.max
        val updated = full.substring(0, start) + html + full.substring(end)
        val newPos = start + html.length
        _editorValue.value = TextFieldValue(updated, TextRange(newPos))
        autoTyperEngine.setSourceText(updated, startFromBeginning = false)
    }

    fun applyAutoCompleteSuggestion(suggestion: AutoCompleteSuggestion) {
        val current = _editorValue.value
        pushUndo(current)
        val result = CodeAutoCompleteEngine.applySuggestion(current, suggestion)
        _editorValue.value = result
        autoTyperEngine.setSourceText(result.text, startFromBeginning = false)
    }

    fun applyFormattingTag(tag: String, attributes: String = "") {
        val current = _editorValue.value
        val text = current.text
        val start = current.selection.min
        val end = current.selection.max
        pushUndo(current)

        val selectedText = if (start != end) text.substring(start, end) else "text"
        val attrStr = if (attributes.isNotBlank()) " $attributes" else ""
        val replacement = when (tag) {
            "!--more--" -> "<!--more-->\n"
            "hr" -> "<hr class=\"oh-hr\" />\n"
            else -> "<$tag$attrStr>$selectedText</$tag>"
        }

        val updated = text.substring(0, start) + replacement + text.substring(end)
        val newPos = start + replacement.length
        _editorValue.value = TextFieldValue(updated, TextRange(newPos))
        autoTyperEngine.setSourceText(updated, startFromBeginning = false)
    }

    fun convertAndLoadDocument(uri: Uri) {
        viewModelScope.launch {
            try {
                val (name, rawText) = DocumentToHtmlConverter.extractTextFromUri(context, uri)
                if (rawText.isNotBlank()) {
                    val html = DocumentToHtmlConverter.convertDocumentToBloggerHtml(
                        rawText = rawText,
                        documentTitle = name
                    )
                    pushUndo(_editorValue.value)
                    _editorValue.value = TextFieldValue(html, TextRange(html.length))
                    _loadedFileInfo.value = LoadedFileInfo(
                        name = name,
                        type = "HTML (Converted)",
                        characterCount = html.length,
                        lineCount = html.count { it == '\n' } + 1
                    )
                    autoTyperEngine.setSourceText(html, startFromBeginning = true)
                    Toast.makeText(context, "Converted $name to OsunHive HTML (${html.length} chars)", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Document was empty", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to convert document: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun openBloggerInChrome() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://draft.blogger.com/")).apply {
                setPackage("com.android.chrome")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://draft.blogger.com/")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallback)
            } catch (_: Exception) {
                Toast.makeText(context, "Could not open Chrome browser", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sharePostContent() {
        try {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, _editorValue.value.text)
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Blogger Post Content").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Share error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoTyperEngine.stop()
        feedback.release()
    }
}
