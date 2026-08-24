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
 * carry no overlay. In admission order — the day close animates them in
 * this sequence.
 */
fun promotedToday(state: AppState, today: LocalDate): List<ItemKey> =
    Ranking.ORDER.filter { state.items.getValue(it).lastPromotedOn == today }
