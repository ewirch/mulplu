package com.mulplu.app.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PoolTest {

    @Test
    fun `cold start pool is the first 10 items of the admission order`() {
        val s = allWrongCalibrated()
        assertEquals(Ranking.ORDER.take(10), Engine.pool(s))
    }

    @Test
    fun `first-ever consolidation admits the next item due tomorrow`() {
        var s = allWrongCalibrated()
        val item = Ranking.ORDER[0]
        for (d in 1..4) s = item.answer(s, correct = true, today = day(d))

        val admitted = Ranking.ORDER[10] // the 11th item slides in
        assertTrue(admitted in Engine.pool(s))
        assertEquals(day(4), s.items.getValue(admitted).satisfiedOn)
        assertFalse(admitted in Engine.openToday(s, day(4))) // due tomorrow
        assertTrue(admitted in Engine.openToday(s, day(5)))
        assertEquals(11, Engine.pool(s).size) // 1 consolidated + 10 front
    }

    @Test
    fun `revoked item stays in the pool and occupies no window slot`() {
        var s = allWrongCalibrated()
        val item = Ranking.ORDER[0]
        for (d in 1..4) s = item.answer(s, correct = true, today = day(d))
        s = item.answer(s, correct = false, today = day(5)) // revoke: level 4

        assertTrue(item in Engine.pool(s))
        assertFalse(item in Engine.learningFront(s))
        assertEquals(10, Engine.learningFront(s).size)
        assertEquals(11, Engine.pool(s).size)
    }

    @Test
    fun `window shrinks when fewer than 10 never-consolidated items remain`() {
        var s = allWrongCalibrated()
        // consolidate the first 30 items directly
        s = s.copy(
            items = s.items.mapValues { (k, v) ->
                if (Ranking.ORDER.indexOf(k) < 30) {
                    v.copy(level = 5, hasEverConsolidated = true)
                } else {
                    v
                }
            },
        )
        assertEquals(6, Engine.learningFront(s).size)
        assertEquals(36, Engine.pool(s).size)
    }

    @Test
    fun `pool caps at 36 and completion sets the permanent mark once`() {
        var s = allWrongCalibrated()
        s = s.copy(
            items = s.items.mapValues { (k, v) ->
                if (k != Ranking.ORDER.last()) v.copy(level = 5, hasEverConsolidated = true) else v
            },
        )
        assertFalse(s.wasEverCompleted)
        val last = Ranking.ORDER.last()
        s = s.copy(items = s.items + (last to s.items.getValue(last).copy(level = 4)))
        s = last.answer(s, correct = true, today = day(1))
        assertTrue(s.wasEverCompleted)
        assertEquals(36, Engine.pool(s).size)

        // monotone: a revocation later does not clear it
        s = last.answer(s, correct = false, today = day(2))
        assertTrue(s.wasEverCompleted)
    }
}
