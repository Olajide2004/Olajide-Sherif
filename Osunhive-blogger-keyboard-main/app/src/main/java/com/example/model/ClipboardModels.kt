package com.example.model

data class ClipboardEntry(
    val id: String,
    val text: String,
    val timestamp: Long,
    val wordCount: Int,
    val charCount: Int,
    val isPinned: Boolean = false,
    val previewTitle: String = ""
)
