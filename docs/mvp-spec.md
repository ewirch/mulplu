# MVP specification: mulplu — times-tables trainer for Android

This document is the handoff spec for v1. Together with [`CONTEXT.md`](../CONTEXT.md)
(the settled vocabulary — read it first; terms used here without definition are defined
there) and the ADRs under [`docs/adr/`](adr/), a fresh session can build v1 from these
files alone. Decision history lives in the wayfinder map
([#1](https://github.com/ewirch/mulplu/issues/1)) and its closed tickets; nothing below
requires reading them.

## 1. Product in one paragraph

A local, offline Android app that trains **one** 9–11 year old child on the **36
multiplication items** (unordered factor pairs from 2–9) until every answer can be
produced by **free input, without answer choices**. Difficulty is carried by an
**answer-choice ladder** (2 → 3 → 4 buttons → free input) and by practice **distributed
across days** — nothing is timed, and correctness is the only signal recorded. The app
is child-facing only, in German, with no accounts, no backend and no settings.

## 2. Fixed constraints

- **Platform:** native Android, Kotlin + Jetpack Compose, portrait.
- **Local only:** no backend, no account, no sync, works offline.
- **Single user**, no profile switching.
- **App language: German.** Code, docs and tracker: English.
- **Nothing is timed.** No countdown, no latency measurement, no personal bests, no
  time pressure of any kind.
- **Nothing accumulates across days** except per-item state: no points, XP, badges,
  currency, collection, streaks, or record of practised days.
- **Zero settings in v1.** Sound is always on; volume is the device's job.

## 3. Task space

Exactly the **36 unordered factor pairs from 2–9**. `3×4` and `4×3` are one item with
one state; which order is shown is a 50/50 draw per presentation. Factors 1 and 10 are
excluded as trivial. No division, no missing-factor tasks.

## 4. Per-item state: the level ladder

The **level** (1–5) is the only per-item score. It is both the difficulty setting and
the measure of mastery.

| Level | Presentation |
|---|---|
| 1 | 2 answer buttons |
| 2 | 3 answer buttons |
| 3 | 4 answer buttons |
| 4 | free input |
| 5 | **consolidated** — free input; the item has been produced correctly at least once |

Movement is made **only by the counting answer** — the first answer to an item on a
local calendar day:

- correct → level + 1 (cap 5)
- wrong → level − 1 (floor 1)

Every later answer to that item the same day is a **practice answer** and moves
nothing. There is no counter, no decay timer, no other rule: forgetting is *measured*
(a consolidated item keeps being asked; a genuine loss shows up as a wrong counting
answer) rather than estimated. Minimum time from level 1 to consolidated: **4 days**.

A wrong counting answer on a level-5 item drops it to 4 and **revokes** consolidation;
it is re-earned the same way. `hasEverConsolidated` is monotone and never reset.

## 5. Admission order (frozen constant)

The fixed ranking that decides the order in which never-consolidated items enter play.
Frozen **as a list in code**; the generating formula (product ascending, ×0.7 for
squares and the ×9 family) stays only as a provenance comment — to change a rank, edit
the list.

```
 1 2×2   2 2×3   3 3×3   4 2×4   5 2×5   6 4×4   7 2×6   8 3×4   9 2×9  10 2×7
11 3×5  12 2×8  13 5×5  14 3×6  15 3×9  16 4×5  17 3×7  18 3×8  19 4×6  20 4×9
21 6×6  22 4×7  23 5×6  24 5×9  25 4×8  26 7×7  27 5×7  28 6×9  29 9×9  30 5×8
31 6×7  32 7×9  33 8×8  34 6×8  35 8×9  36 7×8
```

## 6. Question pool and learning front

```
pool = { every item with hasEverConsolidated }
     ∪ { the first 10 never-consolidated items of the admission order }
```

The 10-wide window is the **learning front**. Pool membership is **derived, never
stored**. The pool never shrinks (monotone `hasEverConsolidated`); when fewer than 10
never-consolidated items remain, the window shrinks; the pool caps at 36. A revoked
item stays in the pool via the ever-consolidated set and occupies **no** window slot —
an item oscillating 4 ↔ 5 cannot admit new material.

First-ever consolidation of an item slides the window: the next never-consolidated
item in admission order enters the pool. The newly admitted item gets
`satisfiedOn = today`, so it first becomes due **tomorrow** — the day goal never
recedes while the child works.

## 7. The day: goal, selection, answers

**There is no session — the day is the only unit** (ADR-0004). A go has no length, no
item count, no clock, no stop-at-first-mistake, and nothing is resumable: state is per
item plus a date. Leaving is free at any time; an unanswered question evaporates.

**Day goal:** the day is done when every pool item has been answered **correctly** at
least once today. A wrong answer does not retire an item; it returns until it sticks —
and since the wrong counting answer *lowered* its level, the retry has fewer buttons.
Reaching the day goal is a **hard stop**: the app asks nothing further that day. There
is exactly one mode — no free practice, no challenge mode.

The current date is read **each time a question is presented**, and nowhere else. A
midnight crossing mid-play applies at once. The clock-farming hole (23:55 / 00:05 buys
two level-ups; setting the device clock forward) is **named and deliberately
undefended** — unpreventable in a local single-user app, and its only victim is the
child's own learning.

### Selection

```
nextQuestion(lastShown):
  openToday = [ i in pool : i.satisfiedOn != today ]
  if openToday is empty: return DAY_GOAL_REACHED
  cand  = |openToday| > 1 ? openToday \ {lastShown} : openToday
  i     = uniformRandom(cand)
  (a,b) = i.factors in random order          # orientation is presentation, not state
  level 1 → 2 buttons | 2 → 3 | 3 → 4 | 4,5 → free input
```

Never the same item twice in a row unless it is the only one open.

### Answer handling

```
onAnswer(i, given):                          # "Weiß nicht" is handled as given = wrong
  isCounting = (i.lastCountedOn < today)
  correct    = (given == product(i))

  if isCounting:
    i.lastCountedOn = today
    i.level = correct ? min(5, i.level+1) : max(1, i.level-1)
    if i.level == 5 and not i.hasEverConsolidated:
      i.hasEverConsolidated = true           # idempotent — admits exactly once, ever
      RANKING.nextNeverConsolidated().satisfiedOn = today   # due tomorrow
    if i.level > previous level: i.lastPromotedOn = today   # map overlay, §10

  if correct: i.satisfiedOn = today
  else:       rejectedToday[i] += given      # in-memory only, distractor exclusion
```

### Distractors (levels 1–3)

Distractor quality is a **precondition of the guessing guard**, not a design axis: the
only guard against guessing is the number of options (pure guessing carries an item to
free input at ≈4 %, where it collapses), and that holds only if no option is eliminable
by anything but knowing the answer.

```
buildChoices(a, b, n):
  correct = a*b
  cand = { a*(b±1), a*(b±2), (a±1)*b, (a±2)*b }   # factors clamped to 2..9
  cand -= { correct } ∪ rejectedToday[item]
  if |cand| < n-1: cand ∪= nearest products of the 36-item table (same exclusions)
  return shuffle({correct} ∪ weightedSample(cand, n-1, weight ~ 1/|v-correct|))
```

Rules: **re-drawn on every presentation** (never fixed per item); **no difficulty
tiering** across levels (the level governs the *number* of options, never their
hardness); **no non-products** (e.g. `6+7=13` — eliminable by magnitude alone);
correct-button position shuffled.

## 8. Calibration and first-run onboarding

One-time first-run pass: **all 36 items probed once by free input, in admission order,
with neutral feedback** (no right/wrong shown). Correct → seeded **level 5** with
`hasEverConsolidated = true`; wrong → **level 1**. Nothing in between — the ladder
sorts fluent from shaky in normal practice. Each probe is the item's **day-1 counting
answer** (`lastCountedOn = today`; correct also sets `satisfiedOn = today`), so a fully
fluent child ends the ~3–4 minute first run with the day goal reached — and, at 36/36,
the terminal event fires then (a true statement is not suppressed to protect a
ceremony).

- **Presentation: 6 rounds of 6 probes** with a breather screen between rounds (round
  done, dots filled, "Weiter?"). A **companion figure** speaks the framing ("Hallo! Ich
  bin dabei", "6 Runden mit 6 Aufgaben, dauert nur ein paar Minuten"; explicitly *not*
  a test, said once), the mercy stop, and the resume greeting (by **round**, not
  count: "Wir waren bei Runde 3 von 6").
- **Neutral acknowledgment:** each probe lands a **stamp** in the current round's row —
  it marks *that* a probe happened, never how it went. Progress is monotone at two
  scales: stamps within the round, dots across rounds. No 36-wide bar, no map during
  the pass.
- **Mercy stop:** after **6 consecutive misses** ("Weiß nicht" counts) calibration ends;
  unprobed items are seeded level 1. Framed as the companion's decision ("Wir hören
  hier auf. Du musst nicht alles vorher zeigen. Ab jetzt helfe ich dir bei jeder
  Aufgabe."). The streak counter is **persisted** (an app close must not reset it).
- **Interruption:** each probe persists immediately; relaunch resumes at the first
  unprobed item (`calibrationIndex` — probe order is the admission order).
- **The reveal introduces the progress map:** 36 empty tiles fill in one by one
  (~45 ms apart), the companion line resolves to "**n** kannst du schon!" (holds at
  zero: "Jetzt weiß ich, wo wir anfangen"), then a CTA leads **straight into day 1** —
  no separate hand-over screen. The reveal is never persisted; a restart mid-reveal
  lands on the resume rule.

Calibration runs once, ever. It may seed levels; it may **not** reorder the admission
order or pull items into the pool out of order.

## 9. Answer interaction (question screen)

Portrait: task at the top, answers at the bottom.

- **Levels 1–3:** big rounded card buttons; the grid **reflows to the option count**
  (2 → one row, 3 → one row, 4 → 2×2). No fixed slots.
- **Levels 4–5 and calibration:** bordered **text field** with placeholder ("Tippe
  deine Antwort ein …") and blinking cursor + flat round-key numeric keypad (1–9, 0,
  ⌫, ✓). Submission **explicit via ✓** — no auto-submit.
- **"Weiß nicht":** on **every** question — all calibration probes and every practice
  question, levels 1–5. Solid button in the map's in-play blue `#5BA3D9`, same label
  everywhere, fixed position below the answer area (does not move when the answer area
  reflows), set apart so it never reads as an extra option. Mechanically a **miss**
  (counting answer → −1 / revocation; in calibration → level 1 + mercy-stop count).
  Feedback = miss feedback **without the error beat**: no red flash, no fading of a
  picked option — only the answer reveal, same timing.
- **Feedback, correct:** pressed button (or field) turns green, spark particles, short
  rising chime; auto-advance after ~550 ms. Overall tempo: fast.
- **Feedback, wrong:** multiple choice — all wrong options fade out *including the
  picked one*, then the correct button grows (~1.45×) in its normal colour. Free input —
  field flashes red, then shows the correct answer in green. Auto-advance after
  ~2.6 s; no tap-to-continue, **no hint text**.
- **Feedback, calibration probe:** neutral — the stamp lands, next probe. No
  right/wrong echo.
- **Sound:** on, always (no toggle — v1 has zero settings). Low soft tone for wrong,
  rising chime for correct. **No haptics.**
- **Header:** a **digit-free, monotone progress bar** for the day goal — it counts
  *satisfied items*, so a miss slows it but never moves it back. The bar exists only
  here, never on the map.

Primary sources: prototypes on branches `prototype/answer-input` and
`prototype/calibration-onboarding`.

## 10. Screens and navigation

**Exactly three screens.** No start screen, no session summary, no settings screen, no
terminal screen.

1. **Progress map** — the home screen. Two states: day open → map + start button (CTA
   in accent blue `#3E6FE0`); day satisfied → map + "Für heute fertig" (after
   completion of all 36: no start button ever again).
2. **Question screen** — §9.
3. **Calibration** — first run only; its phases (intro, probe, breather, mercy stop,
   reveal) are sub-state of this screen, not navigation destinations.

**Navigation:** app start lands on calibration (if incomplete, at the first unprobed
item), otherwise **always on the map** — never straight into a question. System back on
the question screen → map, **no confirmation dialog** (leaving is free); back on the
map or in calibration exits the app. Backgrounding → return lands on the map; an open
question evaporates (state is written per answer). One single re-entry point. No
Navigation-Compose (ADR-0003).

### Progress map

- **Layout: triangle with axes 2–9** — 36 tiles below and on the diagonal, one tile per
  item; the upper half shows dashed **ghost cells** (keeps the school coordinate frame
  without breaking tile = item).
- **Tile states** (derived from pool membership; the **level is deliberately
  invisible** — "in play" spans levels 1–4 undifferentiated):

  | State | Meaning | Colour |
  |---|---|---|
  | Consolidated | level 5 | green `#2FA866`, with check |
  | In play | in the pool, not consolidated (learning front ∪ revoked) | blue `#5BA3D9` |
  | Not started | not in the pool | grey `#D5E0EB` |

  Legend below the map. Blue, not a warm colour, on purpose: at cold start 10 of 36
  tiles are in play. A revoked item is indistinguishable from one never mastered — it
  reads as *being worked on*.
- **Day overlay "promoted today":** solid orange `#F5A623` block arrow with white
  outline on the tile's top-right corner, overhanging the edge. Marks **any level-up
  today** (resets at midnight; driven by `lastPromotedOn`) — beside the ✓ on a green
  tile, alone on a blue one. It says *that* the item moved, not to where.
  **Demotions carry no overlay** — visible as a state change, never announced.
- **Mid-day**, the map shows current state including today's arrows; the **movement
  animation is reserved for the day close**.
- **Day close:** the questions simply cease at the day goal; the map animates the day's
  movements, with a short sound of its own — distinct from the answer chime **in kind,
  not in size**. Nothing is awarded. A day with zero movements (every day after
  completion; an all-missed day) is a quiet map, not an empty sentence.
- **Terminal event** (36th item consolidated, one-time, never re-fired): an **overlay
  on the map** — the last tile animates, sparks fly, a full-area moment fades out. No
  acknowledge button. This is the one place the app goes big.
- **Permanent mark:** from completion on, a **small golden rosette next to the title
  in the map header** — non-numeric, dateless, independent of how many tiles are
  currently green. Tile = current state (revocable); map = high-water mark (permanent).

Primary source: prototype on branch `prototype/progress-map`
(`prototype/progress-map.html`).

## 11. Reward layer

**Nothing accumulates** (ADR-0005). The reward layer is exactly four surfaces, all of
which already exist as domain state: the per-answer feedback (§9), the progress map,
the day close, and the terminal event (§10). No streak, no notifications, no anti-
farming mechanic — volume, speed and re-drilling are structurally unrewardable, and
*one counting answer per item per day* is the whole guard.

## 12. Data model, persistence, architecture

Decided in ADR-0001 (data model), ADR-0002 (DataStore + kotlinx.serialization, one
~2 KB JSON file, whole state as one immutable data class, `version` field for breaking
migrations) and ADR-0003 (single Gradle module with `engine`/`data`/`ui` packages; the
engine is pure `(State, Event, LocalDate) -> State` with injected date and randomness;
one Activity, one `AppViewModel`, screens a 3-case sealed class).

Persistent state, complete:

```
per item (×36, keyed by unordered factor pair):
  level:               1..5
  lastCountedOn:       LocalDate?
  satisfiedOn:         LocalDate?
  hasEverConsolidated: Bool
  lastPromotedOn:      LocalDate?

global:
  calibrationIndex:      0..36     # 36 = calibration complete
  calibrationMissStreak: Int       # persisted mercy-stop counter
  wasEverCompleted:      Bool      # the rosette
  version:               Int
```

Everything else — pool, learning front, open set, tile states — is derived at read
time. Writes happen **per answer**, never per session. `rejectedToday` is in-memory
only.

## 13. Validated properties and accepted costs

An engine simulation (six learner profiles × 90 days, branch `prototype/engine-sim`,
`prototype/engine_sim.py`) validated the engine as specified — build it without
re-tuning:

- No oscillation stall; **zero unreached day goals** in ~160 000 simulated play days;
  no guess-inflated mastery (level 5 needs free-input success, so the ≈4 % guessing
  residual produces nothing).
- Mercy stop = 6 and front width = 10 both validated; **do not re-tune**.
- Completion (all 36) median: day 5 (fluent) / 14 (strong) / 54 (average) / >90 (weak).
  Accepted — the ladder's length *is* the distributed practice.

Costs accepted knowingly (do not "fix" these):

- **Worst day ≈ 90 questions** (weak, sparse learner; mean 45–49/day). The pain is a
  long day, not a stuck item; leaving is free, so no mechanic.
- **After completion the day goal is a permanent ~36-question daily obligation**
  (~4–7 min), with nothing ever again moving on the map. No off-ramp — a reduced
  end-state load would be a scheduler, which the design rejected.
- **The map regresses across days** (revocations are visible; the monotone indicator is
  within-day only). The revocable tile is the honest reading; permanence lives in the
  rosette.
- **The button ladder is a transient:** 83–99 % of item-days are free input. Don't
  over-invest design weight in levels 1–3.
- **Midnight/device-clock farming** is possible and undefended (§7).

## 14. Deliberately left at platform defaults

**Accessibility** was never given its own decision pass. v1 uses Material defaults:
system font scaling respected, touch targets ≥ 48 dp, contrast per the prototype
palette (§10). Revisit only if real-child testing shows a need.

## 15. Non-goals

Ruled out of v1's scope; do not re-open without redrawing the destination:

- **Parent / statistics view** — any progress-reporting surface beyond what the child
  sees. No timeline, no dates, no counts.
- **Play Store release, monetisation, store compliance** (Families Policy, privacy
  policy, store assets).
- **Backend, cloud sync, accounts.**
- **Multiple profiles, sibling comparison or competition** (evidenced cost accepted
  knowingly — competition is a positive moderator in the research).
- **Facts with factor 1 or 10; division; missing-factor tasks (7×?=42); anything
  beyond 9×9.**
- **Any speed, latency or time-pressure mechanic**, including timed bonus windows and
  personal-best times.
- **iOS or any cross-platform target.**
