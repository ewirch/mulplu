package com.mulplu.app.ui

import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.ItemKey
import com.mulplu.app.engine.ItemState
import com.mulplu.app.engine.Ranking
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** Tile derivation for the progress map (mvp-spec §10). */
class MapTilesTest {

    private val today = LocalDate.of(2026, 8, 24)

    private fun state(mutate: MutableMap<com.mulplu.app.engine.ItemKey, ItemState>.() -> Unit): AppState {
        val items = Ranking.ORDER.associateWith { ItemState() }.toMutableMap()
        items.mutate()
        return AppState(items = items, calibrationIndex = Ranking.ORDER.size)
    }

    @Test
    fun `cold start - learning front in play, rest not started`() {
        val tiles = tileStates(state {})
        val front = Ranking.ORDER.take(Ranking.FRONT_WIDTH).toSet()
        for (item in Ranking.ORDER) {
            val expected = if (item in front) TileState.InPlay else TileState.NotStarted
            assertEquals("tile $item", expected, tiles.getValue(item))
        }
    }

    @Test
    fun `level 5 is consolidated, a revoked item reads as in play`() {
        val consolidated = Ranking.ORDER[0]
        val revoked = Ranking.ORDER[1]
        val tiles = tileStates(
            state {
                this[consolidated] = ItemState(level = 5, hasEverConsolidated = true)
                this[revoked] = ItemState(level = 4, hasEverConsolidated = true)
            },
        )
        assertEquals(TileState.Consolidated, tiles.getValue(consolidated))
        assertEquals(TileState.InPlay, tiles.getValue(revoked))
    }

    @Test
    fun `promoted today - stamped today in admission order, older stamps excluded`() {
        val a = Ranking.ORDER[5]
        val b = Ranking.ORDER[2]
        val old = Ranking.ORDER[3]
        val s = state {
            this[a] = ItemState(level = 2, lastPromotedOn = today)
            this[b] = ItemState(level = 3, lastPromotedOn = today)
            this[old] = ItemState(level = 2, lastPromotedOn = today.minusDays(1))
        }
        assertEquals(listOf(b, a), promotedToday(s, today))
    }

    @Test
    fun `pulse waves run diagonal by diagonal, top-left first`() {
        val near = ItemKey.of(2, 3) // sum 5, closest to the top-left corner
        val mid1 = ItemKey.of(3, 5) // sum 8
        val mid2 = ItemKey.of(4, 4) // sum 8 - same diagonal, same wave
        val far = ItemKey.of(8, 9) // sum 17
        val s = state {
            for (item in listOf(far, mid1, near, mid2)) {
                this[item] = ItemState(level = 2, lastPromotedOn = today)
            }
        }
        val waves = promotedWaves(s, today)
        assertEquals(listOf(setOf(near), setOf(mid1, mid2), setOf(far)), waves.map { it.toSet() })
    }
}
