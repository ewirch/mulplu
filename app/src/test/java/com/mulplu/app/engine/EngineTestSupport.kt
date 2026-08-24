package com.mulplu.app.engine

import java.time.LocalDate

val DAY1: LocalDate = LocalDate.of(2026, 8, 1)

fun day(n: Int): LocalDate = DAY1.plusDays((n - 1).toLong())

fun ItemKey.answer(state: AppState, correct: Boolean, today: LocalDate): AppState =
    Engine.reduce(
        state,
        Event.AnswerGiven(this, if (correct) product else -1),
        today,
    )

/** A state where calibration is done and every item was seeded level 1 on [seedDay]. */
fun allWrongCalibrated(seedDay: LocalDate = day(0)): AppState {
    // Build directly instead of replaying probes: mercy stop would kick in.
    return AppState(
        items = Ranking.ORDER.associateWith {
            ItemState(level = 1, lastCountedOn = seedDay)
        },
        calibrationIndex = Ranking.ORDER.size,
    )
}
