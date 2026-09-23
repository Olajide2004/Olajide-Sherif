package com.example.engine

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AutosaveState
import com.example.model.SavedDraft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class DraftAutosaveManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("blogger_draft_autosave_prefs", Context.MODE_PRIVATE)

    private val _autosaveState = MutableStateFlow<AutosaveState>(AutosaveState.Idle)
    val autosaveState: StateFlow<AutosaveState> = _autosaveState.asStateFlow()

    private val _drafts = MutableStateFlow<List<SavedDraft>>(emptyList())
    val drafts: StateFlow<List<SavedDraft>> = _drafts.asStateFlow()

    private var debounceJob: Job? = null
    private var lastSavedContent: String = ""

    companion object {
        private const val KEY_LATEST_CONTENT = "key_latest_draft_content"
        private const val KEY_LATEST_TITLE = "key_latest_draft_title"
        private const val KEY_DRAFTS_JSON = "key_saved_drafts_json"
        private const val MAX_REVISIONS = 25
    }

    init {
        loadDraftsFromPrefs()
    }

    private fun loadDraftsFromPrefs() {
        try {
            val jsonString = prefs.getString(KEY_DRAFTS_JSON, null)
            if (!jsonString.isNullOrEmpty()) {
                val jsonArray = JSONArray(jsonString)
                val list = mutableListOf<SavedDraft>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        SavedDraft(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            title = obj.optString("title", "Draft"),
                            content = obj.optString("content", ""),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            wordCount = obj.optInt("wordCount", 0),
                            charCount = obj.optInt("charCount", 0),
                            isAutoSave = obj.optBoolean("isAutoSave", true)
                        )
                    )
                }
                _drafts.value = list.sortedByDescending { it.timestamp }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun persistDrafts(list: List<SavedDraft>) {
        try {
            val jsonArray = JSONArray()
            for (draft in list) {
                val obj = JSONObject()
                obj.put("id", draft.id)
                obj.put("title", draft.title)
                obj.put("content", draft.content)
                obj.put("timestamp", draft.timestamp)
                obj.put("wordCount", draft.wordCount)
                obj.put("charCount", draft.charCount)
                obj.put("isAutoSave", draft.isAutoSave)
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_DRAFTS_JSON, jsonArray.toString()).apply()
            _drafts.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Called whenever editor text changes. Triggers debounced autosave.
     */
    fun onContentChanged(content: String, title: String? = null) {
        if (content == lastSavedContent) return

        debounceJob?.cancel()
        debounceJob = scope.launch(Dispatchers.IO) {
            delay(1500) // 1.5 second debounce
            saveDraftInternal(content, title ?: "Auto-saved Draft", isAutoSave = true)
        }
    }

    /**
     * Force save immediate draft or named manual snapshot
     */
    fun saveDraftNow(content: String, title: String? = null, isAutoSave: Boolean = false) {
        debounceJob?.cancel()
        scope.launch(Dispatchers.IO) {
            saveDraftInternal(content, title ?: if (isAutoSave) "Auto-saved Draft" else "Manual Snapshot", isAutoSave)
        }
    }

    private fun saveDraftInternal(content: String, title: String, isAutoSave: Boolean) {
        if (content.isBlank() && lastSavedContent.isBlank()) return

        _autosaveState.value = AutosaveState.Saving
        val metrics = WordCountAnalyzer.analyze(content)

        // Save latest working copy
        prefs.edit()
            .putString(KEY_LATEST_CONTENT, content)
            .putString(KEY_LATEST_TITLE, title)
            .putLong("key_latest_timestamp", System.currentTimeMillis())
            .apply()

        lastSavedContent = content

        val newDraft = SavedDraft(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            timestamp = System.currentTimeMillis(),
            wordCount = metrics.words,
            charCount = metrics.charactersWithSpaces,
            isAutoSave = isAutoSave
        )

        val updatedList = mutableListOf(newDraft)
        // Keep non-identical previous drafts
        for (item in _drafts.value) {
            if (item.content != content) {
                updatedList.add(item)
            }
        }
        val trimmed = updatedList.take(MAX_REVISIONS)
        persistDrafts(trimmed)

        _autosaveState.value = AutosaveState.Saved(
            timestamp = newDraft.timestamp,
            wordCount = metrics.words
        )
    }

    fun getLatestSavedContent(): String? {
        return prefs.getString(KEY_LATEST_CONTENT, null)
    }

    fun deleteDraft(id: String) {
        val updated = _drafts.value.filter { it.id != id }
        persistDrafts(updated)
    }

    fun clearAllDrafts() {
        prefs.edit().remove(KEY_DRAFTS_JSON).apply()
        _drafts.value = emptyList()
    }
}
