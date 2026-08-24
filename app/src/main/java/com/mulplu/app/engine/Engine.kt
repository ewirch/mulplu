package com.mulplu.app.engine

import java.time.LocalDate
import kotlin.random.Random

/** Events the engine reduces (ADR-0003: pure `(State, Event, LocalDate) -> State`). */
sealed interface Event {
    /** An answer on the question screen. `given == null` means "Weiß nicht" (a miss). */
    data class AnswerGiven(val item: ItemKey, val given: Int?) : Event

    /** A calibration probe answered; the probed item is `Ranking.ORDER[calibrationIndex]`. */
    data class CalibrationProbeAnswered(val given: Int?) : Event
}

/** What to present next (mvp-spec §7). `optionCount == null` means free input. */
data class Question(
    val item: ItemKey,
    /** Presentation order — a 50/50 draw, not state. */
    val shownA: Int,
    val shownB: Int,
    val optionCount: Int?,
)

/**
 * The pure adaptive engine (mvp-spec §4–§8, ADR-0003). No Android imports, no
 * clock, no `Random` of its own — date and randomness are injected.
 */
object Engine {

    /** Mercy stop: consecutive missed probes that end calibration (mvp-spec §8). */
    const val MERCY_STOP = 6

    /** level -> option count for multiple choice; null = free input. */
    fun optionCount(level: Int): Int? = when (level) {
        1 -> 2
        2 -> 3
        3 -> 4
        else -> null
    }

    // ------------------------------------------------------------- derived sets

    /** pool = {ever consolidated} ∪ learning front (mvp-spec §6). Derived, never stored. */
    fun pool(state: AppState): List<ItemKey> {
        val front = learningFront(state)
        return Ranking.ORDER.filter { state.items.getValue(it).hasEverConsolidated } + front
    }

    /** The first `FRONT_WIDTH` never-consolidated items of the admission order. */
    fun learningFront(state: AppState): List<ItemKey> =
        Ranking.ORDER
            .filter { !state.items.getValue(it).hasEverConsolidated }
            .take(Ranking.FRONT_WIDTH)

    /** Pool items not yet answered correctly today (mvp-spec §7). */
    fun openToday(state: AppState, today: LocalDate): List<ItemKey> =
        pool(state).filter { state.items.getValue(it).satisfiedOn != today }

    // --------------------------------------------------------------- selection

    /**
     * Draws the next question, or null when the day goal is reached. Never the
     * same item twice in a row unless it is the only one open (mvp-spec §7).
     */
    fun nextQuestion(
        state: AppState,
        today: LocalDate,
        lastShown: ItemKey?,
        random: Random,
    ): Question? {
        val open = openToday(state, today)
        if (open.isEmpty()) return null
        val candidates = if (open.size > 1) open.filter { it != lastShown } else open
        val item = candidates.random(random)
        val flip = random.nextBoolean()
        return Question(
            item = item,
            shownA = if (flip) item.b else item.a,
            shownB = if (flip) item.a else item.b,
            optionCount = optionCount(state.items.getValue(item).level),
        )
    }

    // -------------------------------------------------------------- distractors

    /**
     * Draws the answer options for a multiple-choice question (mvp-spec §7).
     * Re-drawn on every presentation. `rejectedToday` are values the child
     * already gave wrongly for this item today (in-memory only, caller-held).
     * Returns `optionCount` values including the correct one, shuffled.
     */
    fun buildChoices(
        a: Int,
        b: Int,
        optionCount: Int,
        rejectedToday: Set<Int>,
        random: Random,
    ): List<Int> {
        val correct = a * b
        val excluded = rejectedToday + correct

        fun clamp(f: Int) = f.coerceIn(2, 9)
        val neighbours = buildSet {
            for (d in listOf(-2, -1, 1, 2)) {
                add(a * clamp(b + d))
                add(clamp(a + d) * b)
            }
        } - excluded

        val candidates = neighbours.toMutableSet()
        if (candidates.size < optionCount - 1) {
            // fall back to the nearest products of the 36-item table, same exclusions
            val tableProducts = Ranking.ORDER.map { it.product }.toSortedSet()
            tableProducts
                .filter { it !in excluded && it !in candidates }
                .sortedBy { kotlin.math.abs(it - correct) }
                .forEach { p ->
                    if (candidates.size < optionCount - 1) candidates.add(p)
                }
        }

        val distractors = weightedSample(
            candidates.toList(),
            optionCount - 1,
            weight = { v -> 1.0 / kotlin.math.abs(v - correct) },
            random = random,
        )
        return (distractors + correct).shuffled(random)
    }

