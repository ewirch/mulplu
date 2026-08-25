package com.mulplu.app.ui

/**
 * Timeline of the answer reveal after a wrong answer / "Weiß nicht" (mvp-spec §9):
 * the wrong options fade out, the correct button grows, holds, and shrinks back —
 * all of it inside [AppViewModel.WRONG_FEEDBACK_MS], so the next question never
 * appears while a button is still magnified (#28).
 */
internal object RevealTimeline {
    const val FADE_MS = 450
    const val GROW_DELAY_MS = 450L
    const val GROW_MS = 450
    const val HOLD_MS = 1100L
    const val SHRINK_MS = 450
    const val GROW_SCALE = 1.45f

    /** Free input: how long the red flash runs before the answer is revealed. */
    const val RED_FLASH_MS = 700L

    /** When the last animation ends, ms after the feedback phase started. */
    const val TOTAL_MS = GROW_DELAY_MS + GROW_MS + HOLD_MS + SHRINK_MS
}
