package com.mulplu.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.Ranking
import kotlinx.coroutines.delay

/**
 * The calibration screen (mvp-spec §8) — first run only. Its phases (intro,
 * probe, breather, mercy stop, reveal) are sub-state, not navigation
 * destinations. All feedback is neutral: a stamp marks *that* a probe
 * happened, never how it went. Copy from `prototype/calibration-onboarding`.
 */
@Composable
fun CalibrationScreen(
    state: AppState,
    phase: CalPhase,
    probe: CalProbeUi?,
    acking: Boolean,
    onContinue: () -> Unit,
    onAnswer: (Int?) -> Unit,
    onMercyOk: () -> Unit,
    onFinish: () -> Unit,
) {
    when (phase) {
        CalPhase.Intro -> IntroPhase(onContinue)
        is CalPhase.Resume -> ResumePhase(phase.round, onContinue)
        CalPhase.Probe -> probe?.let { ProbePhase(state, it, acking, onAnswer) }
        is CalPhase.Breather -> BreatherPhase(phase.doneRound, onContinue)
        CalPhase.Mercy -> MercyPhase(onMercyOk)
        is CalPhase.Reveal -> RevealPhase(state, phase.known, onFinish)
    }
}

/** The companion figure — it speaks all the framing (mvp-spec §8). */
@Composable
private fun Companion() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .background(MulpluColors.CardGrey, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "🦊", fontSize = 44.sp)
    }
}

@Composable
private fun PhaseColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        content()
        Spacer(Modifier.weight(1.2f))
    }
}

@Composable
private fun Cta(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MulpluColors.AccentBlue),
    ) {
        Text(text = label, fontSize = 19.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        fontSize = 17.sp,
        color = MulpluColors.Ink,
        textAlign = TextAlign.Center,
        lineHeight = 24.sp,
    )
}

@Composable
private fun Headline(text: String) {
    Text(
        text = text,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = MulpluColors.Ink,
        textAlign = TextAlign.Center,
    )
}

// ------------------------------------------------------------------ phases

@Composable
private fun IntroPhase(onContinue: () -> Unit) {
    PhaseColumn {
        Companion()
        Spacer(Modifier.height(18.dp))
        Headline("Hallo! Ich bin dabei.")
        Spacer(Modifier.height(14.dp))
        BodyText(
            "Wir schauen zusammen, was du schon kannst – " +
                "6 Runden mit 6 Aufgaben. Dauert nur ein paar Minuten.",
        )
        Spacer(Modifier.height(10.dp))
        BodyText(
            "Wenn du eine Antwort nicht weißt, ist das völlig okay – " +
                "dann sag einfach „Weiß nicht“.",
        )
        Spacer(Modifier.height(28.dp))
        Cta("Runde 1 starten", onContinue)
    }
}

@Composable
private fun ResumePhase(round: Int, onContinue: () -> Unit) {
    PhaseColumn {
        Companion()
        Spacer(Modifier.height(18.dp))
        Headline("Da bist du wieder!")
        Spacer(Modifier.height(14.dp))
        BodyText("Wir waren bei Runde $round von $CAL_ROUNDS.")
        Spacer(Modifier.height(16.dp))
        RoundDots(done = round - 1)
        Spacer(Modifier.height(28.dp))
        Cta("Weitermachen", onContinue)
    }
}

@Composable
private fun BreatherPhase(doneRound: Int, onContinue: () -> Unit) {
    val left = CAL_ROUNDS - doneRound
    PhaseColumn {
        Companion()
        Spacer(Modifier.height(18.dp))
        Headline("Runde $doneRound geschafft.")
        Spacer(Modifier.height(14.dp))
        BodyText("Noch $left ${if (left == 1) "Runde" else "Runden"}. Weiter?")
        Spacer(Modifier.height(16.dp))
        RoundDots(done = doneRound)
        Spacer(Modifier.height(28.dp))
        Cta("Weiter", onContinue)
    }
}

@Composable
private fun MercyPhase(onOk: () -> Unit) {
    PhaseColumn {
        Companion()
        Spacer(Modifier.height(18.dp))
        Headline("Wir hören hier auf.")
        Spacer(Modifier.height(14.dp))
        BodyText("Du musst nicht alles vorher zeigen. Ab jetzt helfe ich dir bei jeder Aufgabe.")
        Spacer(Modifier.height(28.dp))
        Cta("Okay", onOk)
    }
}

