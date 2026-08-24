package com.mulplu.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mulplu.app.data.StateRepository
import com.mulplu.app.engine.Engine
import com.mulplu.app.engine.Event
import java.time.LocalDate
import kotlinx.coroutines.launch

/**
 * Temporary scaffolding until the real screens land (#20, #21, #22): shows the
 * persisted calibration progress and lets a tap run one calibration probe
 * through the engine, so persistence is exercised end to end.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = StateRepository(this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val state by repository.state.collectAsState(initial = null)
                    val scope = rememberCoroutineScope()
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "Mulplu")
                        Text(text = "Kalibrierung: ${state?.calibrationIndex ?: "…"} / 36")
                        Button(onClick = {
                            scope.launch {
                                repository.update {
                                    if (it.calibrationComplete) return@update it
                                    Engine.reduce(
                                        it,
                                        Event.CalibrationProbeAnswered(null),
                                        LocalDate.now(),
                                    )
                                }
                            }
                        }) {
                            Text(text = "Weiß nicht")
                        }
                    }
                }
            }
        }
    }
}
