package com.example.engine

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import com.example.model.ClipboardEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ClipboardHistoryManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("blogger_clipboard_history_prefs", Context.MODE_PRIVATE)

    private val clipboardManager: ClipboardManager? =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

    private val _history = MutableStateFlow<List<ClipboardEntry>>(emptyList())
    val history: StateFlow<List<ClipboardEntry>> = _history.asStateFlow()

    private var lastRecordedText: String = ""

    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        checkSystemClipboard()
    }

    companion object {
        private const val KEY_HISTORY_JSON = "key_clipboard_history_json"
        private const val MAX_HISTORY_ITEMS = 50
    }

    init {
        loadHistoryFromPrefs()
        startListening()
        checkSystemClipboard()
    }

    fun startListening() {
        try {
            clipboardManager?.addPrimaryClipChangedListener(clipListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopListening() {
        try {
            clipboardManager?.removePrimaryClipChangedListener(clipListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadHistoryFromPrefs() {
        try {
            val jsonString = prefs.getString(KEY_HISTORY_JSON, null)
            if (!jsonString.isNullOrEmpty()) {
                val jsonArray = JSONArray(jsonString)
                val list = mutableListOf<ClipboardEntry>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        ClipboardEntry(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            text = obj.optString("text", ""),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            wordCount = obj.optInt("wordCount", 0),
                            charCount = obj.optInt("charCount", 0),
                            isPinned = obj.optBoolean("isPinned", false),
                            previewTitle = obj.optString("previewTitle", "")
                        )
                    )
                }
                _history.value = sortList(list)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun persistHistory(list: List<ClipboardEntry>) {
        try {
            val jsonArray = JSONArray()
            for (entry in list) {
                val obj = JSONObject()
                obj.put("id", entry.id)
                obj.put("text", entry.text)
                obj.put("timestamp", entry.timestamp)
                obj.put("wordCount", entry.wordCount)
                obj.put("charCount", entry.charCount)
                obj.put("isPinned", entry.isPinned)
                obj.put("previewTitle", entry.previewTitle)
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_HISTORY_JSON, jsonArray.toString()).apply()
            _history.value = sortList(list)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sortList(list: List<ClipboardEntry>): List<ClipboardEntry> {
        return list.sortedWith(
            compareByDescending<ClipboardEntry> { it.isPinned }
                .thenByDescending { it.timestamp }
        )
    }

    fun checkSystemClipboard() {
        try {
            val clip = clipboardManager?.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0)?.coerceToText(context)?.toString() ?: ""
                if (text.isNotBlank() && text != lastRecordedText) {
                    addClip(text)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addClip(text: String, isPinned: Boolean = false) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        lastRecordedText = trimmed

        val wordCount = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val charCount = trimmed.length
        val previewTitle = if (trimmed.length > 50) {
            trimmed.substring(0, 47).replace("\n", " ") + "..."
        } else {
            trimmed.replace("\n", " ")
        }

        val newEntry = ClipboardEntry(
            id = UUID.randomUUID().toString(),
            text = text,
            timestamp = System.currentTimeMillis(),
            wordCount = wordCount,
            charCount = charCount,
            isPinned = isPinned,
            previewTitle = previewTitle
        )

        // Deduplicate existing identical clip text
        val filtered = _history.value.filter { it.text.trim() != trimmed }
        val updated = mutableListOf(newEntry).apply { addAll(filtered) }.take(MAX_HISTORY_ITEMS)
        persistHistory(updated)
    }

    fun togglePin(id: String) {
        val updated = _history.value.map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }
        persistHistory(updated)
    }

    fun deleteClip(id: String) {
        val updated = _history.value.filter { it.id != id }
        persistHistory(updated)
    }

    fun clearAll() {
        // Keep pinned clips if any
        val keptPinned = _history.value.filter { it.isPinned }
        persistHistory(keptPinned)
    }

    fun copyToClipboard(entry: ClipboardEntry) {
        try {
            val clip = ClipData.newPlainText("Blogger Clip", entry.text)
            lastRecordedText = entry.text.trim()
            clipboardManager?.setPrimaryClip(clip)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
