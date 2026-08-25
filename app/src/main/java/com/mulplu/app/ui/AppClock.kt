package com.mulplu.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.LocalDate

/**
 * The app's notion of "today" — the one place the UI layer reads the date
 * (the engine stays clockless, ADR-0003).
 *
 * [dayOffset] is zero in production and only ever moved by the test panel
 * (#30), so a manual tester can cross a day boundary without touching the
 * system clock. It is in-memory: a restart is back on the real date.
 */
object AppClock {
    var dayOffset by mutableStateOf(0L)
        internal set

    /** Bumped on every return to the foreground; observed by [todayAsState]. */
    private var foregroundTick by mutableStateOf(0L)

    fun today(): LocalDate = LocalDate.now().plusDays(dayOffset)

    /**
     * [today] for composition. `LocalDate.now()` is not observable, so a screen
     * that read the date would keep showing the day the app was backgrounded on
     * (#31); re-reading on every return to the foreground fixes that.
     */
    @Composable
    fun todayAsState(): LocalDate = remember(foregroundTick, dayOffset) { today() }

    /** The date may have changed while the app was away. */
    fun onForeground() {
        foregroundTick += 1
    }
}
