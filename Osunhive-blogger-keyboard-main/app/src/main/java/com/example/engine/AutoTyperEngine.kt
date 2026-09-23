package com.example.engine

import com.example.model.AutoTypeConfig
import com.example.model.AutoTypeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Random

class AutoTyperEngine(
    private val scope: CoroutineScope,
    private val feedback: TypewriterFeedback,
    private val onCharTyped: (String, Char?) -> Unit
) {
    private val random = Random()
    private var typingJob: Job? = null

    private val _state = MutableStateFlow(AutoTypeState.IDLE)
    val state: StateFlow<AutoTypeState> = _state.asStateFlow()

    private val _activeChar = MutableStateFlow<Char?>(null)
    val activeChar: StateFlow<Char?> = _activeChar.asStateFlow()

    private val _countdownSec = MutableStateFlow(0)
    val countdownSec: StateFlow<Int> = _countdownSec.asStateFlow()

    private val _typedCount = MutableStateFlow(0)
    val typedCount: StateFlow<Int> = _typedCount.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    private val _progressFraction = MutableStateFlow(0f)
    val progressFraction: StateFlow<Float> = _progressFraction.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _estimatedRemainingSeconds = MutableStateFlow(0L)
    val estimatedRemainingSeconds: StateFlow<Long> = _estimatedRemainingSeconds.asStateFlow()

    private var sourceFullText: String = ""
    private var currentIndex: Int = 0
    private var startTimeMillis: Long = 0L
    private var totalPausedDurationMillis: Long = 0L
    private var pauseStartMillis: Long = 0L

    fun setSourceText(text: String, startFromBeginning: Boolean = true) {
        sourceFullText = text
        _totalCount.value = text.length
        if (startFromBeginning) {
            currentIndex = 0
            _typedCount.value = 0
            _progressFraction.value = 0f
            _elapsedSeconds.value = 0L
            _estimatedRemainingSeconds.value = 0L
        } else {
            updateProgress()
        }
    }

    fun start(config: AutoTypeConfig) {
        if (sourceFullText.isEmpty()) return
        if (currentIndex >= sourceFullText.length) {
            currentIndex = 0
            _typedCount.value = 0
        }

        typingJob?.cancel()
        typingJob = scope.launch(Dispatchers.Default) {
            // Handle countdown if configured and starting fresh
            if (config.countdownSeconds > 0 && currentIndex == 0) {
                _state.value = AutoTypeState.COUNTDOWN
                for (sec in config.countdownSeconds downTo 1) {
                    _countdownSec.value = sec
                    delay(1000)
                }
                _countdownSec.value = 0
            }

            _state.value = AutoTypeState.TYPING
            startTimeMillis = System.currentTimeMillis()
            totalPausedDurationMillis = 0L

            runTypingLoop(config)
        }
    }

    fun pause() {
        if (_state.value == AutoTypeState.TYPING) {
            _state.value = AutoTypeState.PAUSED
            pauseStartMillis = System.currentTimeMillis()
            typingJob?.cancel()
        }
    }

    fun resume(config: AutoTypeConfig) {
        if (_state.value == AutoTypeState.PAUSED) {
            if (pauseStartMillis > 0L) {
                totalPausedDurationMillis += (System.currentTimeMillis() - pauseStartMillis)
                pauseStartMillis = 0L
            }
            _state.value = AutoTypeState.TYPING
            typingJob = scope.launch(Dispatchers.Default) {
                runTypingLoop(config)
            }
        }
    }

    fun stop() {
        typingJob?.cancel()
        _state.value = AutoTypeState.IDLE
        _activeChar.value = null
        _countdownSec.value = 0
    }

    fun reset() {
        stop()
        currentIndex = 0
        _typedCount.value = 0
        _progressFraction.value = 0f
        _elapsedSeconds.value = 0L
        _estimatedRemainingSeconds.value = 0L
    }

    private suspend fun runTypingLoop(config: AutoTypeConfig) {
        val total = sourceFullText.length
        val baseDelayMs = (1000.0 / config.speedCharsPerSec.coerceAtLeast(1)).toLong().coerceAtLeast(2L)

        while (currentIndex < total && _state.value == AutoTypeState.TYPING) {
            val charOrChunk: String
            val keyCharForHighlight: Char?

            if (config.chunkMode && config.chunkSize > 1) {
                val end = (currentIndex + config.chunkSize).coerceAtMost(total)
                charOrChunk = sourceFullText.substring(currentIndex, end)
                keyCharForHighlight = charOrChunk.firstOrNull()
                currentIndex = end
            } else {
                val c = sourceFullText[currentIndex]
                charOrChunk = c.toString()
                keyCharForHighlight = c
                currentIndex++
            }

            _activeChar.value = keyCharForHighlight
            _typedCount.value = currentIndex
            updateProgress()

            // Play feedback
            feedback.playKeyFeedback(config.soundEnabled, config.hapticsEnabled)

            // Callback to update editor content
            onCharTyped(charOrChunk, keyCharForHighlight)

            // Human jitter calculation
            val jitterVariance = if (config.jitterPercent > 0) {
                val maxVariance = (baseDelayMs * (config.jitterPercent / 100.0)).toLong()
                if (maxVariance > 0) {
                    (random.nextLong() % (maxVariance * 2 + 1)) - maxVariance
                } else 0L
            } else 0L

            // Slower delay for punctuation / paragraph breaks to mimic realistic human cadence
            val punctuationBonus = if (charOrChunk.endsWith("\n") || charOrChunk.endsWith(".") || charOrChunk.endsWith(">")) {
                (baseDelayMs * 0.8).toLong()
            } else 0L

            val delayTime = (baseDelayMs + jitterVariance + punctuationBonus).coerceAtLeast(2L)
            delay(delayTime)
        }

        if (currentIndex >= total) {
            _state.value = AutoTypeState.COMPLETED
            _activeChar.value = null
        }
    }

    private fun updateProgress() {
        val total = sourceFullText.length
        if (total > 0) {
            val fraction = currentIndex.toFloat() / total
            _progressFraction.value = fraction

            val now = System.currentTimeMillis()
            val activeElapsed = if (startTimeMillis > 0) {
                ((now - startTimeMillis - totalPausedDurationMillis) / 1000).coerceAtLeast(0)
            } else 0L
            _elapsedSeconds.value = activeElapsed

            if (currentIndex > 0 && fraction < 1f) {
                val remainingChars = total - currentIndex
                val rate = currentIndex.toDouble() / activeElapsed.coerceAtLeast(1)
                val etaSec = (remainingChars / rate.coerceAtLeast(0.1)).toLong()
                _estimatedRemainingSeconds.value = etaSec
            } else {
                _estimatedRemainingSeconds.value = 0L
            }
        }
    }
}
