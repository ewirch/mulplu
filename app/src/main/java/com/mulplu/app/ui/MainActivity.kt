package com.mulplu.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulplu.app.data.StateRepository
import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.Engine
import java.time.LocalDate

/**
 * The single Activity (ADR-0003). The question screen (#20) and the progress
 * map (#21) are real; the calibration composable here is a placeholder until
 * #22 lands.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = StateRepository(this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val vm: AppViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                AppViewModel(repository) as T
                        },
                    )
                    App(vm)
                }
            }
        }
    }
}

@Composable
private fun App(vm: AppViewModel) {
    val state by vm.appState.collectAsState()
    val context = LocalContext.current
    val soundPlayer = remember { SoundPlayer(context) }
    DisposableEffect(Unit) { onDispose { soundPlayer.release() } }
    LaunchedEffect(vm.soundEvent) { vm.soundEvent?.let { (_, sound) -> soundPlayer.play(sound) } }

    val appState = state ?: return
    if (!appState.calibrationComplete) {
        CalibrationScaffold(vm, appState)
        return
    }
    when (vm.screen) {
        Screen.Question -> {
            // System back → map, no confirmation (mvp-spec §10); the open
            // question evaporates.
            BackHandler { vm.backToMap() }
            vm.questionUi?.let { ui ->
                QuestionScreen(ui = ui, feedback = vm.feedback, onAnswer = vm::answer)
            }
        }
        else -> {
            val today = LocalDate.now()
            MapScreen(
                state = appState,
                dayDone = Engine.openToday(appState, today).isEmpty(),
                today = today,
                dayCloseTick = vm.dayCloseTick,
                terminalTick = vm.terminalTick,
                onStart = vm::startPractice,
                onPlayDayClose = soundPlayer::playDayClose,
            )
        }
    }
}

/**
 * Temporary calibration scaffold (replaced by #22): drives real
 * `CalibrationProbeAnswered` events so the practice loop can be reached.
 */
@Composable
private fun CalibrationScaffold(vm: AppViewModel, state: AppState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Kalibrierung: ${state.calibrationIndex} / 36")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { vm.answerCalibrationProbe(correct = true) }) {
                Text("Richtig")
            }
            Button(onClick = { vm.answerCalibrationProbe(correct = false) }) {
                Text("Weiß nicht")
            }
        }
    }
}
