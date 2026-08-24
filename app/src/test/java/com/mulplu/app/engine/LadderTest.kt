package com.mulplu.app.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LadderTest {

    private val item = Ranking.ORDER[0] // 2x2

    @Test
    fun `correct counting answer moves level up`() {
        val s = item.answer(allWrongCalibrated(), correct = true, today = day(1))
        assertEquals(2, s.items.getValue(item).level)
        assertEquals(day(1), s.items.getValue(item).lastCountedOn)
        assertEquals(day(1), s.items.getValue(item).satisfiedOn)
    }

    @Test
    fun `wrong counting answer moves level down with floor 1`() {
        var s = allWrongCalibrated()
        s = item.answer(s, correct = true, today = day(1))
        s = item.answer(s, correct = false, today = day(2))
        assertEquals(1, s.items.getValue(item).level)
        s = item.answer(s, correct = false, today = day(3))
        assertEquals(1, s.items.getValue(item).level)
    }

    @Test
    fun `weiss nicht is a miss`() {
        var s = allWrongCalibrated()
        s = item.answer(s, correct = true, today = day(1))
        s = Engine.reduce(s, Event.AnswerGiven(item, given = null), day(2))
        assertEquals(1, s.items.getValue(item).level)
    }

    @Test
    fun `practice answers move nothing`() {
        var s = allWrongCalibrated()
        s = item.answer(s, correct = true, today = day(1))
        repeat(5) { s = item.answer(s, correct = true, today = day(1)) }
        assertEquals(2, s.items.getValue(item).level)
        // and a wrong practice answer moves nothing either
        s = item.answer(s, correct = false, today = day(1))
        assertEquals(2, s.items.getValue(item).level)
    }

    @Test
    fun `wrong practice answer does not retire the item`() {
        var s = allWrongCalibrated()
        s = item.answer(s, correct = false, today = day(1)) // counting, wrong
        assertNull(s.items.getValue(item).satisfiedOn)
        assertTrue(item in Engine.openToday(s, day(1)))
        // a later correct practice answer satisfies it
        s = item.answer(s, correct = true, today = day(1))
        assertEquals(day(1), s.items.getValue(item).satisfiedOn)
        assertFalse(item in Engine.openToday(s, day(1)))
        assertEquals(1, s.items.getValue(item).level) // still no movement
    }

    @Test
    fun `level caps at 5 and consolidation takes a minimum of 4 days`() {
        var s = allWrongCalibrated()
        for (d in 1..4) {
            assertFalse(s.items.getValue(item).hasEverConsolidated)
            s = item.answer(s, correct = true, today = day(d))
        }
        assertEquals(5, s.items.getValue(item).level)
        assertTrue(s.items.getValue(item).hasEverConsolidated)
        s = item.answer(s, correct = true, today = day(5))
        assertEquals(5, s.items.getValue(item).level)
    }

    @Test
    fun `promotion stamps lastPromotedOn but demotion does not`() {
        var s = allWrongCalibrated()
        s = item.answer(s, correct = true, today = day(1))
        assertEquals(day(1), s.items.getValue(item).lastPromotedOn)
        s = item.answer(s, correct = false, today = day(2))
        assertEquals(day(1), s.items.getValue(item).lastPromotedOn)
    }

    @Test
    fun `revocation drops to 4 but hasEverConsolidated is monotone`() {
        var s = allWrongCalibrated()
        for (d in 1..4) s = item.answer(s, correct = true, today = day(d))
        s = item.answer(s, correct = false, today = day(5))
        val it = s.items.getValue(item)
        assertEquals(4, it.level)
        assertTrue(it.hasEverConsolidated)
    }

    @Test
    fun `re-consolidation after revocation does not admit again`() {
        var s = allWrongCalibrated()
        for (d in 1..4) s = item.answer(s, correct = true, today = day(d))
        val poolAfterFirst = Engine.pool(s).size
        s = item.answer(s, correct = false, today = day(5)) // revoke
        s = item.answer(s, correct = true, today = day(6)) // back to 5
        assertEquals(5, s.items.getValue(item).level)
        assertEquals(poolAfterFirst, Engine.pool(s).size)
    }
}
