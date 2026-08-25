package com.mulplu.app.ui

import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.ItemState
import com.mulplu.app.engine.ItemKey

/**
 * State shortcuts for manual testing (#30), reachable only where
 * `BuildConfig.TEST_HOOKS` is true. Pure transforms so they can be tested;
 * they bypass the engine on purpose — that is the point of a shortcut.
 */
object TestHooks {

    /** All 36 items consolidated: the state the terminal event fires on. */
    fun consolidateAll(state: AppState): AppState = state.copy(
        items = state.items.mapValues { (_, s) -> s.copy(level = 5, hasEverConsolidated = true) },
        wasEverCompleted = true,
    )

    /**
     * Every item back to the ladder's floor and the pool back to the learning
     * front's first ten. The calibration stays done — use [AppState.initial]
     * to get the calibration back.
     */
    fun resetLevels(state: AppState): AppState = state.copy(
        items = state.items.mapValues { ItemState() },
        wasEverCompleted = false,
    )

    /** One item to an exact level; level 5 implies consolidated. */
    fun setLevel(state: AppState, item: ItemKey, level: Int): AppState {
        val before = state.items.getValue(item)
        val after = before.copy(
            level = level,
            hasEverConsolidated = before.hasEverConsolidated || level == 5,
        )
        return state.copy(items = state.items + (item to after))
    }
}
