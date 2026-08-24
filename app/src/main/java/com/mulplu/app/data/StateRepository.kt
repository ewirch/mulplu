package com.mulplu.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.mulplu.app.engine.AppState
import kotlinx.coroutines.flow.Flow

private val Context.appStateStore: DataStore<AppState> by dataStore(
    fileName = "app_state.json",
    serializer = AppStateSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler { AppState.initial() },
)

/**
 * Single access point to the persisted [AppState] (ADR-0002). Call [update]
 * with the engine reducer after every answer; DataStore serializes writers and
 * writes atomically.
 */
class StateRepository(context: Context) {

    private val store = context.applicationContext.appStateStore

    val state: Flow<AppState> = store.data

    suspend fun update(transform: (AppState) -> AppState): AppState = store.updateData(transform)
}
