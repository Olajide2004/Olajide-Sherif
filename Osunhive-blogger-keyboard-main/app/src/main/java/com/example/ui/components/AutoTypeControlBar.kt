package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AutoTypeConfig
import com.example.model.AutoTypeState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SignalGold

@Composable
fun AutoTypeControlBar(
    state: AutoTypeState,
    countdownSec: Int,
    typedCount: Int,
    totalCount: Int,
    progressFraction: Float,
    elapsedSeconds: Long,
    estimatedRemainingSeconds: Long,
    config: AutoTypeConfig,
    onConfigChange: (AutoTypeConfig) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSettings by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("autotype_control_bar"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Main Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Play / Pause / Resume Primary Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (state) {
                        AutoTypeState.IDLE, AutoTypeState.COMPLETED -> {
                            Button(
                                onClick = onStart,
                                enabled = totalCount > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SignalGold,
                                    contentColor = Color(0xFF1C2B2A)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("button_start_autotype")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (state == AutoTypeState.COMPLETED) "Re-Type" else "Auto-Type",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        AutoTypeState.COUNTDOWN -> {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.15f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(500, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )
                            Box(
                                modifier = Modifier
                                    .scale(scale)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SignalGold)
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Starting in ${countdownSec}s...",
                                    color = Color(0xFF1C2B2A),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        AutoTypeState.TYPING -> {
                            Button(
                                onClick = onPause,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SignalGold,
                                    contentColor = Color(0xFF1C2B2A)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("button_pause_autotype")
                            ) {
                                Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pause", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                        AutoTypeState.PAUSED -> {
                            Button(
                                onClick = onResume,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SignalGold,
                                    contentColor = Color(0xFF1C2B2A)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("button_resume_autotype")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Resume", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (state == AutoTypeState.TYPING || state == AutoTypeState.PAUSED) {
                        OutlinedButton(
                            onClick = onStop,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("button_stop_autotype")
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop", color = AlertRed, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    } else if (typedCount > 0) {
                        IconButton(
                            onClick = onReset,
                            modifier = Modifier.size(36.dp).testTag("button_reset_autotype")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Status indicator and settings button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (state) {
                                    AutoTypeState.TYPING -> SignalGold
                                    AutoTypeState.PAUSED -> Color(0xFFFFA000)
                                    AutoTypeState.COUNTDOWN -> SignalGold
                                    AutoTypeState.COMPLETED -> Color(0xFF34A853)
                                    AutoTypeState.IDLE -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = state.name,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showSettings = !showSettings },
                        modifier = Modifier.size(32.dp).testTag("button_typer_settings")
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Typing settings",
                            tint = if (showSettings) SignalGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Progress Bar & Telemetry
            if (totalCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progressFraction.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = SignalGold,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val percent = (progressFraction * 100).toInt()
                    Text(
                        text = "$typedCount / $totalCount chars ($percent%)",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Time: ${formatSeconds(elapsedSeconds)}  •  ETA: ${formatSeconds(estimatedRemainingSeconds)}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Collapsible settings drawer
            AnimatedVisibility(visible = showSettings) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Typewriter Parameters",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Speed Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Speed: ${config.speedCharsPerSec} chars/sec",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = config.speedCharsPerSec.toFloat(),
                        onValueChange = { onConfigChange(config.copy(speedCharsPerSec = it.toInt())) },
                        valueRange = 2f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = SignalGold,
                            activeTrackColor = SignalGold
                        )
                    )

                    // Human Jitter Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Human Jitter: ${config.jitterPercent}%",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = config.jitterPercent.toFloat(),
                        onValueChange = { onConfigChange(config.copy(jitterPercent = it.toInt())) },
                        valueRange = 0f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = SignalGold,
                            activeTrackColor = SignalGold
                        )
                    )

                    // Countdown delay
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Start Countdown: ${config.countdownSeconds}s",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = config.countdownSeconds.toFloat(),
                        onValueChange = { onConfigChange(config.copy(countdownSeconds = it.toInt())) },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = SignalGold,
                            activeTrackColor = SignalGold
                        )
                    )

                    // Toggles: Sound & Haptics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Typewriter Clicks", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Switch(
                            checked = config.soundEnabled,
                            onCheckedChange = { onConfigChange(config.copy(soundEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = SignalGold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Haptic Feedback", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Switch(
                            checked = config.hapticsEnabled,
                            onCheckedChange = { onConfigChange(config.copy(hapticsEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = SignalGold)
                        )
                    }
                }
            }
        }
    }
}

private fun formatSeconds(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
