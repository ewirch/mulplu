# Persistence: DataStore with kotlinx.serialization, not Room

The whole state is ~2 KB (see ADR-0001) and the access pattern is one full read at app start plus one small write per answer; the only query ever needed is "everything". We persist the state as a single immutable data class in one JSON file via Jetpack DataStore with a custom kotlinx.serialization serializer. DataStore gives atomic writes (no corrupt file on process kill mid-write) and a coroutine API; rewriting the full ~2 KB JSON per answer is irrelevant at this size.

Rejected: **Room** — DAO/entity/SQL infrastructure for queries that will never exist; **hand-rolled file I/O** — saves the dependency but re-implements exactly the atomicity and concurrent-write safety DataStore already solves.

Migration story, chosen for a schema that will change repeatedly while the app is tuned: adding a field with a default is free (kotlinx.serialization defaults); breaking changes bump a `version: Int` inside the JSON and are handled by a hand-written mapping function at load time.
