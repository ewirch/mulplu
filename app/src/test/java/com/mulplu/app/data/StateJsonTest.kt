package com.mulplu.app.data

import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.ItemKey
import com.mulplu.app.engine.ItemState
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class StateJsonTest {

    private fun sampleState(): AppState {
        val base = AppState.initial()
        val items = base.items.toMutableMap()
        items[ItemKey.of(3, 4)] = ItemState(
            level = 5,
            lastCountedOn = LocalDate.of(2026, 8, 20),
            satisfiedOn = LocalDate.of(2026, 8, 20),
            hasEverConsolidated = true,
            lastPromotedOn = LocalDate.of(2026, 8, 19),
        )
        items[ItemKey.of(7, 8)] = ItemState(level = 2, lastCountedOn = LocalDate.of(2026, 8, 24))
        return base.copy(
            items = items,
            calibrationIndex = 36,
            calibrationMissStreak = 0,
            wasEverCompleted = true,
        )
    }

    @Test
    fun `write-read round-trip preserves the whole state`() {
        val state = sampleState()
        assertEquals(state, StateJson.decode(StateJson.encode(state)))
    }

    @Test
    fun `round-trip of the initial state`() {
        val state = AppState.initial()
        assertEquals(state, StateJson.decode(StateJson.encode(state)))
    }

    @Test
    fun `missing fields fall back to defaults (add-a-field migration)`() {
        // A v1 document written before optional fields existed: only version
        // and one sparse item. Everything absent must come back as defaults.
        val old = """{"version":1,"items":{"3x4":{"level":3}},"calibrationIndex":5}"""
        val state = StateJson.decode(old)
        assertEquals(3, state.items.getValue(ItemKey.of(3, 4)).level)
        assertEquals(null, state.items.getValue(ItemKey.of(3, 4)).lastCountedOn)
        assertEquals(ItemState(), state.items.getValue(ItemKey.of(2, 2)))
        assertEquals(5, state.calibrationIndex)
        assertEquals(false, state.wasEverCompleted)
        assertEquals(36, state.items.size)
    }

    @Test
    fun `unknown fields are ignored (forward-compatible reads)`() {
        val withExtra = """{"version":1,"items":{},"futureField":"whatever"}"""
        assertEquals(AppState.initial(), StateJson.decode(withExtra))
    }

    @Test
    fun `unknown version is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            StateJson.decode("""{"version":99,"items":{}}""")
        }
    }
}
