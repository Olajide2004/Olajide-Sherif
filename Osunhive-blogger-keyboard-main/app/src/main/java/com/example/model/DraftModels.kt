package com.example.model

data class SavedDraft(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val wordCount: Int,
    val charCount: Int,
    val isAutoSave: Boolean = true
)

sealed class AutosaveState {
    data object Idle : AutosaveState()
    data object Saving : AutosaveState()
    data class Saved(val timestamp: Long, val wordCount: Int) : AutosaveState()
    data class Error(val message: String) : AutosaveState()
}
