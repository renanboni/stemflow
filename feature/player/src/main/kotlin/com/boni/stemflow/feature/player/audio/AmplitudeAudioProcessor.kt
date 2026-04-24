package com.boni.stemflow.feature.player.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AmplitudeAudioProcessor : BaseAudioProcessor() {

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private var smoothed = 0f

    override fun onConfigure(
        inputAudioFormat: AudioProcessor.AudioFormat,
    ): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val size = inputBuffer.remaining()
        if (size == 0) return

        val shorts = inputBuffer.duplicate().order(ByteOrder.nativeOrder()).asShortBuffer()
        var maxAbs = 0
        while (shorts.hasRemaining()) {
            val sample = shorts.get().toInt()
            val abs = if (sample < 0) -sample else sample
            if (abs > maxAbs) maxAbs = abs
        }
        if (maxAbs > 0) {
            val peak = maxAbs.toFloat() / Short.MAX_VALUE
            val target = (peak * GAIN).coerceIn(0f, 1f)
            val factor = if (target > smoothed) ATTACK else RELEASE
            smoothed += (target - smoothed) * factor
            _amplitude.value = smoothed
        }

        val outputBuffer = replaceOutputBuffer(size)
        outputBuffer.put(inputBuffer)
        outputBuffer.flip()
    }

    override fun onReset() {
        smoothed = 0f
        _amplitude.value = 0f
    }

    private companion object {
        const val ATTACK = 0.65f
        const val RELEASE = 0.13f
        const val GAIN = 1.25f
    }
}
