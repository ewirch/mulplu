package com.mulplu.app.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.mulplu.app.engine.AppState
import java.io.InputStream
import java.io.OutputStream

/** DataStore serializer: whole state as one JSON document (ADR-0002). */
object AppStateSerializer : Serializer<AppState> {

    override val defaultValue: AppState = AppState.initial()

    override suspend fun readFrom(input: InputStream): AppState =
        try {
            StateJson.decode(input.readBytes().decodeToString())
        } catch (e: Exception) {
            throw CorruptionException("cannot read app state", e)
        }

    override suspend fun writeTo(t: AppState, output: OutputStream) {
        output.write(StateJson.encode(t).encodeToByteArray())
    }
}
