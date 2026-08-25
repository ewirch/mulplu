package com.mulplu.app.ui

import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.Engine
import com.mulplu.app.engine.ItemKey
import com.mulplu.app.engine.Ranking
import java.time.LocalDate

/**
 * Tile base states on the progress map (mvp-spec §10). Derived from pool
 * membership; the level itself stays invisible — "in play" spans levels 1–4.
 */
enum class TileState { Consolidated, InPlay, NotStarted }

/** Base state of every item, derived at read time (mvp-spec §12). */
fun tileStates(state: AppState): Map<ItemKey, TileState> {
    val pool = Engine.pool(state).toSet()
    return Ranking.ORDER.associateWith { item ->
        when {
            state.items.getValue(item).level == 5 -> TileState.Consolidated
            item in pool -> TileState.InPlay
            else -> TileState.NotStarted
        }
    }
}

/**
 * Items carrying the "promoted today" corner arrow: any level-up today,
 * driven by `lastPromotedOn`; resets at midnight (mvp-spec §10). Demotions
 * carry no overlay. In admission order.
 */
fun promotedToday(state: AppState, today: LocalDate): List<ItemKey> =
    Ranking.ORDER.filter { state.items.getValue(it).lastPromotedOn == today }

/**
 * The day-close pulse waves. Promoted items grouped by their diagonal on the
 * map — the triangle's tile `b × a` sits in row `b`, column `a`, so items with
 * an equal factor sum share one diagonal and pulse together. Ascending sum
 * runs the wave from the top-left corner to the bottom-right one.
 */
fun promotedWaves(state: AppState, today: LocalDate): List<List<ItemKey>> =
    promotedToday(state, today)
        .groupBy { it.a + it.b }
        .toSortedMap()
        .values
        .toList()
