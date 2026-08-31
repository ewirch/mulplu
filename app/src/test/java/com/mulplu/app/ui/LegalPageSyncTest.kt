package com.mulplu.app.ui

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The hosted privacy page and the in-app legal screen must be word-for-word
 * identical (#58). Both read the same `res/raw` files for the prose, so the only
 * place they can drift is `docs/privacy/index.html`, which is hand-written HTML.
 * This test is what makes that drift loud instead of silent — the page is public
 * and Play-facing, and nobody re-reads it.
 *
 * Compared paragraph by paragraph rather than as one string, so a failure names
 * the one paragraph that moved instead of diffing two kilobytes.
 */
class LegalPageSyncTest {

    private val page = repoFile("docs/privacy/index.html").readText().withoutComments()

    @Test
    fun `hosted page carries exactly the legal text the app renders`() {
        val expected =
            listOf(HEADING_IMPRESSUM) + rawParagraphs("impressum") +
                listOf(HEADING_PRIVACY) + rawParagraphs("privacy")
        val actual = page.blockTexts()

        // Index by index before comparing sizes: this is what keeps a failure
        // down to the one block that moved. A deleted paragraph shifts the rest,
        // so the first mismatch lands exactly on the deletion.
        for (i in 0 until minOf(expected.size, actual.size)) {
            assertEquals("text block $i", expected[i], actual[i])
        }
        assertEquals("number of text blocks", expected.size, actual.size)
    }

    @Test
    fun `hosted page uses no HTML entities`() {
        // The comparison strips tags but does not decode entities, so an entity
        // would read as literal `&uuml;` and fail confusingly. UTF-8 carries the
        // umlauts and dashes directly, so there is never a reason for one.
        val entities = ENTITY.findAll(page).map { it.value }.toList()
        assertTrue("entities in docs/privacy/index.html: $entities", entities.isEmpty())
    }

    private fun rawParagraphs(name: String): List<String> =
        repoFile("app/src/main/res/raw/$name.txt")
            .readText()
            .trim()
            .split(BLANK_LINE)
            .map { it.collapseWhitespace() }

    /** The `<h2>` and `<p>` texts, in document order. */
    private fun String.blockTexts(): List<String> =
        BLOCK.findAll(this)
            .map { it.groupValues[2].replace(TAG, " ").collapseWhitespace() }
            .toList()

    private fun String.withoutComments(): String = replace(COMMENT, "")

    private fun String.collapseWhitespace(): String = replace(WHITESPACE, " ").trim()

    private companion object {
        val COMMENT = Regex("<!--.*?-->", RegexOption.DOT_MATCHES_ALL)
        val BLOCK = Regex("<(h2|p)>(.*?)</\\1>", RegexOption.DOT_MATCHES_ALL)
        val TAG = Regex("<[^>]+>")
        val WHITESPACE = Regex("\\s+")
        val ENTITY = Regex("&[a-zA-Z#][a-zA-Z0-9]*;")
        val BLANK_LINE = Regex("\\n\\s*\\n")

        /**
         * Resolved from the repo root rather than the working directory: Gradle
         * runs unit tests in `app/`, an IDE often in the repo root, and this test
         * reads files on both sides of that boundary.
         */
        fun repoFile(path: String): File {
            var dir: File? = File("").absoluteFile
            while (dir != null && !File(dir, "settings.gradle.kts").isFile) {
                dir = dir.parentFile
            }
            val root = requireNotNull(dir) { "repo root not found above ${File("").absolutePath}" }
            return File(root, path).also {
                require(it.isFile) { "missing: $it" }
            }
        }
    }
}
