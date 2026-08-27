package com.mulplu.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulplu.app.data.StateRepository
import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.Engine
import com.mulplu.app.engine.Event
import com.mulplu.app.engine.ItemKey
import com.mulplu.app.engine.Question
import com.mulplu.app.engine.Ranking
import java.time.LocalDate
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** The three screens (ADR-0003: a 3-case sealed class, no Navigation-Compose). */
sealed interface Screen {
    /** Home: the progress map. */
    data object Map : Screen

    /** First run only; phases are sub-state of the screen (mvp-spec §10). */
    data object Calibration : Screen

    data object Question : Screen
}

/** What the question screen shows for one presentation. */
data class QuestionUi(
    val question: Question,
    /** Choice values incl. the correct one, shuffled; null = free input. */
    val choices: List<Int>?,
    /** Fraction of pool items satisfied today, for the digit-free progress bar. */
    val dayProgress: Float,
)

/** Per-answer feedback phase. */
sealed interface Feedback {
    /** ~550 ms: green + sparks + chime. */
    data class Correct(val given: Int) : Feedback

    /** ~2.6 s: error beat (red / fade of the pick) + answer reveal. */
    data class Wrong(val given: Int) : Feedback

    /** "Weiß nicht": answer reveal only, same ~2.6 s, no error beat. */
    data object Reveal : Feedback
}

/** One-shot sounds; the screen collects and plays them (device audio). */
enum class Sound { Correct, Wrong }

