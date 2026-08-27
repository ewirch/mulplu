package com.mulplu.app.engine

import java.time.LocalDate
import java.util.Random as JavaRandom
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Re-runs the scenarios of the reference simulation (branch `prototype/engine-sim`,
 * `prototype/engine_sim.py`) against the real engine and asserts the invariants
 * validated there (mvp-spec §13): the day goal is always reachable (no oscillation
 * stall) and pure guessing never produces mastery.
 */
class SimulationOracleTest {

    private data class Profile(
        val name: String,
        val base: Double,
        val slope: Double,
        val learn: Double,
        val noise: Double,
        val forget: Double,
        val attendance: Double,
    )

    private val profiles = listOf(
        Profile("ideal", 0.98, 0.00, 0.30, 0.02, 0.000, 1.00),
        Profile("strong", 0.95, 0.35, 0.22, 0.05, 0.005, 0.95),
        Profile("average", 0.90, 0.70, 0.15, 0.10, 0.010, 0.85),
        Profile("weak", 0.70, 0.95, 0.08, 0.12, 0.020, 0.80),
        Profile("inconsistent", 0.85, 0.60, 0.10, 0.30, 0.030, 0.60),
        Profile("weak+sparse", 0.65, 0.95, 0.07, 0.15, 0.030, 0.50),
    )

    /** Per-item knowledge in [0,1] = probability of a correct free-input answer. */
    private class Learner(val p: Profile, val rng: Random, val gauss: JavaRandom) {
        val k = DoubleArray(36) { i ->
            val item = Ranking.ORDER[i]
            (p.base - p.slope * hardness(item)).coerceIn(0.02, 0.99)
        }

        private fun hardness(item: ItemKey): Double {
            var prod = (item.a * item.b).toDouble()
            if (item.a == item.b || item.a == 9 || item.b == 9) prod *= 0.85
            return ((prod - 4.0) / 52.0).coerceIn(0.0, 1.0)
        }

        /** options == null -> free input. Returns (correct, knewIt). */
        fun answer(idx: Int, options: Int?): Pair<Boolean, Boolean> {
            val eff = (k[idx] + gauss.nextGaussian() * p.noise).coerceIn(0.0, 1.0)
            if (rng.nextDouble() < eff) return true to true
            if (options == null) return false to false
            return (rng.nextDouble() < 1.0 / options) to false
        }

        fun study(idx: Int, knew: Boolean, sawAnswer: Boolean) {
            val g = if (knew) p.learn else if (sawAnswer) p.learn * 0.55 else 0.0
            k[idx] += g * (1.0 - k[idx])
        }

        fun decay(days: Int) {
            if (p.forget <= 0) return
            var f = 1.0
            repeat(days) { f *= 1.0 - p.forget }
            for (i in k.indices) k[i] *= f
        }
    }

    private fun idx(item: ItemKey) = Ranking.ORDER.indexOf(item)

    private fun runLifetime(profile: Profile, seed: Int, days: Int = 90): AppState {
        val rng = Random(seed)
        val gauss = JavaRandom(seed.toLong())
        val learner = Learner(profile, rng, gauss)
        var state = AppState.initial()
        var lastPlayed = 1
        val start = LocalDate.of(2026, 1, 1)

        for (d in 1..days) {
            val today = start.plusDays((d - 1).toLong())
            if (d > 1) {
                if (rng.nextDouble() > profile.attendance) continue
                learner.decay(d - lastPlayed)
            }
            lastPlayed = d

            if (d == 1) {
                // calibration: all 36 probed by free input in admission order
                while (!state.calibrationComplete) {
                    val i = idx(Ranking.ORDER[state.calibrationIndex])
                    val (correct, knew) = learner.answer(i, null)
                    val item = Ranking.ORDER[state.calibrationIndex]
                    state = Engine.reduce(
                        state,
                        Event.CalibrationProbeAnswered(if (correct) item.product else null),
                        today,
                    )
                    learner.study(i, knew, sawAnswer = false)
                }
            }

            // play until the day goal
            var lastShown: ItemKey? = null
            var guard = 0
            while (true) {
                val q = Engine.nextQuestion(state, today, lastShown, rng) ?: break
                guard++
                assertTrue(
                    "day goal not reachable: ${profile.name} seed=$seed day=$d",
                    guard <= 4000,
                )
                lastShown = q.item
                val i = idx(q.item)
                val (correct, knew) = learner.answer(i, q.optionCount)
                state = Engine.reduce(
                    state,
                    Event.AnswerGiven(q.item, if (correct) q.item.product else -1),
                    today,
                )
                learner.study(i, knew, sawAnswer = !correct)
            }
            // the day goal was reached: every pool item satisfied today
            assertTrue(Engine.openToday(state, today).isEmpty())
        }
        return state
    }

