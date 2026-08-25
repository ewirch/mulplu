package com.mulplu.app.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class RevealTimelineTest {

    /**
     * #28: the correct button must be back at its normal size before the next
     * question is presented, i.e. the whole reveal fits into the feedback window.
     */
    @Test
    fun `reveal finishes before the next question is presented`() {
        assertTrue(
            "reveal takes ${RevealTimeline.TOTAL_MS} ms, window is ${AppViewModel.WRONG_FEEDBACK_MS} ms",
            RevealTimeline.TOTAL_MS <= AppViewModel.WRONG_FEEDBACK_MS,
        )
    }

    /** The wrong options are gone before the correct one starts growing. */
    @Test
    fun `fade out completes before the grow starts`() {
        assertTrue(RevealTimeline.FADE_MS <= RevealTimeline.GROW_DELAY_MS)
    }
}