class AppViewModel(
    private val repository: StateRepository,
    private val random: Random = Random.Default,
) : ViewModel() {

    val appState: StateFlow<AppState?> = repository.state
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    var screen by mutableStateOf<Screen>(Screen.Map)
        private set

    var questionUi by mutableStateOf<QuestionUi?>(null)
        private set

    var feedback by mutableStateOf<Feedback?>(null)
        private set

    /** Monotonically increasing token; the screen plays one sound per bump. */
    var soundEvent by mutableStateOf<Pair<Long, Sound>?>(null)
        private set

    /** Bumped when the day goal was just reached — the map animates the day's movements. */
    var dayCloseTick by mutableStateOf(0L)
        private set

    /** Bumped when the terminal event (36th consolidation, one-time) must fire on the map. */
    var terminalTick by mutableStateOf(0L)
        private set

    /** Wrong values given per item today — in-memory only (mvp-spec §12). */
    private val rejectedToday = mutableMapOf<ItemKey, MutableSet<Int>>()
    private var rejectedDate: LocalDate = AppClock.today()
    private var soundToken = 0L
    private var answering = false

    /** Terminal event waiting for the next map landing (in-memory; the flag itself is `wasEverCompleted`). */
    private var terminalPending = false

    fun startPractice() {
        viewModelScope.launch {
            val state = repository.state.filterNotNull().first()
            presentNext(state, lastShown = null)
        }
    }

    /** Backgrounding: an open question evaporates, return lands on the map (mvp-spec §10). */
    fun onBackgrounded() {
        if (screen == Screen.Question) backToMap()
    }

    fun backToMap() {
        screen = Screen.Map
        questionUi = null
        feedback = null
        answering = false
        if (terminalPending) {
            terminalPending = false
            terminalTick += 1
        }
    }

    // ------------------------------------------------------------ calibration

    /** Current calibration phase; null once the pass (incl. reveal) is left. */
    var calibrationPhase by mutableStateOf<CalPhase?>(null)
        private set

    /** The probe on screen while [calibrationPhase] is [CalPhase.Probe]. */
    var calProbe by mutableStateOf<CalProbeUi?>(null)
        private set

    /** True during the neutral acknowledgment window after a probe answer. */
    var calAcking by mutableStateOf(false)
        private set

    /** Sets the landing phase (intro or resume-by-round) once, on first show. */
    fun ensureCalibrationPhase() {
        if (calibrationPhase != null) return
        viewModelScope.launch {
            val state = repository.state.filterNotNull().first()
            if (calibrationPhase == null && !state.calibrationComplete) {
                calibrationPhase = initialCalPhase(state)
            }
        }
    }

    /** Intro / resume / breather CTA: present the next probe. */
    fun continueCalibration() {
        viewModelScope.launch {
            val state = repository.state.filterNotNull().first()
            presentCalProbe(state)
        }
    }

    private fun presentCalProbe(state: AppState) {
        if (state.calibrationComplete) return
        val item = Ranking.ORDER[state.calibrationIndex]
        val flip = random.nextBoolean()
        calProbe = CalProbeUi(
            item = item,
            shownA = if (flip) item.b else item.a,
            shownB = if (flip) item.a else item.b,
        )
        calibrationPhase = CalPhase.Probe
    }

    /** A probe answer; `given == null` is "Weiß nicht" (a miss, mvp-spec §8). */
    fun answerCalibrationProbe(given: Int?) {
        if (calAcking || calibrationPhase != CalPhase.Probe) return
        calAcking = true
        viewModelScope.launch {
            var beforeIndex = 0
            var completedBefore = false
            val after = repository.update { state ->
                if (state.calibrationComplete) return@update state
                beforeIndex = state.calibrationIndex
                completedBefore = state.wasEverCompleted
                Engine.reduce(state, Event.CalibrationProbeAnswered(given), AppClock.today())
            }
            if (!completedBefore && after.wasEverCompleted) terminalPending = true
            // Neutral acknowledgment: the stamp lands, no right/wrong echo.
            delay(CAL_STAMP_MS)
            calAcking = false
            when (val next = calPhaseAfterProbe(beforeIndex, after)) {
                CalPhase.Probe -> presentCalProbe(after)
                else -> {
                    calProbe = null
                    calibrationPhase = next
                }
            }
        }
    }

    /** Mercy-stop CTA: on to the reveal. */
    fun mercyAcknowledged() {
        viewModelScope.launch {
            val state = repository.state.filterNotNull().first()
            calibrationPhase = CalPhase.Reveal(known = knownCount(state))
        }
    }

    /** Reveal CTA: straight into day 1 — no separate hand-over (mvp-spec §8). */
    fun finishCalibration() {
        calibrationPhase = null
        startPractice()
    }

    /** An answer on the question screen; `given == null` is "Weiß nicht". */
    fun answer(given: Int?) {
        val ui = questionUi ?: return
        if (answering || feedback != null) return
        answering = true
        viewModelScope.launch {
            val item = ui.question.item
            val correct = given == item.product
            var completedBefore = false
            val after = repository.update {
                completedBefore = it.wasEverCompleted
                Engine.reduce(it, Event.AnswerGiven(item, given), AppClock.today())
            }
            if (!completedBefore && after.wasEverCompleted) terminalPending = true
            if (!correct && given != null) {
                rejectedTodayFor(item).add(given)
            }
            feedback = when {
                correct -> Feedback.Correct(given)
                given == null -> Feedback.Reveal
                else -> Feedback.Wrong(given)
            }
            when (feedback) {
                is Feedback.Correct -> emitSound(Sound.Correct)
                is Feedback.Wrong -> emitSound(Sound.Wrong)
                else -> Unit // "Weiß nicht": no error beat, no wrong tone
            }
            delay(if (correct) CORRECT_FEEDBACK_MS else WRONG_FEEDBACK_MS)
            feedback = null
            answering = false
            // Backgrounded mid-feedback: the question already evaporated — don't re-open one.
            if (screen == Screen.Question) presentNext(after, lastShown = item)
        }
    }

    private fun presentNext(state: AppState, lastShown: ItemKey?) {
        val today = AppClock.today()
        if (rejectedDate != today) {
            rejectedToday.clear()
            rejectedDate = today
        }
        val question = Engine.nextQuestion(state, today, lastShown, random)
        if (question == null) {
            // Day goal reached: the questions simply cease; the map animates
            // the day's movements (mvp-spec §10). When the terminal event is
            // pending it supersedes the day close.
            if (!terminalPending && lastShown != null) dayCloseTick += 1
            backToMap()
            return
        }
        present(state, question)
    }

    /** Puts one drawn question on screen: options and day progress from [state]. */
    private fun present(state: AppState, question: Question) {
        val choices = question.optionCount?.let { n ->
            Engine.buildChoices(
                question.shownA,
                question.shownB,
                n,
                rejectedTodayFor(question.item),
                random,
            )
        }
        val pool = Engine.pool(state)
        val today = AppClock.today()
        val satisfied = pool.count { state.items.getValue(it).satisfiedOn == today }
        questionUi = QuestionUi(
            question = question,
            choices = choices,
            dayProgress = if (pool.isEmpty()) 0f else satisfied.toFloat() / pool.size,
        )
        screen = Screen.Question
    }

    // ------------------------------------------------------------- test hooks
    // Manual-testing shortcuts (#30). Only ever called from the test panel,
    // which exists only where BuildConfig.TEST_HOOKS is true.

    /** Ends the day and begins the next one, without touching the system clock. */
    fun testAdvanceDay() {
        AppClock.dayOffset += 1
        rejectedToday.clear()
        rejectedDate = AppClock.today()
        backToMap()
    }

    /** Every item consolidated — lands on the map with the terminal animation. */
    fun testConsolidateAll() {
        viewModelScope.launch {
            repository.update(TestHooks::consolidateAll)
            backToMap()
            terminalTick += 1
        }
    }

    /** Ladder back to the floor, pool back to the first ten; calibration stays done. */
    fun testResetLevels() {
        viewModelScope.launch {
            repository.update(TestHooks::resetLevels)
            rejectedToday.clear()
            backToMap()
        }
    }

    /** Wipes everything: the next screen is the calibration's intro. */
    fun testResetAll() {
        viewModelScope.launch {
            repository.update { AppState.initial() }
            AppClock.dayOffset = 0
            rejectedToday.clear()
            calibrationPhase = null
            calProbe = null
            calAcking = false
            backToMap()
        }
    }

    /** Question screen: the item on screen jumps to [level] and is re-presented. */
    fun testSetLevel(level: Int) {
        val shown = questionUi?.question ?: return
        viewModelScope.launch {
            val after = repository.update { TestHooks.setLevel(it, shown.item, level) }
            feedback = null
            answering = false
            // Same task, same orientation — only the presentation changes.
            present(after, shown.copy(optionCount = Engine.optionCount(level)))
        }
    }

    private fun rejectedTodayFor(item: ItemKey): MutableSet<Int> =
        rejectedToday.getOrPut(item) { mutableSetOf() }

    private fun emitSound(sound: Sound) {
        soundToken += 1
        soundEvent = soundToken to sound
    }

    companion object {
        const val CORRECT_FEEDBACK_MS = 550L
        const val WRONG_FEEDBACK_MS = 2600L
        const val CAL_STAMP_MS = 500L
    }
}
