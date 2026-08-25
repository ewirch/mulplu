package com.mulplu.app.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalibrationTest {

    private fun probe(state: AppState, correct: Boolean): AppState {
        val item = Ranking.ORDER[state.calibrationIndex]
        return Engine.reduce(
            state,
            Event.CalibrationProbeAnswered(if (correct) item.product else null),
            day(1),
        )
    }

    @Test
    fun `correct probe seeds level 5, consolidated and satisfied today`() {
        val s = probe(AppState.initial(), correct = true)
        val it = s.items.getValue(Ranking.ORDER[0])
        assertEquals(5, it.level)
        assertTrue(it.hasEverConsolidated)
        assertNull(it.lastCountedOn)
        assertEquals(day(1), it.satisfiedOn)
        assertEquals(1, s.calibrationIndex)
        assertEquals(0, s.calibrationMissStreak)
    }

    @Test
    fun `wrong probe seeds level 1 and counts toward the mercy stop`() {
        val s = probe(AppState.initial(), correct = false)
        val it = s.items.getValue(Ranking.ORDER[0])
        assertEquals(1, it.level)
        assertFalse(it.hasEverConsolidated)
        assertNull(it.lastCountedOn)
        assertNull(it.satisfiedOn)
        assertEquals(1, s.calibrationMissStreak)
    }

    @Test
    fun `a wrong-probed item still levels up in day 1's round`() {
        val item = Ranking.ORDER[0]
        var s = probe(AppState.initial(), correct = false)
        s = Engine.reduce(s, Event.AnswerGiven(item, item.product), day(1))
        val it = s.items.getValue(item)
        assertEquals(2, it.level)
        assertEquals(day(1), it.lastPromotedOn)
    }

    @Test
    fun `in a round, wrong then correct on the same item does not level up`() {
        val item = Ranking.ORDER[0]
        var s = probe(AppState.initial(), correct = false)
        s = Engine.reduce(s, Event.AnswerGiven(item, null), day(1))
        s = Engine.reduce(s, Event.AnswerGiven(item, item.product), day(1))
        val it = s.items.getValue(item)
        assertEquals(1, it.level)
        assertNull(it.lastPromotedOn)
    }

    @Test
    fun `a correct probe resets the miss streak`() {
        var s = AppState.initial()
        repeat(5) { s = probe(s, correct = false) }
        assertEquals(5, s.calibrationMissStreak)
        s = probe(s, correct = true)
        assertEquals(0, s.calibrationMissStreak)
        assertFalse(s.calibrationComplete)
    }

    @Test
    fun `six consecutive misses end calibration and seed the rest at level 1`() {
        var s = AppState.initial()
        repeat(6) { s = probe(s, correct = false) }
        assertTrue(s.calibrationComplete)
        assertEquals(36, s.calibrationIndex)
        // unprobed items are level 1 and never counted
        for (item in Ranking.ORDER.drop(6)) {
            val it = s.items.getValue(item)
            assertEquals(1, it.level)
            assertNull(it.lastCountedOn)
        }
        // day 1 continues in normal play: pool is the front, all open
        assertEquals(10, Engine.openToday(s, day(1)).size)
    }

    @Test
    fun `fully fluent child ends calibration with the day goal reached and the terminal event`() {
        var s = AppState.initial()
        repeat(36) { s = probe(s, correct = true) }
        assertTrue(s.calibrationComplete)
        assertTrue(s.wasEverCompleted)
        assertEquals(36, Engine.pool(s).size)
        assertTrue(Engine.openToday(s, day(1)).isEmpty()) // day goal reached
    }

    @Test
    fun `calibration never pulls items into the pool out of order`() {
        var s = AppState.initial()
        // alternate: wrong, correct, wrong, correct ... never 6 in a row
        repeat(36) { i -> s = probe(s, correct = i % 2 == 1) }
        assertTrue(s.calibrationComplete)
        val consolidated = Ranking.ORDER.filterIndexed { i, _ -> i % 2 == 1 }.toSet()
        val expectedFront =
            Ranking.ORDER.filter { it !in consolidated }.take(Ranking.FRONT_WIDTH)
        assertEquals(expectedFront, Engine.learningFront(s))
        assertEquals(consolidated.size + 10, Engine.pool(s).size)
    }
}