    private fun weightedSample(
        values: List<Int>,
        n: Int,
        weight: (Int) -> Double,
        random: Random,
    ): List<Int> {
        val remaining = values.toMutableList()
        val picked = mutableListOf<Int>()
        while (picked.size < n && remaining.isNotEmpty()) {
            val weights = remaining.map(weight)
            var r = random.nextDouble() * weights.sum()
            var idx = 0
            for ((i, w) in weights.withIndex()) {
                r -= w
                if (r <= 0) {
                    idx = i
                    break
                }
                idx = i
            }
            picked.add(remaining.removeAt(idx))
        }
        return picked
    }

    // ----------------------------------------------------------------- reducer

    /** The single entry point: `(State, Event, LocalDate) -> State`. */
    fun reduce(state: AppState, event: Event, today: LocalDate): AppState = when (event) {
        is Event.AnswerGiven -> onAnswer(state, event.item, event.given, today)
        is Event.CalibrationProbeAnswered -> onCalibrationProbe(state, event.given, today)
    }

    /** Answer handling per mvp-spec §7. "Weiß nicht" (given == null) is a miss. */
    private fun onAnswer(state: AppState, item: ItemKey, given: Int?, today: LocalDate): AppState {
        val before = state.items.getValue(item)
        val correct = given == item.product
        val isCounting = before.lastCountedOn == null || before.lastCountedOn < today

        var after = before
        var items = state.items
        if (isCounting) {
            val newLevel =
                if (correct) minOf(5, before.level + 1) else maxOf(1, before.level - 1)
            after = after.copy(
                level = newLevel,
                lastCountedOn = today,
                lastPromotedOn = if (newLevel > before.level) today else after.lastPromotedOn,
            )
            if (newLevel == 5 && !before.hasEverConsolidated) {
                after = after.copy(hasEverConsolidated = true)
                items = admitNext(state, item, today)
            }
        }
        if (correct) after = after.copy(satisfiedOn = today)
        items = items + (item to after)

        return state.copy(items = items).withCompletionMark()
    }

    /**
     * First-ever consolidation slides the learning front: the next
     * never-consolidated item enters the pool with `satisfiedOn = today`, so it
     * first becomes due tomorrow — the day goal never recedes (mvp-spec §6).
     */
    private fun admitNext(
        state: AppState,
        consolidated: ItemKey,
        today: LocalDate,
    ): Map<ItemKey, ItemState> {
        val oldFront = learningFront(state)
        val admitted = Ranking.ORDER
            .filter { it != consolidated && !state.items.getValue(it).hasEverConsolidated }
            .take(Ranking.FRONT_WIDTH)
            .firstOrNull { it !in oldFront }
            ?: return state.items // window shrinks: fewer than 10 remain
        return state.items +
            (admitted to state.items.getValue(admitted).copy(satisfiedOn = today))
    }

    /**
     * Calibration probe per mvp-spec §8: free input, neutral feedback, each probe
     * is the item's day-1 counting answer. Correct → seeded level 5 and
     * consolidated; wrong → level 1. Mercy stop after 6 consecutive misses seeds
     * the remaining items at level 1 (their default) and ends calibration.
     */
    private fun onCalibrationProbe(state: AppState, given: Int?, today: LocalDate): AppState {
        require(!state.calibrationComplete) { "calibration already complete" }
        val item = Ranking.ORDER[state.calibrationIndex]
        val correct = given == item.product

        val probed =
            if (correct) {
                ItemState(
                    level = 5,
                    lastCountedOn = today,
                    satisfiedOn = today,
                    hasEverConsolidated = true,
                )
            } else {
                ItemState(level = 1, lastCountedOn = today)
            }
        val streak = if (correct) 0 else state.calibrationMissStreak + 1
        val nextIndex =
            if (streak >= MERCY_STOP) Ranking.ORDER.size else state.calibrationIndex + 1

        return state.copy(
            items = state.items + (item to probed),
            calibrationIndex = nextIndex,
            calibrationMissStreak = streak,
        ).withCompletionMark()
    }

    /** `wasEverCompleted` is monotone: set once all 36 items have ever consolidated. */
    private fun AppState.withCompletionMark(): AppState =
        if (!wasEverCompleted && items.values.all { it.hasEverConsolidated }) {
            copy(wasEverCompleted = true)
        } else {
            this
        }
}
