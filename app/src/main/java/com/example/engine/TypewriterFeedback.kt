package com.example.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import java.util.Random
import kotlin.math.sin

class TypewriterFeedback(private val context: Context) {

    private val random = Random()
    private var audioTrack: AudioTrack? = null
    private var clickSamples: ShortArray? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        initAudio()
    }

    private fun initAudio() {
        try {
            val sampleRate = 22050
            val durationMs = 12 // Crisp mechanical typewriter click
            val numSamples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val envelope = (1.0 - progress) * (1.0 - progress) // exponential decay
                // Mix high noise with a slight mechanical resonance at 1800Hz
                val noise = (random.nextDouble() * 2.0 - 1.0) * 0.6
                val resonance = sin(2.0 * Math.PI * 1800.0 * i / sampleRate) * 0.4
                val sampleValue = (noise + resonance) * envelope * Short.MAX_VALUE
                buffer[i] = sampleValue.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            clickSamples = buffer

            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(numSamples * 2)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
        } catch (_: Exception) {
            // AudioTrack failure fallback gracefully
        }
    }

    fun playKeyFeedback(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (soundEnabled) {
            playClickSound()
        }
        if (hapticsEnabled) {
            playHaptic()
        }
    }

    private fun playClickSound() {
        try {
            val samples = clickSamples ?: return
            audioTrack?.let { track ->
                if (track.playState != AudioTrack.PLAYSTATE_PLAYING) {
                    track.play()
                }
                track.write(samples, 0, samples.size, AudioTrack.WRITE_NON_BLOCKING)
            }
        } catch (_: Exception) {
            // gracefully ignore sound failure
        }
    }

    private fun playHaptic() {
        try {
            vibrator?.let { vib ->
                if (vib.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    } else {
                        @Suppress("DEPRECATION")
                        vib.vibrate(10)
                    }
                }
            }
        } catch (_: Exception) {
            // gracefully ignore haptic failure
        }
    }

    fun release() {
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (_: Exception) {
        }
    }
}
