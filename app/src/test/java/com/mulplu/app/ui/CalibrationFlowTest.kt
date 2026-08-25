package com.mulplu.app.ui

import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.Engine
import com.mulplu.app.engine.Event
import com.mulplu.app.engine.Ranking
import com.mulplu.app.engine.day
import org.junit.Assert.assertEquals
import org.junit.Test

class CalibrationFlowTest {

    private fun probe(state: AppState, correct: Boolean): AppState {
        val item = Ranking.ORDER[state.calibrationIndex]
        return Engine.reduce(
            state,
            Event.CalibrationProbeAnswered(if (correct) item.product else null),
            day(1),
        )
    }

    @Test
    fun `fresh install lands on the intro`() {
        assertEquals(CalPhase.Intro, initialCalPhase(AppState.initial()))
    }

    @Test
    fun `resume greets by round, not count`() {
        var state = AppState.initial()
        repeat(13) { state = probe(state, correct = true) }
        // 13 probes done: mid round 3 -> "Wir waren bei Runde 3 von 6"
        assertEquals(CalPhase.Resume(round = 3), initialCalPhase(state))
    }

    @Test
    fun `mid-round answer continues probing`() {
        val state = probe(AppState.initial(), correct = true)
        assertEquals(CalPhase.Probe, calPhaseAfterProbe(0, state))
    }

    @Test
    fun `round boundary lands on the breather`() {
        var state = AppState.initial()
        repeat(6) { state = probe(state, correct = true) }
        assertEquals(CalPhase.Breather(doneRound = 1), calPhaseAfterProbe(5, state))
    }

    @Test
    fun `six straight misses trigger the mercy stop`() {
        var state = AppState.initial()
        repeat(6) { state = probe(state, correct = false) }
        assertEquals(CalPhase.Mercy, calPhaseAfterProbe(5, state))
    }

    @Test
    fun `a full correct pass ends in the reveal with 36 known`() {
        var state = AppState.initial()
        repeat(36) { state = probe(state, correct = true) }
        assertEquals(CalPhase.Reveal(known = 36), calPhaseAfterProbe(35, state))
    }

    @Test
    fun `a sixth miss on the last item is the natural end, not a mercy stop`() {
        var state = AppState.initial()
        repeat(30) { state = probe(state, correct = true) }
        repeat(6) { state = probe(state, correct = false) }
        assertEquals(CalPhase.Reveal(known = 30), calPhaseAfterProbe(35, state))
    }

    @Test
    fun `the stamp window after the mercy-stop probe still belongs to calibration`() {
        var state = AppState.initial()
        repeat(6) { state = probe(state, correct = false) }
        // The probe is stamped, the phase has not moved on yet (#33): no map.
        assertEquals(true, inCalibration(state, CalPhase.Probe))
    }

    @Test
    fun `the mercy stop and the reveal are shown on the calibration screen`() {
        var state = AppState.initial()
        repeat(6) { state = probe(state, correct = false) }
        assertEquals(true, inCalibration(state, CalPhase.Mercy))
        assertEquals(true, inCalibration(state, CalPhase.Reveal(known = 0)))
    }

    @Test
    fun `leaving the reveal leaves the calibration screen`() {
        var state = AppState.initial()
        repeat(36) { state = probe(state, correct = true) }
        assertEquals(false, inCalibration(state, null))
    }

    @Test
    fun `knownCount counts only probed-correct items`() {
        var state = AppState.initial()
        repeat(4) { state = probe(state, correct = true) }
        state = probe(state, correct = false)
        assertEquals(4, knownCount(state))
    }
}
