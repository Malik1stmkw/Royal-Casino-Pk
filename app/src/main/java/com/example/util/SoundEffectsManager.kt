package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

object SoundEffectsManager {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val toneGen: ToneGenerator? by lazy {
        try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            null
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun setMuted(muted: Boolean) {
        _isMuted.value = muted
    }

    fun playButtonClick() {
        if (_isMuted.value) return
        scope.launch {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
            } catch (e: Exception) {
                playPcmTone(880.0, 30)
            }
        }
    }

    fun playChipSound() {
        if (_isMuted.value) return
        scope.launch {
            playPcmTone(1200.0, 35)
        }
    }

    fun playSpinSound() {
        if (_isMuted.value) return
        scope.launch {
            playPcmTone(550.0, 25)
        }
    }

    fun playWinSound() {
        if (_isMuted.value) return
        scope.launch {
            // Ascending major arpeggio C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                playPcmTone(freq, 70)
                Thread.sleep(60)
            }
        }
    }

    fun playJackpotSound() {
        if (_isMuted.value) return
        scope.launch {
            // Grand fanfare sequence
            val melody = listOf(
                523.25 to 70L,
                659.25 to 70L,
                783.99 to 70L,
                1046.50 to 100L,
                1318.51 to 100L,
                1567.98 to 140L,
                2093.00 to 250L
            )
            for ((freq, duration) in melody) {
                playPcmTone(freq, duration.toInt())
                Thread.sleep(duration - 10)
            }
        }
    }

    fun playLossSound() {
        if (_isMuted.value) return
        scope.launch {
            playPcmTone(392.0, 90)
            Thread.sleep(80)
            playPcmTone(293.66, 140)
        }
    }

    private fun playPcmTone(freqHz: Double, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        if (numSamples <= 0) return

        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i * freqHz / sampleRate
            // Natural decay envelope
            val envelope = exp(-3.5 * i / numSamples)
            buffer[i] = (sin(angle) * 16384 * envelope).toInt().toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
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
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 15)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, durationMs)
            } catch (_: Exception) {}
        }
    }
}
