# Context: multiplication-table trainer

Vocabulary of the domain. Terms here are the ones used in code, tickets and ADRs.
Settled so far by [#2](https://github.com/ewirch/mulplu/issues/2) and
[#5](https://github.com/ewirch/mulplu/issues/5).

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

A new item enters at level 1. Level 1 is the floor.

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

Minimum time from entry to consolidated: **4 days**.

## Consolidated (`gefestigt`)

An item is **consolidated** when it reaches level 5 — i.e. the child has produced its
answer by free input, without choices, at least once.

Reaching it fires once **per item, ever**: one new item is admitted to the question
pool. An item that re-earns the status after a revocation admits nothing further —
otherwise an item oscillating between levels 4 and 5 would pump the pool, and
instability would be rewarded with more new material.

A wrong counting answer on a consolidated item drops it to level 4 and **revokes** the
status; it is re-earned the same way.

## Question pool

The set of items the app currently asks from — always a **prefix of the admission
order**. Its size is the starting size plus the number of items that have ever been
consolidated, capped at 36.

It therefore **never shrinks** by construction rather than by rule: an item that falls
back from consolidated stays in the pool, and membership is not something the app
records per item.

The starting size is 10. Calibration
([#7](https://github.com/ewirch/mulplu/issues/7)) may raise it and may seed levels, but
may never lower it or pull an individual item in out of order.

## Admission order

The fixed, once-authored ranking of all 36 items from easiest to hardest. It decides
which item is admitted when a consolidation fires, and — as the pool's prefix — which
items the app starts with.

It is a frozen list, not a formula evaluated at runtime. Its ordering follows the
problem-size effect (smaller product = easier) with the shortcut families (squares,
`×9`) pulled forward.

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
