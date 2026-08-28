# Releasing Mulplu

Releases are manual and rare, built and uploaded by hand from this machine. There is no CI path to
a release: CI builds the debug variant only (#55), and no signing secret ever reaches GitHub
Actions.

## Released tags are immutable

A tag `v<versionName>` is **never moved and never deleted** once it exists. If a released version
turns out to be wrong, burn the number and release the next one.

The reason is that the shipped app prints `github.com/ewirch/mulplu/releases/tag/v<versionName>` on
its licence screen as the GPLv3 § 6(d) source offer, and that binary is out of our hands. A moved
tag makes the app lie about its own source — worse than a 404, because it looks plausible. A
deleted tag makes § 6(d) unmet retroactively, for an artefact that cannot be recalled.

This is enforced, not merely intended: the repository ruleset *Release tags are immutable* blocks
deletion and updates on `refs/tags/v*` with an empty bypass list, so it binds the owner too.
Creation stays open, because `releaseVersion` needs it.

GitHub serves `Source code (zip/tar.gz)` at `/releases/tag/<tag>` for a bare tag, so no GitHub
Release object is needed and none is created.

## Steps

1. **Everything to be shipped is merged into `main`**, and the working tree is clean. `releaseVersion`
   refuses otherwise, and so does the packaging guard.
2. **Tests and lint**, on the code as it stands:
   ```
   ./gradlew test lintRelease
   ```
   `lintRelease` is not in CI (#55) and is a local pre-release step.
3. **Bump, commit, tag, push** — one step:
   ```
   ./gradlew releaseVersion              # minor: 1.0 -> 1.1
   ./gradlew releaseVersion -Pbump=major # major: 1.0 -> 2.0
   ```
   This is the only command in the repo that writes history or touches the network. If the push
   fails, the commit and tag survive locally: fix the cause and rerun
   `git push --atomic origin main v<version>` by hand — do **not** rerun the task, it would bump
   again.
4. **Signing credentials** in the environment, from the password manager entry (#46):
   ```
   export MULPLU_STORE_PASSWORD=... MULPLU_KEY_PASSWORD=...
   ```
   plus `app/release.keystore` in place. Without these the packaging tasks abort by name.
5. **Build both channels.** They carry the same signature and are interchangeable (#46):
   ```
   ./gradlew bundleRelease assembleRelease
   ```
   The guard from step 3 runs here: the tag must exist, point at `HEAD`, and the tree must be clean.
6. **Test the APK on the child device** before uploading — `app/build/outputs/apk/release/`, via the
   MTP route from #44 (`adb` cannot write into the supervised user). This is what `assembleRelease`
   exists for.
7. **Upload the AAB** from `app/build/outputs/bundle/release/` to the Play Console, and check that
   the listing's website field points at the new tag.

## One-time step before the first Play rollout

`v1.0` (versionCode 2) is tagged retroactively: it is the build that was sideloaded onto the child
device, and it predates this procedure, so its tag does not sit on a `releaseVersion` commit. Nothing
to do about it — the next release runs the procedure above from step 1 and lands on `1.1`.
