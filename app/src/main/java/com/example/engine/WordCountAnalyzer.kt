package com.example.engine

data class WordCountMetrics(
    val words: Int,
    val charactersWithSpaces: Int,
    val charactersWithoutSpaces: Int,
    val sentences: Int,
    val paragraphs: Int,
    val htmlTagsCount: Int,
    val readingTimeMinutes: Double,
    val readingTimeString: String,
    val speakingTimeString: String,
    val seoRecommendation: String,
    val readabilityLevel: String
)

object WordCountAnalyzer {

    fun analyze(htmlOrPlainText: String): WordCountMetrics {
        if (htmlOrPlainText.isBlank()) {
            return WordCountMetrics(
                words = 0,
                charactersWithSpaces = 0,
                charactersWithoutSpaces = 0,
                sentences = 0,
                paragraphs = 0,
                htmlTagsCount = 0,
                readingTimeMinutes = 0.0,
                readingTimeString = "0 min read",
                speakingTimeString = "0 min speak",
                seoRecommendation = "Start typing or load a template to build your Blogger post.",
                readabilityLevel = "N/A"
            )
        }

        // Strip HTML tags for clean text analytics
        val stripped = htmlOrPlainText.replace(Regex("<[^>]*>"), " ")
        val normalized = stripped.replace(Regex("\\s+"), " ").trim()

        val words = if (normalized.isBlank()) 0 else {
            normalized.split(Regex("\\s+")).filter { it.isNotBlank() }.size
        }

        val charactersWithSpaces = htmlOrPlainText.length
        val charactersWithoutSpaces = htmlOrPlainText.filterNot { it.isWhitespace() }.length

        val tagCount = Regex("<[^>]+>").findAll(htmlOrPlainText).count()

        val paragraphs = if (normalized.isBlank()) 0 else {
            val pTags = Regex("</p>|<br\\s*/?>|\\n\\n+").findAll(htmlOrPlainText).count()
            if (pTags > 0) pTags + 1 else htmlOrPlainText.split(Regex("\\n+")).count { it.isNotBlank() }.coerceAtLeast(1)
        }

        val sentences = if (normalized.isBlank()) 0 else {
            val matches = Regex("[.!?]+").findAll(normalized).count()
            matches.coerceAtLeast(1)
        }

        // Standard reading speed: ~200-220 words per minute
        val readingMinutes = words / 200.0
        val readingSeconds = (readingMinutes * 60).toInt()
        val readTimeStr = when {
            words == 0 -> "0 min"
            readingSeconds < 60 -> "< 1 min read"
            readingSeconds < 120 -> "1 min read"
            else -> "${(readingSeconds + 30) / 60} mins read"
        }

        // Standard speaking speed: ~130 words per minute
        val speakingMinutes = words / 130.0
        val speakSeconds = (speakingMinutes * 60).toInt()
        val speakTimeStr = when {
            words == 0 -> "0 min"
            speakSeconds < 60 -> "< 1 min speak"
            else -> "${(speakSeconds + 30) / 60} mins speak"
        }

        val seoRec = when {
            words == 0 -> "Empty post: Add content to analyze."
            words < 300 -> "Thin Content (<300 words): Google AdSense & Blogger SEO prefer at least 600 words to avoid low value content notices."
            words in 300..599 -> "Moderate (300-599 words): Suitable for brief updates, but expand with subheadings (H2/H3) for better organic rankings."
            words in 600..1200 -> "Optimal (600-1,200 words): Ideal target for high-ranking Blogger articles and natural Google AdSense ad distribution."
            words in 1201..2000 -> "In-Depth (1,200-2,000 words): High topical authority, excellent dwell time, and higher AdSense RPM."
            else -> "Cornerstone / Pillar (>2,000 words): Comprehensive ultimate guide. Best for multiple AdSense in-article units."
        }

        // Simple Flesch reading ease estimation
        val avgWordsPerSentence = if (sentences > 0) words.toDouble() / sentences else 0.0
        val readability = when {
            words < 20 -> "Preliminary"
            avgWordsPerSentence <= 14 -> "Easy & Engaging (High User Retention)"
            avgWordsPerSentence <= 22 -> "Standard / Professional (Blogger Balanced)"
            else -> "Complex / Academic (Consider breaking sentences)"
        }

        return WordCountMetrics(
            words = words,
            charactersWithSpaces = charactersWithSpaces,
            charactersWithoutSpaces = charactersWithoutSpaces,
            sentences = sentences,
            paragraphs = paragraphs,
            htmlTagsCount = tagCount,
            readingTimeMinutes = readingMinutes,
            readingTimeString = readTimeStr,
            speakingTimeString = speakTimeStr,
            seoRecommendation = seoRec,
            readabilityLevel = readability
        )
    }
}
