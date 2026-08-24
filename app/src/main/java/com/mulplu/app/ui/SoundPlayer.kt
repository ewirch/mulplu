package com.mulplu.app.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.mulplu.app.R

/**
 * Plays the self-made answer sounds (mvp-spec §9: rising chime for correct,
 * low soft tone for wrong; always on, no haptics). Assets in `res/raw` are
 * synthesized sine tones — no third-party material.
 */
class SoundPlayer(context: Context) {

    private val pool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val correctId = pool.load(context, R.raw.chime_correct, 1)
    private val wrongId = pool.load(context, R.raw.tone_wrong, 1)
    private val dayCloseId = pool.load(context, R.raw.day_close, 1)

    fun play(sound: Sound) {
        val id = when (sound) {
            Sound.Correct -> correctId
            Sound.Wrong -> wrongId
        }
        pool.play(id, 1f, 1f, 1, 0, 1f)
    }

    /** The day-close sound (mvp-spec §10) — wired up by the map ticket (#21). */
    fun playDayClose() {
        pool.play(dayCloseId, 1f, 1f, 1, 0, 1f)
    }

    fun release() = pool.release()
}
