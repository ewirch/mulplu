package com.mulplu.app.engine

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DistractorTest {

    @Test
    fun `choices contain the correct answer, are distinct and sized right`() {
        val rng = Random(3)
        for (item in Ranking.ORDER) {
            for (n in 2..4) {
                repeat(20) {
                    val choices = Engine.buildChoices(item.a, item.b, n, emptySet(), rng)
                    assertEquals(n, choices.size)
                    assertEquals(n, choices.distinct().size)
                    assertTrue(item.product in choices)
                }
            }
        }
    }

    @Test
    fun `rejected values of the day are never offered again`() {
        val rng = Random(9)
        val rejected = setOf(6, 8, 12)
        repeat(100) {
            val choices = Engine.buildChoices(2, 5, 4, rejected, rng)
            assertTrue(choices.none { it in rejected })
            assertTrue(10 in choices)
        }
    }

    @Test
    fun `falls back to table products when neighbours run out`() {
        val rng = Random(5)
        // 2x2: neighbours are {6, 8} only; reject both to force the fallback
        repeat(100) {
            val choices = Engine.buildChoices(2, 2, 4, setOf(6, 8), rng)
            assertEquals(4, choices.size)
            assertEquals(4, choices.distinct().size)
            assertTrue(4 in choices)
            assertTrue(choices.none { it == 6 || it == 8 })
        }
    }

    @Test
    fun `every distractor is a product of the trained table or a neighbour product`() {
        val rng = Random(11)
        val plausible = buildSet {
            for (a in 2..9) for (b in 2..9) add(a * b)
        }
        for (item in Ranking.ORDER) {
            repeat(10) {
                val choices = Engine.buildChoices(item.a, item.b, 4, emptySet(), rng)
                assertTrue(
                    "implausible options for $item: $choices",
                    choices.all { it in plausible },
                )
            }
        }
    }

    @Test
    fun `choices are re-drawn per presentation`() {
        val rng = Random(13)
        val draws = (1..50).map { Engine.buildChoices(6, 7, 4, emptySet(), rng).toSet() }
        assertTrue(draws.distinct().size > 1)
    }

    @Test
    fun `near distractors are favoured`() {
        val rng = Random(17)
        var near = 0
        var far = 0
        repeat(2000) {
            val choices = Engine.buildChoices(6, 7, 2, emptySet(), rng)
            val d = choices.first { it != 42 }
            if (kotlin.math.abs(d - 42) <= 7) near++ else far++
        }
        assertTrue("near=$near far=$far", near > far)
    }
}
