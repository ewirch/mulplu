package com.mulplu.app.engine

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectionTest {

    @Test
    fun `never the same item twice in a row unless it is the only one open`() {
        val s = allWrongCalibrated()
        val rng = Random(42)
        var last: ItemKey? = null
        repeat(500) {
            val q = Engine.nextQuestion(s, day(1), last, rng)!!
            assertNotEquals(last, q.item)
            last = q.item
        }
    }

    @Test
    fun `the only open item may repeat`() {
        var s = allWrongCalibrated()
        val rng = Random(1)
        // satisfy all pool items except the first
        for (item in Engine.pool(s).drop(1)) s = item.answer(s, correct = true, today = day(1))
        val only = Engine.pool(s).first()
        val q = Engine.nextQuestion(s, day(1), lastShown = only, random = rng)
        assertNotNull(q)
        assertEquals(only, q!!.item)
    }

    @Test
    fun `day goal reached returns null`() {
        var s = allWrongCalibrated()
        for (item in Engine.pool(s)) s = item.answer(s, correct = true, today = day(1))
        assertNull(Engine.nextQuestion(s, day(1), null, Random(1)))
        // next day everything is open again
        assertNotNull(Engine.nextQuestion(s, day(2), null, Random(1)))
    }

    @Test
    fun `presentation order is a per-question draw`() {
        val s = allWrongCalibrated()
        val rng = Random(7)
        val seen = mutableSetOf<Pair<Int, Int>>()
        repeat(200) {
            val q = Engine.nextQuestion(s, day(1), null, rng)!!
            if (q.item.a != q.item.b) seen.add(q.shownA to q.shownB)
            assertEquals(q.item, ItemKey.of(q.shownA, q.shownB))
        }
        // both orientations of at least one non-square item occurred
        assertTrue(seen.any { (a, b) -> (b to a) in seen })
    }

    @Test
    fun `option count follows the level`() {
        assertEquals(2, Engine.optionCount(1))
        assertEquals(3, Engine.optionCount(2))
        assertEquals(4, Engine.optionCount(3))
        assertNull(Engine.optionCount(4))
        assertNull(Engine.optionCount(5))
    }
}
