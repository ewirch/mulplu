package com.mulplu.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/** Palette from the prototypes (mvp-spec §9/§10). */
object MulpluColors {
    val InPlayBlue = Color(0xFF5BA3D9)
    val AccentBlue = Color(0xFF3E6FE0)
    val CorrectGreen = Color(0xFF2FA866)
    val WrongRed = Color(0xFFE05B5B)
    val CardGrey = Color(0xFFEFF4F9)
    val TrackGrey = Color(0xFFD5E0EB)
    val Ink = Color(0xFF2B3A4A)
}

/**
 * The question screen (mvp-spec §9): task on top, answers at the bottom,
 * "Weiß nicht" fixed below the answer area, digit-free day-progress bar in
 * the header.
 */
@Composable
fun QuestionScreen(
    ui: QuestionUi,
    feedback: Feedback?,
    onAnswer: (Int?) -> Unit,
) {
    // Two-stage wrong feedback (mvp-spec §9): red flash first, then the answer
    // in green. Held here, not inside the field, so the task line reveals in
    // step with the field instead of during the red flash (#35).
    var wrongStage by remember(feedback) { mutableStateOf(0) }
    LaunchedEffect(feedback) {
        if (feedback is Feedback.Wrong) {
            wrongStage = 1
            kotlinx.coroutines.delay(RevealTimeline.RED_FLASH_MS)
            wrongStage = 2
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp),
    ) {
        DayProgressBar(progress = ui.dayProgress)
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            TaskText(ui = ui, feedback = feedback, wrongStage = wrongStage)
        }
        if (ui.choices != null) {
            ChoiceGrid(
                choices = ui.choices,
                correct = ui.question.item.product,
                feedback = feedback,
                onPick = onAnswer,
            )
        } else {
            FreeInputArea(
                correct = ui.question.item.product,
                feedback = feedback,
                wrongStage = wrongStage,
                onSubmit = onAnswer,
            )
        }
        Spacer(Modifier.height(16.dp))
        // Fixed position below the answer area; set apart from the options.
        Button(
            onClick = { onAnswer(null) },
            enabled = feedback == null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MulpluColors.InPlayBlue,
                disabledContainerColor = MulpluColors.InPlayBlue.copy(alpha = 0.5f),
            ),
        ) {
            Text("Weiß nicht", fontSize = 18.sp, color = Color.White)
        }
    }
}

/** Digit-free, monotone day-goal bar (mvp-spec §9): no numbers, only fill. */
@Composable
private fun DayProgressBar(progress: Float) {
    val animated by animateFloatAsState(targetValue = progress, animationSpec = tween(400), label = "dayProgress")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MulpluColors.TrackGrey),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated.coerceIn(0f, 1f))
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MulpluColors.CorrectGreen),
        )
    }
}

@Composable
private fun TaskText(ui: QuestionUi, feedback: Feedback?, wrongStage: Int) {
    val q = ui.question
    // Wrong answer in free input: reveal only from stage 2 on, so the solution
    // is not on screen while the field is still flashing red (#35).
    val wrongRevealed = feedback is Feedback.Wrong && (ui.choices != null || wrongStage == 2)
    val revealForFree = ui.choices == null && (wrongRevealed || feedback is Feedback.Reveal)
    Text(
        text = if (revealForFree) {
            "${q.shownA} × ${q.shownB} = ${q.item.product}"
        } else {
            "${q.shownA} × ${q.shownB} = ?"
        },
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        // Green means "you produced it". After "Weiß nicht" the reveal stays
        // neutral (#32) — otherwise a still-standing correct entry reads as a
        // correct answer, though it was counted as a miss.
        color = if (wrongRevealed) MulpluColors.CorrectGreen else MulpluColors.Ink,
        textAlign = TextAlign.Center,
    )
}

/**
 * Multiple choice, levels 1–3: the grid reflows to the option count —
 * 2 and 3 in one row, 4 as 2×2 (mvp-spec §9). No fixed slots.
 */
