package com.mulplu.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulplu.app.data.StateRepository
import com.mulplu.app.engine.Engine
import java.time.LocalDate

/** The single Activity (ADR-0003). */
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

    // Backgrounding (Home, recents, lock): the open question evaporates (mvp-spec §10).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) vm.onBackgrounded()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val appState = state ?: return
    // Calibration: while incomplete, and through its mercy/reveal phases even
    // though the last probe already flipped `calibrationComplete` (mvp-spec §8).
    val calPhase = vm.calibrationPhase
    if (!appState.calibrationComplete || calPhase is CalPhase.Mercy || calPhase is CalPhase.Reveal) {
        LaunchedEffect(Unit) { vm.ensureCalibrationPhase() }
        if (calPhase != null) {
            CalibrationScreen(
                state = appState,
                phase = calPhase,
                probe = vm.calProbe,
                acking = vm.calAcking,
                onContinue = vm::continueCalibration,
                onAnswer = vm::answerCalibrationProbe,
                onMercyOk = vm::mercyAcknowledged,
                onFinish = vm::finishCalibration,
            )
        }
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