    @Test
    fun `day goal is always reached for every profile - no oscillation stall`() {
        for (profile in profiles) {
            for (seed in 0 until 10) {
                runLifetime(profile, seed)
            }
        }
    }

    @Test
    fun `ideal learner completes all 36 items within 90 days`() {
        val state = runLifetime(profiles.first { it.name == "ideal" }, seed = 1)
        assertTrue(state.wasEverCompleted)
        assertEquals(36, state.items.values.count { it.hasEverConsolidated })
    }

    /** A learner that knows nothing: mercy-stopped calibration, then blind guessing. */
    private fun runPureGuesser(seed: Int, days: Int): AppState {
        val rng = Random(seed)
        var state = AppState.initial()
        val start = LocalDate.of(2026, 1, 1)
        // calibration: knows nothing, always "Weiß nicht" -> mercy stop
        repeat(Engine.MERCY_STOP) {
            state = Engine.reduce(state, Event.CalibrationProbeAnswered(null), start)
        }
        assertTrue(state.calibrationComplete)

        for (d in 1..days) {
            val today = start.plusDays((d - 1).toLong())
            var lastShown: ItemKey? = null
            var asked = 0
            while (asked < 200) { // a guesser never satisfies free-input items; cap the day
                val q = Engine.nextQuestion(state, today, lastShown, rng) ?: break
                asked++
                lastShown = q.item
                val given = if (q.optionCount != null) {
                    Engine.buildChoices(q.item.a, q.item.b, q.optionCount, emptySet(), rng)
                        .random(rng)
                } else {
                    rng.nextInt(4, 82) // blind free-input guess
                }
                state = Engine.reduce(state, Event.AnswerGiven(q.item, given), today)
            }
        }
        return state
    }

    @Test
    fun `guessing guard - a pure guesser never masters the table`() {
        // Levels 4-5 are free input, so a blind hit at ~1/78 can consolidate a single
        // item, and hasEverConsolidated is monotone: over months that becomes close to
        // certain. What holds is the weaker property (#38, mvp-spec §13): guessing never
        // carries the learner through the table, and the residual stays well below the
        // front width. Swept over seeds so that no single one is load-bearing.
        val counts = (1..20).map { seed ->
            val state = runPureGuesser(seed, days = 365)
            assertFalse(
                "a pure guesser completed all 36 items (seed=$seed)",
                state.wasEverCompleted,
            )
            state.items.values.count { it.hasEverConsolidated }
        }
        // measured over 60 seeds: mean 2.1 / max 7 at 365 days, max 10 of 36 at 1095
        assertTrue("guessing residual too large: $counts", counts.average() < 5.0)
    }

    @Test
    fun `admission keeps the day goal from receding while the child works`() {
        // a fluent learner mid-day: consolidating an item admits a new one that is
        // not open until tomorrow, so openToday shrinks monotonically
        var state = allWrongCalibrated()
        val today = day(1)
        val rng = Random(5)
        var open = Engine.openToday(state, today).size
        // promote everything over 4 days, checking within-day monotonicity
        for (d in 1..16) {
            val t = day(d)
            open = Engine.openToday(state, t).size
            var lastShown: ItemKey? = null
            while (true) {
                val q = Engine.nextQuestion(state, t, lastShown, rng) ?: break
                lastShown = q.item
                state = Engine.reduce(state, Event.AnswerGiven(q.item, q.item.product), t)
                val nowOpen = Engine.openToday(state, t).size
                assertTrue("open set grew mid-day", nowOpen < open)
                open = nowOpen
            }
        }
        assertTrue(state.wasEverCompleted)
    }
}
