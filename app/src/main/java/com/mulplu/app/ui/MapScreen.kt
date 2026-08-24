package com.mulplu.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.ItemKey
import java.time.LocalDate
import kotlinx.coroutines.delay

/** Map-only colours (prototype `prototype/progress-map.html`, mvp-spec §10). */
private object MapColors {
    val ArrowOrange = Color(0xFFF5A623)
    val GhostBorder = Color(0xFFCFDBE6)
    val Muted = Color(0xFF5B6B7C)
    val RosetteLight = Color(0xFFFFE9A8)
    val RosetteDark = Color(0xFFE2B23C)
    val TerminalScrim = Color(0xF2FFFDF6)
}

private val FACTORS = (2..9).toList()

/**
 * The progress map — the home screen (mvp-spec §10). Triangle with axes 2–9,
 * 36 tiles, ghost cells in the upper half, legend below; start button while
 * the day is open, "Für heute fertig" once satisfied; golden rosette in the
 * header from completion on. Day close and terminal event render as overlays
 * driven by the view model's one-shot ticks.
 */
@Composable
fun MapScreen(
    state: AppState,
    dayDone: Boolean,
    today: LocalDate,
    dayCloseTick: Long,
    terminalTick: Long,
    onStart: () -> Unit,
    onPlayDayClose: () -> Unit,
) {
    val tiles = tileStates(state)
    val promoted = promotedToday(state, today).toSet()

    // Day close: the map animates the day's movements, one pulse per moved
    // tile, with the day-close sound. A day with zero movements is a quiet map.
    var pulsing by remember { mutableStateOf<ItemKey?>(null) }
    LaunchedEffect(dayCloseTick) {
        if (dayCloseTick == 0L) return@LaunchedEffect
        onPlayDayClose()
        for (item in promotedToday(state, today)) {
            pulsing = item
            delay(280)
            pulsing = null
            delay(130)
        }
    }

    // Terminal event: the last tile animates, sparks fly, a full-area moment
    // fades out. No acknowledge button.
    var terminalPhase by remember { mutableStateOf(TerminalPhase.Off) }
    LaunchedEffect(terminalTick) {
        if (terminalTick == 0L) return@LaunchedEffect
        val last = promotedToday(state, today).lastOrNull()
        if (last != null) {
            pulsing = last
            delay(420)
            pulsing = null
        }
        terminalPhase = TerminalPhase.On
        delay(3600)
        terminalPhase = TerminalPhase.FadingOut
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MapHeader(rosette = state.wasEverCompleted)
            Spacer(Modifier.height(12.dp))
            TriangleGrid(tiles = tiles, promoted = promoted, pulsing = pulsing)
            Spacer(Modifier.height(12.dp))
            Legend()
            Spacer(Modifier.weight(1f))
            if (dayDone) {
                Text(
                    text = "Für heute fertig",
                    fontSize = 18.sp,
                    color = MapColors.Muted,
                    modifier = Modifier.padding(bottom = 28.dp),
                )
            } else {
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MulpluColors.AccentBlue),
                ) {
                    Text(text = "Los geht's", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
                Spacer(Modifier.height(20.dp))
            }
        }
        TerminalOverlay(phase = terminalPhase)
    }
}

@Composable
private fun MapHeader(rosette: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (rosette) Rosette()
        Text(
            text = "Einmaleins",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MapColors.Muted,
        )
    }
}

/** The permanent mark: small, golden, non-numeric, dateless (mvp-spec §10). */
@Composable
private fun Rosette() {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(MapColors.RosetteLight, MapColors.RosetteDark),
                    radius = 40f,
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "★", fontSize = 14.sp, color = Color.White)
    }
}

