# Architecture: single module, pure engine, no navigation library

Single Gradle module (`:app`), separated by package discipline: `engine` (pure Kotlin), `data` (DataStore serializer, ADR-0002), `ui` (Compose). A separate pure-JVM engine module would enforce the boundary by compiler but adds Gradle wiring a single-user app with three screens does not earn.

The adaptive engine is **pure functions `(State, Event, LocalDate) -> State`** with zero Android imports. The current date and all randomness (item selection, distractor drawing) are injected by the caller — the engine never reads a clock or a `Random` of its own — so every rule from #2/#5/#7 is a plain JVM unit test.

UI: one Activity, one `AppViewModel` holding `StateFlow<AppState>` fed from DataStore; it receives events, calls the engine, persists the result. The current screen is a sealed class with exactly three cases (progress map, question, calibration — fixed by #10); **no Navigation-Compose**, because "three screens, no backstack, single re-entry point" gets no value from routes and deep-link machinery. The calibration phases from #14 (intro, probe, breather, mercy stop, reveal) are a sub-state of the calibration screen case, not navigation destinations; the reveal is never persisted — a restart mid-reveal lands on the resume rule.
