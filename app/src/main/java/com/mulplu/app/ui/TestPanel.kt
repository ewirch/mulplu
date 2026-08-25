package com.mulplu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mulplu.app.BuildConfig

/**
 * The manual-testing panel (#30): a corner handle over whatever screen is up,
 * opening the shortcuts that would otherwise take days of real use to reach.
 *
 * Gated on `BuildConfig.TEST_HOOKS` — true in debug, false in release, so the
 * handle does not exist in a build that reaches a child. Labels are English on
 * purpose: this is developer surface, not part of the app's German UI.
 */
@Composable
fun TestPanel(vm: AppViewModel, onQuestionScreen: Boolean) {
    if (!BuildConfig.TEST_HOOKS) return
    var open by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.TopEnd,
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(34.dp)
                .background(MulpluColors.Ink.copy(alpha = 0.28f), CircleShape)
                .clickable { open = true },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "🧪", fontSize = 16.sp)
        }
    }

    if (open) {
        TestPanelDialog(
            vm = vm,
            onQuestionScreen = onQuestionScreen,
            onDismiss = { open = false },
        )
    }
}

@Composable
private fun TestPanelDialog(vm: AppViewModel, onQuestionScreen: Boolean, onDismiss: () -> Unit) {
    fun act(action: () -> Unit) {
        action()
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Test panel",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MulpluColors.Ink,
                )
                Text(
                    text = "Day offset: +${AppClock.dayOffset} (${AppClock.today()})",
                    fontSize = 13.sp,
                    color = MulpluColors.Ink,
                )
                PanelButton("Next day") { act(vm::testAdvanceDay) }
                PanelButton("All items to level 5") { act(vm::testConsolidateAll) }
                PanelButton("Reset levels, refill pool") { act(vm::testResetLevels) }
                PanelButton("Wipe all data (recalibrate)") { act(vm::testResetAll) }

                if (onQuestionScreen) {
                    Text(
                        text = "Level of the item on screen",
                        fontSize = 13.sp,
                        color = MulpluColors.Ink,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (level in 1..5) {
                            Button(
                                onClick = { act { vm.testSetLevel(level) } },
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MulpluColors.InPlayBlue,
                                ),
                            ) {
                                Text("$level", fontSize = 15.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MulpluColors.AccentBlue),
    ) {
        Text(text = label, fontSize = 15.sp, color = Color.White)
    }
}