/** Triangle with axes 2–9; upper half dashed ghost cells (mvp-spec §10). */
@Composable
private fun TriangleGrid(
    tiles: Map<ItemKey, TileState>,
    promoted: Set<ItemKey>,
    pulsing: ItemKey?,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val gap = 5.dp
        val tileSize = ((maxWidth - gap * (FACTORS.size)) / (FACTORS.size + 1)).coerceAtMost(42.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // axis header row
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                AxisCell("", tileSize)
                for (c in FACTORS) AxisCell("$c", tileSize)
            }
            for (r in FACTORS) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    AxisCell("$r", tileSize)
                    for (c in FACTORS) {
                        if (c > r) {
                            GhostCell(tileSize)
                        } else {
                            val item = ItemKey.of(r, c)
                            Tile(
                                state = tiles.getValue(item),
                                promoted = item in promoted,
                                pulsing = item == pulsing,
                                size = tileSize,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AxisCell(text: String, size: Dp) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MapColors.Muted)
    }
}

@Composable
private fun GhostCell(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .drawBehind {
                drawRoundRect(
                    color = MapColors.GhostBorder,
                    cornerRadius = CornerRadius(9.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(6f, 6f),
                        ),
                    ),
                )
            },
    )
}

@Composable
private fun Tile(state: TileState, promoted: Boolean, pulsing: Boolean, size: Dp) {
    val colour = when (state) {
        TileState.Consolidated -> MulpluColors.CorrectGreen
        TileState.InPlay -> MulpluColors.InPlayBlue
        TileState.NotStarted -> MulpluColors.TrackGrey
    }
    val pulseScale by animateFloatAsState(
        targetValue = if (pulsing) 1.18f else 1f,
        animationSpec = tween(200),
        label = "tilePulse",
    )
    Box(modifier = Modifier.size(size).scale(pulseScale)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colour, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (state == TileState.Consolidated) {
                Text(text = "✓", fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        // Promoted today: solid orange block arrow with white outline, on the
        // tile's top-right corner, overhanging the edge (mvp-spec §10).
        if (promoted) {
            PromotedArrow(
                modifier = Modifier
                    .size(size * 0.57f)
                    .align(Alignment.TopEnd)
                    .offset(x = size * 0.19f, y = -size * 0.24f),
            )
        }
    }
}

@Composable
private fun PromotedArrow(modifier: Modifier) {
    Box(
        modifier = modifier.drawBehind {
            // Block arrow on a 24×24 viewbox: M12 2 L22 13 H16.5 V22 H7.5 V13 H2 Z
            val path = Path().apply {
                moveTo(12f, 2f)
                lineTo(22f, 13f)
                lineTo(16.5f, 13f)
                lineTo(16.5f, 22f)
                lineTo(7.5f, 22f)
                lineTo(7.5f, 13f)
                lineTo(2f, 13f)
                close()
            }
            scale(scaleX = size.width / 24f, scaleY = size.height / 24f, pivot = androidx.compose.ui.geometry.Offset.Zero) {
                drawPath(path, color = Color.White, style = Stroke(width = 4f))
                drawPath(path, color = MapColors.ArrowOrange)
            }
        },
    )
}

@Composable
private fun Legend() {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        LegendEntry(MulpluColors.CorrectGreen, "kannst du")
        LegendEntry(MulpluColors.InPlayBlue, "üben wir")
        LegendEntry(MulpluColors.TrackGrey, "später")
    }
}

@Composable
private fun LegendEntry(colour: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(modifier = Modifier.size(10.dp).background(colour, RoundedCornerShape(3.dp)))
        Text(text = label, fontSize = 12.sp, color = MapColors.Muted)
    }
}

private enum class TerminalPhase { Off, On, FadingOut }

/** The one place the app goes big — one-time, fades out by itself. */
@Composable
private fun TerminalOverlay(phase: TerminalPhase) {
    AnimatedVisibility(
        visible = phase == TerminalPhase.On,
        enter = fadeIn(tween(500)) + scaleIn(tween(500), initialScale = 0.9f),
        exit = fadeOut(tween(900)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MapColors.TerminalScrim),
            contentAlignment = Alignment.Center,
        ) {
            Sparks(color = MapColors.RosetteDark)
            Sparks(color = MulpluColors.AccentBlue)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🏆", fontSize = 72.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Alle 36 geschafft!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MulpluColors.Ink,
                )
            }
        }
    }
}
