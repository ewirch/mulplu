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

    /** First run only. Placeholder scaffold until #22 delivers the real flow. */
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
    private var rejectedDate: LocalDate = LocalDate.now()
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

    /** Temporary probe driver for the #22 placeholder. */
    fun answerCalibrationProbe(correct: Boolean) {
        viewModelScope.launch {
            var completedBefore = false
            val after = repository.update { state ->
                if (state.calibrationComplete) return@update state
                completedBefore = state.wasEverCompleted
                val item = com.mulplu.app.engine.Ranking.ORDER[state.calibrationIndex]
                Engine.reduce(
                    state,
                    Event.CalibrationProbeAnswered(if (correct) item.product else null),
                    LocalDate.now(),
                )
            }
            if (!completedBefore && after.wasEverCompleted) terminalPending = true
            if (after.calibrationComplete) backToMap()
        }
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
                Engine.reduce(it, Event.AnswerGiven(item, given), LocalDate.now())
            }
            if (!completedBefore && after.wasEverCompleted) terminalPending = true
            if (!correct && given != null) {
                rejectedTodayFor(item).add(given)
            }
            feedback = when {
                correct -> Feedback.Correct(given!!)
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
            presentNext(after, lastShown = item)
        }
    }

    private fun presentNext(state: AppState, lastShown: ItemKey?) {
        val today = LocalDate.now()
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
        val satisfied = pool.count { state.items.getValue(it).satisfiedOn == today }
        questionUi = QuestionUi(
            question = question,
            choices = choices,
            dayProgress = if (pool.isEmpty()) 0f else satisfied.toFloat() / pool.size,
        )
        screen = Screen.Question
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
    }
}
