package com.mulplu.app.engine

import java.time.LocalDate

/**
 * An item is an unordered factor pair from 2..9 (CONTEXT.md). `3×4` and `4×3`
 * are one item with one state; presentation order is drawn per question.
 */
data class ItemKey(val a: Int, val b: Int) {
    init {
        require(a in 2..9 && b in 2..9 && a <= b) { "invalid item $a x $b" }
    }

    val product: Int get() = a * b

    companion object {
        fun of(x: Int, y: Int): ItemKey = ItemKey(minOf(x, y), maxOf(x, y))
    }
}

/** Per-item persistent state (ADR-0001). */
data class ItemState(
    val level: Int = 1,
    val lastCountedOn: LocalDate? = null,
    val satisfiedOn: LocalDate? = null,
    val hasEverConsolidated: Boolean = false,
    val lastPromotedOn: LocalDate? = null,
) {
    init {
        require(level in 1..5) { "level out of range: $level" }
    }
}

/** The whole persistent state, one immutable value (ADR-0001, ADR-0002). */
data class AppState(
    val items: Map<ItemKey, ItemState>,
    val calibrationIndex: Int = 0,
    val calibrationMissStreak: Int = 0,
    val wasEverCompleted: Boolean = false,
    val version: Int = 1,
) {
    val calibrationComplete: Boolean get() = calibrationIndex >= Ranking.ORDER.size

    companion object {
        fun initial(): AppState = AppState(items = Ranking.ORDER.associateWith { ItemState() })
    }
}
