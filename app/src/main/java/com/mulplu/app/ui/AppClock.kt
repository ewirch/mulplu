package com.mulplu.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

    fun today(): LocalDate = LocalDate.now().plusDays(dayOffset)
}
