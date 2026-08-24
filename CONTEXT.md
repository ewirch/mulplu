# Context: multiplication-table trainer

Vocabulary of the domain. Terms here are the ones used in code, tickets and ADRs.
Settled so far by [#2](https://github.com/ewirch/mulplu/issues/2),
[#5](https://github.com/ewirch/mulplu/issues/5) and
[#7](https://github.com/ewirch/mulplu/issues/7).

## Item

An **item** is an *unordered* pair of factors drawn from 2–9. `3×4` and `4×3` are the
same item and share one state — commutativity is not something the child is asked to
re-learn per direction.

There are **36 items**. Factors 1 and 10 are excluded: multiplying by them is trivial
and not worth practice.

Which of the two orders is shown when an item is asked is presentation, not state.

## Level

The **level** is the only state an item carries. It is both the difficulty setting and
the measure of how well the item is known — there is no separate score.

| Level | Presentation |
|---|---|
| 1 | 2 answer buttons |
| 2 | 3 answer buttons |
| 3 | 4 answer buttons |
| 4 | free input |
| 5 | **consolidated** — free input, produced correctly at least once |

Levels 1–3 are multiple choice; the child presses one of the offered numbers. Levels 4
and 5 present the same free-input interaction — they differ only in status.

Every item's initial level is seeded by calibration: produced correctly → level 5,
otherwise → level 1. Level 1 is the floor.

## Counting answer

The **counting answer** is the *first* answer given to an item on a given local
calendar day (day boundary: midnight, local time).

Per item this needs one date ("last counted on") alongside the level. No timestamps.

## Practice answer

Every answer to an item after its counting answer on the same day is a **practice
answer**. It moves nothing — the child may repeat an item until it sticks, but only
distributed practice moves the ladder, because only long-term retention is the goal.

A practice answer is presented at the item's *current* level, i.e. after any movement
the counting answer caused. A wrong counting answer therefore makes the next practice
answer **easier**, not harder — the ladder runs in the helpful direction.

## Movement

- Counting answer **correct** → level + 1
- Counting answer **wrong** → level − 1

That is the whole rule. There is no counter, no point value, no per-day cap as a
separate rule (at most one movement per item per day follows from the definition of a
counting answer), and no time-based decay: an item keeps being asked, so forgetting is
measured rather than estimated.

Minimum time from level 1 to consolidated: **4 days**. (Calibration can seed level 5
directly — see Calibration.)

## Consolidated (`gefestigt`)

An item is **consolidated** when it reaches level 5 — i.e. the child has produced its
answer by free input, without choices, at least once.

First-ever consolidation admits one new item: the item leaves the learning front's
window and the next never-consolidated item in admission order slides in. An item
that re-earns the status after a revocation admits nothing further (it no longer
occupies the window) — an item oscillating between levels 4 and 5 cannot pump the
pool.

A wrong counting answer on a consolidated item drops it to level 4 and **revokes** the
status; it is re-earned the same way.

## Question pool

The set of items the app currently asks from:

**pool = { every item ever consolidated } ∪ { the first 10 never-consolidated items
of the admission order }**

The 10-wide window is the **learning front**: at most 10 never-consolidated items are
in play at once. When fewer than 10 never-consolidated items remain, the window
shrinks; the pool caps at 36.

Membership is derived from per-item state (`hasEverConsolidated` plus the admission
order), never stored. The pool **never shrinks**: `hasEverConsolidated` is monotone,
and a first-time consolidation moves the item from the window into the
ever-consolidated set while the next never-consolidated item slides in. A revoked
item stays in the pool via the ever-consolidated set and occupies no window slot.

With nothing known at calibration this reduces to the first 10 items of the admission
order — the cold-start case. (Settled by
[#7](https://github.com/ewirch/mulplu/issues/7), amending #5's prefix formulation.)

## Admission order

The fixed, once-authored ranking of all 36 items from easiest to hardest. It decides
the order in which never-consolidated items enter the learning front — new material
always arrives easiest-first. (Since #7 the pool as a whole is no longer a prefix of
this order: items the calibration found already known join the pool regardless of
their position.)

It is a frozen list, not a formula evaluated at runtime. Its ordering follows the
problem-size effect (smaller product = easier) with the shortcut families (squares,
`×9`) pulled forward.

## Calibration (`Kalibrierung`)

The one-time first-run pass that seeds every item's initial level: each of the 36
items is asked once by free input. Correct → seeded **consolidated** (level 5),
wrong → **level 1**. Nothing in between — a single untimed probe cannot tell fluent
from shaky; the ladder sorts that out in normal practice, where a miscalibrated
"known" is asked daily and revoked on its first wrong counting answer.

## Day goal

The **day goal** is reached when every item in the question pool has been answered
correctly at least once on the current day. Until then the item stays in the day's
open set and is re-asked; a wrong answer does not retire it.

Selection is a uniform random draw from the open set, never the item just shown unless
it is the only one left. An item admitted part-way through a day counts as satisfied
for that day and first becomes due the next day — so the day goal, once seen, does not
recede while the child works.

What the child gets for reaching it is gamification, not domain — see
[#9](https://github.com/ewirch/mulplu/issues/9).

## Distractor

A wrong number offered alongside the correct one on levels 1–3. Distractors are drawn
from the neighbourhood of the correct product — other products of the 36-item table,
preferring those sharing a factor — because that is where children's multiplication
errors actually live.

A distractor must never be eliminable by anything but knowing the answer. This is what
makes the level ladder the *only* guard against guessing: the number of choices carries
the difficulty, never their plausibility, which is uniform across levels.

## Not part of this domain

**Nothing is timed.** No countdown, no response-time measurement, no personal-best
time, no time pressure of any kind. Correctness is the only signal the app records.
