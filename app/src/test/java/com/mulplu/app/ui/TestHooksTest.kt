package com.mulplu.app.ui

import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.Engine
import com.mulplu.app.engine.ItemState
import com.mulplu.app.engine.Ranking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The manual-testing shortcuts (#30) land on states the app can actually be in. */
class TestHooksTest {

    private val calibrated = AppState(
        items = Ranking.ORDER.associateWith { ItemState() },
        calibrationIndex = Ranking.ORDER.size,
    )

    @Test
    fun `consolidateAll - every item at level 5, completion marked`() {
        val after = TestHooks.consolidateAll(calibrated)
        assertTrue(after.items.values.all { it.level == 5 && it.hasEverConsolidated })
        assertTrue(after.wasEverCompleted)
        assertEquals(Ranking.ORDER.size, Engine.pool(after).size)
    }

    @Test
    fun `resetLevels - back to the floor and the first ten`() {
        val after = TestHooks.resetLevels(TestHooks.consolidateAll(calibrated))
        assertTrue(after.items.values.all { it.level == 1 && !it.hasEverConsolidated })
        assertFalse(after.wasEverCompleted)
        assertEquals(Ranking.ORDER.take(Ranking.FRONT_WIDTH), Engine.pool(after))
        // The calibration is not undone by this one.
        assertTrue(after.calibrationComplete)
    }

    @Test
    fun `setLevel - moves one item, level 5 consolidates it`() {
        val item = Ranking.ORDER[0]
        val toThree = TestHooks.setLevel(calibrated, item, 3)
        assertEquals(3, toThree.items.getValue(item).level)
        assertFalse(toThree.items.getValue(item).hasEverConsolidated)

        val toFive = TestHooks.setLevel(toThree, item, 5)
        assertTrue(toFive.items.getValue(item).hasEverConsolidated)
        // Everything else is untouched.
        assertTrue(Ranking.ORDER.drop(1).all { toFive.items.getValue(it).level == 1 })
    }
}
