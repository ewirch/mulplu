package com.mulplu.app.engine

/**
 * The frozen admission order (mvp-spec §5). Frozen as a list; the generating
 * formula (product ascending, ×0.7 for squares and the ×9 family) is provenance
 * only — to change a rank, edit the list.
 */
object Ranking {
    val ORDER: List<ItemKey> = listOf(
        2 to 2, 2 to 3, 3 to 3, 2 to 4, 2 to 5, 4 to 4, 2 to 6, 3 to 4, 2 to 9, 2 to 7,
        3 to 5, 2 to 8, 5 to 5, 3 to 6, 3 to 9, 4 to 5, 3 to 7, 3 to 8, 4 to 6, 4 to 9,
        6 to 6, 4 to 7, 5 to 6, 5 to 9, 4 to 8, 7 to 7, 5 to 7, 6 to 9, 9 to 9, 5 to 8,
        6 to 7, 7 to 9, 8 to 8, 6 to 8, 8 to 9, 7 to 8,
    ).map { (a, b) -> ItemKey(a, b) }

    /** Learning-front width (mvp-spec §6). Validated by simulation — do not re-tune. */
    const val FRONT_WIDTH = 10
}