/** Dots across rounds — the coarse of the two monotone progress scales. */
@Composable
private fun RoundDots(done: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(CAL_ROUNDS) { k ->
            Box(
                modifier = Modifier
                    .size(13.dp)
                    .background(
                        if (k < done) MulpluColors.InPlayBlue else MulpluColors.TrackGrey,
                        CircleShape,
                    ),
            )
        }
    }
}

// ------------------------------------------------------------------- probe

/**
 * One free-input probe. Neutral acknowledgment: no right/wrong echo — the
 * stamp lands (state's `calibrationIndex` advanced), then the next probe.
 */
@Composable
private fun ProbePhase(
    state: AppState,
    probe: CalProbeUi,
    acking: Boolean,
    onAnswer: (Int?) -> Unit,
) {
    val probeIndex = Ranking.ORDER.indexOf(probe.item)
    val round = probeIndex / CAL_ROUND_SIZE + 1
    // Before the answer the persisted index equals probeIndex; right after it
    // is probeIndex+1 — the stamp lands from persisted state, monotone.
    val stamps = (minOf(state.calibrationIndex, probeIndex + 1) - (round - 1) * CAL_ROUND_SIZE)
        .coerceIn(0, CAL_ROUND_SIZE)
    var entered by remember(probe) { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Runde $round",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MulpluColors.Ink,
            )
            Text(
                text = "von $CAL_ROUNDS",
                fontSize = 14.sp,
                color = MulpluColors.Ink.copy(alpha = 0.55f),
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        StampRow(stamps = stamps)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${probe.shownA} × ${probe.shownB} = ?",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MulpluColors.Ink,
                textAlign = TextAlign.Center,
            )
        }
        // Neutral input field: never turns red or green during calibration.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(2.dp, MulpluColors.InPlayBlue, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (entered.isEmpty()) {
                Text(
                    text = "Tippe deine Antwort ein …",
                    fontSize = 16.sp,
                    color = MulpluColors.TrackGrey,
                )
            } else {
                Text(
                    text = entered,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MulpluColors.Ink,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Keypad(
            enabled = !acking,
            onDigit = { d -> if (entered.length < 3) entered += d },
            onBackspace = { entered = entered.dropLast(1) },
            onSubmit = { entered.toIntOrNull()?.let(onAnswer) },
        )
        Spacer(Modifier.height(12.dp))
        // "Weiß nicht" — the escape hatch, on every probe (mvp-spec §9).
        // Below the keypad, matching its placement on the day question screen (#27).
        Button(
            onClick = { onAnswer(null) },
            enabled = !acking,
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

/** Stamps within the round — the fine of the two monotone progress scales. */
@Composable
private fun StampRow(stamps: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(CAL_ROUND_SIZE) { k ->
            val set = k < stamps
            val scale by animateFloatAsState(
                targetValue = if (set) 1f else 0.9f,
                animationSpec = tween(180),
                label = "stamp",
            )
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .scale(scale)
                    .background(
                        if (set) MulpluColors.InPlayBlue else MulpluColors.CardGrey,
                        RoundedCornerShape(10.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (set) {
                    Text(text = "✓", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ------------------------------------------------------------------ reveal

/**
 * The reveal introduces the progress map: 36 empty tiles fill in one by one
 * (~45 ms apart, admission order), the companion line resolves, then the CTA
 * leads straight into day 1 (mvp-spec §8). Never persisted.
 */
@Composable
private fun RevealPhase(state: AppState, known: Int, onFinish: () -> Unit) {
    var revealedCount by remember { mutableIntStateOf(0) }
    var resolved by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        for (i in 1..Ranking.ORDER.size) {
            delay(45)
            revealedCount = i
        }
        delay(300)
        resolved = true
    }
    val revealed = Ranking.ORDER.take(revealedCount).toSet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))
        Companion()
        Spacer(Modifier.height(14.dp))
        Headline(
            when {
                !resolved -> "Ich baue dir deine Karte."
                known == 0 -> "Jetzt weiß ich, wo wir anfangen."
                else -> "$known kannst du schon!"
            },
        )
        Spacer(Modifier.height(16.dp))
        TriangleGrid(
            tiles = tileStates(state),
            promoted = emptySet(),
            pulsing = emptySet(),
            revealed = revealed,
        )
        Spacer(Modifier.height(12.dp))
        Legend()
        Spacer(Modifier.weight(1f))
        if (resolved) {
            Cta("Los geht's", onFinish)
        }
        Spacer(Modifier.height(16.dp))
    }
}
