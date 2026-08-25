package com.mulplu.app.ui

import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.Engine
import com.mulplu.app.engine.ItemKey
import com.mulplu.app.engine.Ranking

/**
 * Sub-state of the calibration screen (mvp-spec §8/§10): intro, probe,
 * breather, mercy stop, reveal are phases of one screen, not navigation
 * destinations. Never persisted — the reveal replays the resume rule after a
 * restart (mvp-spec §8).
 */
sealed interface CalPhase {
    /** Fresh first run: the companion introduces the pass. */
    data object Intro : CalPhase

    /** Interrupted pass, relaunch: greeting by round, not count (mvp-spec §8). */
    data class Resume(val round: Int) : CalPhase

    /** A probe is on screen. */
    data object Probe : CalPhase

    /** Between rounds: round done, dots filled, "Weiter?". */
    data class Breather(val doneRound: Int) : CalPhase

    /** Mercy stop after 6 straight misses — framed as the companion's decision. */
    data object Mercy : CalPhase

    /** The reveal: the map builds tile by tile; `known` = items probed correct. */
    data class Reveal(val known: Int) : CalPhase
}

/** One calibration probe as presented (orientation is a 50/50 draw, not state). */
data class CalProbeUi(val item: ItemKey, val shownA: Int, val shownB: Int)

/** Rounds of six (mvp-spec §8). */
const val CAL_ROUND_SIZE = 6
const val CAL_ROUNDS = 6

/** Phase to land on when the calibration screen opens (mvp-spec §8: resume at first unprobed item). */
fun initialCalPhase(state: AppState): CalPhase =
    if (state.calibrationIndex == 0) {
        CalPhase.Intro
    } else {
        CalPhase.Resume(round = state.calibrationIndex / CAL_ROUND_SIZE + 1)
    }

/**
 * Phase after a probe was reduced. [beforeIndex] is the probed item's index in
 * admission order. A mercy stop jumps `calibrationIndex` to 36 from anywhere
 * before the last item; a 6th straight miss *on* the last item is simply the
 * natural end of the pass, not a mercy stop.
 */
fun calPhaseAfterProbe(beforeIndex: Int, after: AppState): CalPhase = when {
    after.calibrationComplete ->
        if (after.calibrationMissStreak >= Engine.MERCY_STOP && beforeIndex < Ranking.ORDER.size - 1) {
            CalPhase.Mercy
        } else {
            CalPhase.Reveal(known = knownCount(after))
        }
    after.calibrationIndex % CAL_ROUND_SIZE == 0 ->
        CalPhase.Breather(doneRound = after.calibrationIndex / CAL_ROUND_SIZE)
    else -> CalPhase.Probe
}

/**
 * Whether the calibration screen owns the display. The pass' last probe already
 * flips `calibrationComplete`, so completion alone cannot end the screen — it
 * stays until the phase is left behind. Otherwise the map shows through the
 * stamp window between the last probe and the mercy stop / reveal (#33).
 */
fun inCalibration(state: AppState, phase: CalPhase?): Boolean =
    !state.calibrationComplete || phase != null

/** Items the child produced correctly — "n kannst du schon!" (mvp-spec §8). */
fun knownCount(state: AppState): Int = state.items.values.count { it.hasEverConsolidated }
