package com.example.ime

import android.content.ClipboardManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.engine.TypewriterFeedback
import com.example.model.KeyboardLayout
import com.example.ui.components.TypewriterKeyboard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SignalGold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * System Input Method Service (IME) allowing the typewriter auto-typing keyboard
 * to be used as the phone's default system keyboard across all applications
 * (Chrome, draft.blogger.com, Notes, Social Media, etc.).
 */
class BloggerInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var feedback: TypewriterFeedback

    // IME State
    private val _currentLayout = MutableStateFlow(KeyboardLayout.QWERTY)
    val currentLayout = _currentLayout.asStateFlow()

    private val _activeChar = MutableStateFlow<Char?>(null)
    val activeChar = _activeChar.asStateFlow()

    // Auto-typing engine state for phone IME
    private val _isAutoTyping = MutableStateFlow(false)
    val isAutoTyping = _isAutoTyping.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _autoTypeProgress = MutableStateFlow(0f)
    val autoTypeProgress = _autoTypeProgress.asStateFlow()

    private val _autoTypeStatusText = MutableStateFlow("")
    val autoTypeStatusText = _autoTypeStatusText.asStateFlow()

    private var autoTypeJob: Job? = null
    private var pendingAutoTypeText: String = ""
    private var autoTypeIndex: Int = 0

    // Settings
    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled = _soundEnabled.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled = _hapticsEnabled.asStateFlow()

    private val _charsPerSecond = MutableStateFlow(15) // Speed
    val charsPerSecond = _charsPerSecond.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        feedback = TypewriterFeedback(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        feedback.release()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onShowInputRequested(flags: Int, configChange: Boolean): Boolean {
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        window?.window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(this)
            decor.setViewTreeViewModelStoreOwner(this)
            decor.setViewTreeSavedStateRegistryOwner(this)
        }
        if (lifecycleRegistry.currentState != Lifecycle.State.RESUMED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        showWindow(true)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        stopAutoType()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(this)
            decor.setViewTreeViewModelStoreOwner(this)
            decor.setViewTreeSavedStateRegistryOwner(this)
        }

        if (lifecycleRegistry.currentState != Lifecycle.State.RESUMED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        val composeView = ComposeView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(this@BloggerInputMethodService)
            setViewTreeViewModelStoreOwner(this@BloggerInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@BloggerInputMethodService)

            setContent {
                MyApplicationTheme {
                    ImeKeyboardScreen()
                }
            }
        }
        return composeView
    }

    @Composable
    private fun ImeKeyboardScreen() {
        val layout by currentLayout.collectAsState()
        val char by activeChar.collectAsState()
        val autoTyping by isAutoTyping.collectAsState()
        val paused by isPaused.collectAsState()
        val progress by autoTypeProgress.collectAsState()
        val statusText by autoTypeStatusText.collectAsState()
        val sound by soundEnabled.collectAsState()
        val haptics by hapticsEnabled.collectAsState()
        val speed by charsPerSecond.collectAsState()

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                // IME Toolbar (Auto-type clipboard, globe switcher, sound/speed toggles)
                ImeHeaderToolbar(
                    autoTyping = autoTyping,
                    paused = paused,
                    progress = progress,
                    statusText = statusText,
                    soundEnabled = sound,
                    hapticsEnabled = haptics,
                    speed = speed,
                    onSwitchIme = { switchKeyboard() },
                    onAutoTypeClipboard = { autoTypeFromClipboard() },
                    onPauseResumeAutoType = {
                        if (paused) resumeAutoType() else pauseAutoType()
                    },
                    onStopAutoType = { stopAutoType() },
                    onToggleSound = { _soundEnabled.value = !_soundEnabled.value },
                    onToggleHaptics = { _hapticsEnabled.value = !_hapticsEnabled.value },
                    onChangeSpeed = { cycleSpeed() }
                )

                // The Typewriter Keyboard layout
                TypewriterKeyboard(
                    activeChar = char,
                    currentLayout = layout,
                    onLayoutChange = { _currentLayout.value = it },
                    onKeyPress = { handleKeyPress(it) },
                    onBackspace = { handleBackspace() },
                    onEnter = { handleEnter() },
                    onSpace = { handleSpace() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    private fun ImeHeaderToolbar(
        autoTyping: Boolean,
        paused: Boolean,
        progress: Float,
        statusText: String,
        soundEnabled: Boolean,
        hapticsEnabled: Boolean,
        speed: Int,
        onSwitchIme: () -> Unit,
        onAutoTypeClipboard: () -> Unit,
        onPauseResumeAutoType: () -> Unit,
        onStopAutoType: () -> Unit,
        onToggleSound: () -> Unit,
        onToggleHaptics: () -> Unit,
        onChangeSpeed: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            if (autoTyping) {
                // Active auto-typing status & controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SignalGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = SignalGold,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onPauseResumeAutoType,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (paused) "Resume" else "Pause",
                            tint = SignalGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onStopAutoType,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                // Standard IME Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Switch IME button (Globe)
                    IconButton(
                        onClick = onSwitchIme,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Input Method",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Auto-Type Clipboard Chip
                    AssistChip(
                        onClick = onAutoTypeClipboard,
                        label = { Text("Auto-Type Paste", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ContentPaste,
                                contentDescription = null,
                                tint = SignalGold,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = SignalGold
                        )
                    )

                    // Blogger Snippet Fast Access
                    AssistChip(
                        onClick = { handleKeyPress("<h2></h2>") },
                        label = { Text("<h2>", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                    AssistChip(
                        onClick = { handleKeyPress("<p></p>") },
                        label = { Text("<p>", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                    AssistChip(
                        onClick = { handleKeyPress("<b></b>") },
                        label = { Text("<b>", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
                    )

                    // Speed Chip
                    AssistChip(
                        onClick = onChangeSpeed,
                        label = { Text("${speed}cps", fontSize = 10.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
                    )

                    // Sound Toggle
                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Sound",
                            tint = if (soundEnabled) SignalGold else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Haptics Toggle
                    IconButton(
                        onClick = onToggleHaptics,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Toggle Haptics",
                            tint = if (hapticsEnabled) SignalGold else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    private fun handleKeyPress(text: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)

        val firstChar = text.firstOrNull() ?: ' '
        _activeChar.value = firstChar

        feedback.playKeyFeedback(_soundEnabled.value, _hapticsEnabled.value)

        serviceScope.launch {
            delay(120)
            if (_activeChar.value == firstChar) {
                _activeChar.value = null
            }
        }
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return
        // Delete 1 character before cursor
        val selectedText = ic.getSelectedText(0)
        if (selectedText.isNullOrEmpty()) {
            ic.deleteSurroundingText(1, 0)
        } else {
            ic.commitText("", 1)
        }

        feedback.playKeyFeedback(_soundEnabled.value, _hapticsEnabled.value)
    }

    private fun handleEnter() {
        val ic = currentInputConnection ?: return
        val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
        if (action != null && action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(action)
        } else {
            ic.commitText("\n", 1)
        }
        feedback.playKeyFeedback(_soundEnabled.value, _hapticsEnabled.value)
    }

    private fun handleSpace() {
        val ic = currentInputConnection ?: return
        ic.commitText(" ", 1)
        _activeChar.value = ' '

        feedback.playKeyFeedback(_soundEnabled.value, _hapticsEnabled.value)

        serviceScope.launch {
            delay(100)
            if (_activeChar.value == ' ') {
                _activeChar.value = null
            }
        }
    }

    private fun switchKeyboard() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                switchToPreviousInputMethod()
            } else {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
                imm?.showInputMethodPicker()
            }
        } catch (_: Exception) {
            KeyboardSetupManager.showInputMethodPicker(this)
        }
    }

    private fun autoTypeFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipData = clipboard?.primaryClip
        val text = clipData?.getItemAt(0)?.text?.toString()

        if (text.isNullOrEmpty()) {
            Toast.makeText(this, "Clipboard is empty! Copy text first.", Toast.LENGTH_SHORT).show()
            return
        }

        startAutoType(text)
    }

    private fun startAutoType(text: String) {
        stopAutoType()
        pendingAutoTypeText = text
        autoTypeIndex = 0
        _isAutoTyping.value = true
        _isPaused.value = false

        autoTypeJob = serviceScope.launch {
            val total = pendingAutoTypeText.length
            val delayMs = (1000L / _charsPerSecond.value.coerceAtLeast(1)).coerceAtLeast(10L)

            while (autoTypeIndex < total && _isAutoTyping.value) {
                if (_isPaused.value) {
                    delay(100)
                    continue
                }

                val ch = pendingAutoTypeText[autoTypeIndex]
                val ic = currentInputConnection
                if (ic != null) {
                    ic.commitText(ch.toString(), 1)
                    _activeChar.value = ch

                    feedback.playKeyFeedback(_soundEnabled.value, _hapticsEnabled.value)
                }

                autoTypeIndex++
                _autoTypeProgress.value = autoTypeIndex.toFloat() / total
                _autoTypeStatusText.value = "Typing $autoTypeIndex / $total (${(autoTypeProgress.value * 100).toInt()}%)"

                delay(delayMs)
            }

            _activeChar.value = null
            _isAutoTyping.value = false
            _autoTypeStatusText.value = "Auto-typing complete ($total chars)"
        }
    }

    private fun pauseAutoType() {
        _isPaused.value = true
        _autoTypeStatusText.value = "Paused at $autoTypeIndex / ${pendingAutoTypeText.length}"
    }

    private fun resumeAutoType() {
        _isPaused.value = false
        _autoTypeStatusText.value = "Resuming auto-type..."
    }

    private fun stopAutoType() {
        autoTypeJob?.cancel()
        autoTypeJob = null
        _isAutoTyping.value = false
        _isPaused.value = false
        _activeChar.value = null
    }

    private fun cycleSpeed() {
        val speeds = listOf(10, 20, 40, 80)
        val currentIndex = speeds.indexOf(_charsPerSecond.value)
        val nextIndex = (currentIndex + 1) % speeds.size
        _charsPerSecond.value = speeds[nextIndex]
    }
}
