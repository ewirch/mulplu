package com.mulplu.app.data

import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.ItemKey
import com.mulplu.app.engine.ItemState
import java.time.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * JSON codec for [AppState] (ADR-0002).
 *
 * The engine model stays serialization-free; this file owns the stored shape.
 * Migration policy:
 *  - added fields with defaults are free (kotlinx.serialization defaults +
 *    `ignoreUnknownKeys`),
 *  - breaking changes bump [CURRENT_VERSION] and get a hand-written mapping
 *    in [migrate].
 */
object StateJson {

    const val CURRENT_VERSION = 1

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Serializable
    data class StoredItem(
        val level: Int = 1,
        val lastCountedOn: String? = null,
        val satisfiedOn: String? = null,
        val hasEverConsolidated: Boolean = false,
        val lastPromotedOn: String? = null,
    )

    @Serializable
    data class StoredState(
        val version: Int = CURRENT_VERSION,
        // Keys are "a x b" with a <= b, e.g. "3x4".
        val items: Map<String, StoredItem> = emptyMap(),
        val calibrationIndex: Int = 0,
        val calibrationMissStreak: Int = 0,
        val wasEverCompleted: Boolean = false,
    )

    fun encode(state: AppState): String = json.encodeToString(toStored(state))

    /** @throws IllegalArgumentException / SerializationException on invalid input. */
    fun decode(text: String): AppState = fromStored(migrate(json.decodeFromString<StoredState>(text)))

    private fun migrate(stored: StoredState): StoredState = when (stored.version) {
        CURRENT_VERSION -> stored
        else -> throw IllegalArgumentException("unknown state version ${stored.version}")
    }

    private fun toStored(state: AppState) = StoredState(
        version = CURRENT_VERSION,
        items = state.items.entries.associate { (key, item) ->
            "${key.a}x${key.b}" to StoredItem(
                level = item.level,
                lastCountedOn = item.lastCountedOn?.toString(),
                satisfiedOn = item.satisfiedOn?.toString(),
                hasEverConsolidated = item.hasEverConsolidated,
                lastPromotedOn = item.lastPromotedOn?.toString(),
            )
        },
        calibrationIndex = state.calibrationIndex,
        calibrationMissStreak = state.calibrationMissStreak,
        wasEverCompleted = state.wasEverCompleted,
    )

    private fun fromStored(stored: StoredState): AppState {
        val storedItems = stored.items.entries.associate { (key, item) ->
            parseKey(key) to ItemState(
                level = item.level,
                lastCountedOn = item.lastCountedOn?.let(LocalDate::parse),
                satisfiedOn = item.satisfiedOn?.let(LocalDate::parse),
                hasEverConsolidated = item.hasEverConsolidated,
                lastPromotedOn = item.lastPromotedOn?.let(LocalDate::parse),
            )
        }
        // Every item the engine knows must exist; items missing from the stored
        // map (e.g. state written before an item existed) fall back to defaults.
        val items = AppState.initial().items.mapValues { (key, default) -> storedItems[key] ?: default }
        return AppState(
            items = items,
            calibrationIndex = stored.calibrationIndex,
            calibrationMissStreak = stored.calibrationMissStreak,
            wasEverCompleted = stored.wasEverCompleted,
        )
    }

    private fun parseKey(key: String): ItemKey {
        val (a, b) = key.split("x", limit = 2).map(String::toInt)
        return ItemKey.of(a, b)
    }
}
