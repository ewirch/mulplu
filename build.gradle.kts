plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Bump the version, commit, tag and push, in that order and as one step (#59).
// The app itself prints github.com/ewirch/mulplu/releases/tag/v<versionName> to
// satisfy GPLv3 § 6(d), so the tag has to exist on the remote before a rollout
// or the shipped binary points at a 404.
//
// Invoked by hand before a release (docs/release.md), never as part of a build:
// this is the only place in the repo that touches the network or writes history,
// and keeping it out of the task graph is what lets `packageRelease` stay
// offline and merely *verify*.
//
//   ./gradlew releaseVersion              # 1.0 -> 1.1
//   ./gradlew releaseVersion -Pbump=major # 1.0 -> 2.0
tasks.register("releaseVersion") {
    group = "release"
    description = "Bump version.properties, commit, tag v<versionName> and push."

    // Captured as plain values: the task action must not reach back into the
    // build script, or the configuration cache cannot serialize it.
    val repoDir = rootDir
    val bump = providers.gradleProperty("bump").getOrElse("minor")

    doLast {
        fun run(vararg args: String): Pair<Int, String> {
            val process = ProcessBuilder(listOf("git", *args))
                .directory(repoDir)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            return process.waitFor() to output
        }

        fun git(vararg args: String): String {
            val (code, output) = run(*args)
            check(code == 0) { "git ${args.joinToString(" ")} failed:\n$output" }
            return output
        }

        check(bump == "minor" || bump == "major") {
            "Unknown -Pbump=$bump, expected 'minor' or 'major'."
        }

        val versionFile = File(repoDir, "version.properties")
        val lines = versionFile.readLines()
        fun property(key: String) = lines.first { it.startsWith("$key=") }.substringAfter('=')

        val newCode = property("versionCode").toInt() + 1
        val (major, minor) = property("versionName").split(".").map(String::toInt)
        val newName = if (bump == "major") "${major + 1}.0" else "$major.${minor + 1}"
        val tag = "v$newName"

        // Refuse before writing anything. Each of these would otherwise produce
        // a tag that does not describe what ships.
        check(git("status", "--porcelain").isEmpty()) {
            "Working tree is not clean — the release commit would pick up unrelated changes."
        }
        check(git("rev-parse", "--abbrev-ref", "HEAD") == "main") {
            "Releases are tagged on main only; HEAD is on " +
                "${git("rev-parse", "--abbrev-ref", "HEAD")}."
        }
        git("fetch", "--quiet", "origin", "main")
        check(git("rev-parse", "HEAD") == git("rev-parse", "origin/main")) {
            "HEAD and origin/main have diverged — pull or push first."
        }
        check(run("rev-parse", "-q", "--verify", "refs/tags/$tag").first != 0) {
            "Tag $tag already exists locally. Released tags are immutable (#59)."
        }
        check(git("ls-remote", "--tags", "origin", "refs/tags/$tag").isEmpty()) {
            "Tag $tag already exists on origin. Released tags are immutable (#59)."
        }

        versionFile.writeText(
            lines.joinToString("\n") { line ->
                when {
                    line.startsWith("versionCode=") -> "versionCode=$newCode"
                    line.startsWith("versionName=") -> "versionName=$newName"
                    else -> line
                }
            } + "\n",
        )

        git("add", "version.properties")
        git("commit", "--message", "release: $newName (versionCode $newCode)")
        git("tag", "--annotate", tag, "--message", "Mulplu $newName (versionCode $newCode)")
        // Atomic: either main and the tag both land on origin, or neither does.
        // If this fails, the commit and tag survive locally — fix the cause and
        // rerun the push by hand, do not rerun the task.
        git("push", "--atomic", "origin", "main", tag)

        logger.lifecycle("Released $newName (versionCode $newCode), tag $tag pushed to origin.")
    }
}
