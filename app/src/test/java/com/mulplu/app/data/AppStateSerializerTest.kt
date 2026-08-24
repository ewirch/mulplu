package com.mulplu.app.data

import androidx.datastore.core.CorruptionException
import com.mulplu.app.engine.AppState
import com.mulplu.app.engine.Engine
import com.mulplu.app.engine.Event
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class AppStateSerializerTest {

    @Test
    fun `serializer round-trip through streams`() = runTest {
        val today = LocalDate.of(2026, 8, 24)
        var state = AppState.initial()
        // Drive some real engine transitions so the persisted state is non-trivial.
        repeat(5) { state = Engine.reduce(state, Event.CalibrationProbeAnswered(null), today) }

        val out = ByteArrayOutputStream()
        AppStateSerializer.writeTo(state, out)
        val read = AppStateSerializer.readFrom(ByteArrayInputStream(out.toByteArray()))
        assertEquals(state, read)
    }

    @Test
    fun `corrupt bytes raise CorruptionException`() = runTest {
        assertThrows(CorruptionException::class.java) {
            kotlinx.coroutines.runBlocking {
                AppStateSerializer.readFrom(ByteArrayInputStream("not json".encodeToByteArray()))
            }
        }
    }
}