@Composable
private fun ChoiceGrid(
    choices: List<Int>,
    correct: Int,
    feedback: Feedback?,
    onPick: (Int) -> Unit,
) {
    val rows = if (choices.size == 4) choices.chunked(2) else listOf(choices)
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                row.forEachIndexed { col, value ->
                    // Grow pivot: edge columns grow inward, every button grows
                    // upward into the flexible task area — never off screen and
                    // never down into "Weiß nicht" (#25).
                    val pivotX = when {
                        row.size == 1 -> 0.5f
                        col == 0 -> 0f
                        col == row.size - 1 -> 1f
                        else -> 0.5f
                    }
                    ChoiceButton(
                        value = value,
                        isCorrect = value == correct,
                        feedback = feedback,
                        growOrigin = TransformOrigin(pivotX, 1f),
                        onPick = { onPick(value) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChoiceButton(
    value: Int,
    isCorrect: Boolean,
    feedback: Feedback?,
    growOrigin: TransformOrigin,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Feedback per mvp-spec §9: correct pick turns green with sparks; on a
    // wrong pick (and on "Weiß nicht") all wrong options fade out — including
    // the picked one — and the correct button grows in its normal colour.
    val revealing = feedback is Feedback.Wrong || feedback is Feedback.Reveal
    val picked = feedback is Feedback.Correct && feedback.given == value
    // Scripted rather than target-driven (#28): grow *and* shrink have to finish
    // inside the feedback window, and once the feedback clears the button snaps
    // back — otherwise the next question shows up with the previous correct
    // button still magnified, shrinking afterwards.
    val fade = remember { Animatable(1f) }
    val grow = remember { Animatable(1f) }
    LaunchedEffect(revealing, isCorrect) {
        when {
            !revealing -> {
                fade.snapTo(1f)
                grow.snapTo(1f)
            }
            isCorrect -> {
                kotlinx.coroutines.delay(RevealTimeline.GROW_DELAY_MS)
                grow.animateTo(RevealTimeline.GROW_SCALE, tween(RevealTimeline.GROW_MS))
                kotlinx.coroutines.delay(RevealTimeline.HOLD_MS)
                grow.animateTo(1f, tween(RevealTimeline.SHRINK_MS))
            }
            else -> fade.animateTo(0f, tween(RevealTimeline.FADE_MS))
        }
    }
    Box(modifier = modifier.aspectRatio(1.6f), contentAlignment = Alignment.Center) {
        Surface(
            onClick = onPick,
            enabled = feedback == null,
            shape = RoundedCornerShape(18.dp),
            color = if (picked) MulpluColors.CorrectGreen else MulpluColors.CardGrey,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = fade.value
                    scaleX = grow.value
                    scaleY = grow.value
                    transformOrigin = growOrigin
                },
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$value",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (picked) Color.White else MulpluColors.Ink,
                )
            }
        }
        if (picked) Sparks()
    }
}

/** Spark particles bursting from the correct answer (mvp-spec §9). */
@Composable
fun Sparks(color: Color = MulpluColors.CorrectGreen) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(500)) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val p = progress.value
                if (p <= 0f || p >= 1f) return@drawBehind
                val center = Offset(size.width / 2f, size.height / 2f)
                val reach = size.minDimension * (0.5f + p)
                repeat(10) { i ->
                    val angle = i / 10f * 2f * Math.PI.toFloat()
                    val r = reach * (0.4f + 0.6f * p)
                    drawCircle(
                        color = color.copy(alpha = 1f - p),
                        radius = size.minDimension * 0.045f * (1f - p * 0.6f),
                        center = center + Offset(cos(angle) * r, sin(angle) * r),
                    )
                }
            },
    )
}

/**
 * Free input, levels 4–5: bordered field with placeholder + flat round-key
 * numeric keypad; submission explicit via ✓ (mvp-spec §9).
 */
@Composable
private fun FreeInputArea(
    correct: Int,
    feedback: Feedback?,
    /** Stage of the two-stage wrong feedback, driven by the screen (#35). */
    wrongStage: Int,
    onSubmit: (Int) -> Unit,
) {
    var entered by remember(correct, feedback == null) { mutableStateOf("") }
    val wrong = feedback is Feedback.Wrong
    val correctFb = feedback is Feedback.Correct
    val reveal = feedback is Feedback.Reveal
    // The "Weiß nicht" reveal is neutral, not green (#32): green is reserved for
    // an answer the child produced, and the entry it typed stays on screen.
    val borderColor = when {
        correctFb -> MulpluColors.CorrectGreen
        wrong && wrongStage == 1 -> MulpluColors.WrongRed
        wrong && wrongStage == 2 -> MulpluColors.CorrectGreen
        else -> MulpluColors.InPlayBlue
    }
    val fieldText = when {
        correctFb -> "$correct"
        wrong && wrongStage == 1 -> entered.ifEmpty { (feedback as Feedback.Wrong).given.toString() }
        (wrong && wrongStage == 2) || reveal -> "$correct"
        else -> entered
    }

    Column {
        // Wrapper box so the sparks can burst past the field's rounded clip (#36).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (correctFb) MulpluColors.CorrectGreen.copy(alpha = 0.15f) else Color.White)
                    .border(2.dp, borderColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (fieldText.isEmpty()) {
                    Text(
                        text = "Tippe deine Antwort ein …",
                        fontSize = 16.sp,
                        color = MulpluColors.TrackGrey,
                    )
                } else {
                    Text(
                        text = fieldText,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            wrong && wrongStage == 1 -> MulpluColors.WrongRed
                            correctFb || (wrong && wrongStage == 2) -> MulpluColors.CorrectGreen
                            else -> MulpluColors.Ink
                        },
                    )
                }
                if (feedback == null) BlinkingCursor(hasText = fieldText.isNotEmpty())
            }
            // Same reward as a correct button pick (mvp-spec §9). Own square
            // canvas so the burst has a choice button's reach — the flat field
            // alone would squeeze it into a smudge over the digits.
            if (correctFb) Box(Modifier.requiredSize(120.dp)) { Sparks() }
        }
        Spacer(Modifier.height(14.dp))
        Keypad(
            enabled = feedback == null,
            onDigit = { d -> if (entered.length < 3) entered += d },
            onBackspace = { entered = entered.dropLast(1) },
            onSubmit = { entered.toIntOrNull()?.let(onSubmit) },
        )
    }
}

@Composable
private fun BlinkingCursor(hasText: Boolean) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(530)
            visible = !visible
        }
    }
    if (!hasText) {
        Box(
            modifier = Modifier
                .size(2.dp, 28.dp)
                .alpha(if (visible) 1f else 0f)
                .background(MulpluColors.InPlayBlue),
        )
    }
}

/** Flat round-key numeric keypad: 1–9, 0, ⌫, ✓ (mvp-spec §9). */
@Composable
internal fun Keypad(
    enabled: Boolean,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onSubmit: () -> Unit,
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("⌫", "0", "✓"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { key ->
                    val isSubmit = key == "✓"
                    Surface(
                        onClick = {
                            when (key) {
                                "⌫" -> onBackspace()
                                "✓" -> onSubmit()
                                else -> onDigit(key)
                            }
                        },
                        enabled = enabled,
                        shape = CircleShape,
                        color = if (isSubmit) MulpluColors.AccentBlue else MulpluColors.CardGrey,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.5f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = key,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSubmit) Color.White else MulpluColors.Ink,
                            )
                        }
                    }
                }
            }
        }
    }
}
