#!/usr/bin/env python3
"""Simulation of the mulplu adaptive engine (issue #13).

Throwaway validation script. Implements the engine exactly as specified by
issues #2 (level ladder), #5 (day goal, admission order, selection) and
#7 (calibration, learning-front pool), drives it with synthetic learners and
reports the five metrics the ticket asks for.

Not production code. Python because it is a throwaway.
"""

import random
import statistics
from dataclasses import dataclass, field

# ---------------------------------------------------------------- engine spec

# Frozen admission order from #5.
RANKING = [
    (2, 2), (2, 3), (3, 3), (2, 4), (2, 5), (4, 4), (2, 6), (3, 4), (2, 9), (2, 7),
    (3, 5), (2, 8), (5, 5), (3, 6), (3, 9), (4, 5), (3, 7), (3, 8), (4, 6), (4, 9),
    (6, 6), (4, 7), (5, 6), (5, 9), (4, 8), (7, 7), (5, 7), (6, 9), (9, 9), (5, 8),
    (6, 7), (7, 9), (8, 8), (6, 8), (8, 9), (7, 8),
]
assert len(RANKING) == 36 and len(set(RANKING)) == 36

FRONT_WIDTH = 10        # learning front, #7
MERCY_STOP = 6          # consecutive wrong probes, #7
BUTTONS = {1: 2, 2: 3, 3: 4}   # level -> option count; 4,5 -> free input


@dataclass
class Item:
    idx: int
    a: int
    b: int
    level: int = 1
    last_counted_on: int = -1
    satisfied_on: int = -1
    has_ever_consolidated: bool = False
    seeded: bool = False        # probed during calibration
    # instrumentation
    level_history: list = field(default_factory=list)
    consolidated_on: int = -1
    revocations: int = 0

    @property
    def product(self):
        return self.a * self.b


# ------------------------------------------------------------ synthetic learner

@dataclass
class Profile:
    name: str
    base: float          # knowledge of the easiest item at start
    slope: float         # how much harder items start off
    learn: float         # learning rate per meaningful exposure
    noise: float         # per-trial performance noise (inconsistency)
    forget: float        # per-missed-day knowledge decay (learner-side, not engine)
    attendance: float    # probability of playing on a given day


PROFILES = [
    #                        base slope learn noise forget attend
    Profile("ideal",         0.98, 0.00, 0.30, 0.02, 0.000, 1.00),
    Profile("strong",        0.95, 0.35, 0.22, 0.05, 0.005, 0.95),
    Profile("average",       0.90, 0.70, 0.15, 0.10, 0.010, 0.85),
    Profile("weak",          0.70, 0.95, 0.08, 0.12, 0.020, 0.80),
    Profile("inconsistent",  0.85, 0.60, 0.10, 0.30, 0.030, 0.60),
    Profile("weak+sparse",   0.65, 0.95, 0.07, 0.15, 0.030, 0.50),
]


def item_hardness(a, b):
    """0 = easiest, 1 = hardest. Problem-size effect, squares/x9 a bit easier."""
    p = a * b
    if a == b or a == 9 or b == 9:
        p *= 0.85
    lo, hi = 4.0, 56.0
    return max(0.0, min(1.0, (p - lo) / (hi - lo)))


class Learner:
    """Per-item knowledge in [0,1] = probability of a correct *free input* answer."""

    def __init__(self, profile, rng):
        self.p = profile
        self.rng = rng
        self.k = {}
        for i, (a, b) in enumerate(RANKING):
            h = item_hardness(a, b)
            self.k[i] = max(0.02, min(0.99, profile.base - profile.slope * h))

    def answers_correctly(self, item, options):
        """options=None -> free input. Otherwise n multiple-choice options."""
        k = self.k[item.idx]
        eff = max(0.0, min(1.0, self.rng.gauss(k, self.p.noise)))
        knows = self.rng.random() < eff
        if knows:
            return True, True          # correct, knew it
        if options is None:
            return False, False
        # distractors are non-eliminable (#5) -> uniform guess
        return self.rng.random() < 1.0 / options, False

    def study(self, item, knew, saw_answer):
        """Learning: recalling correctly teaches most, seeing the answer teaches some."""
        g = self.p.learn if knew else (self.p.learn * 0.55 if saw_answer else 0.0)
        self.k[item.idx] += g * (1.0 - self.k[item.idx])

    def decay(self, days=1):
        if self.p.forget <= 0:
            return
        for i in self.k:
            self.k[i] *= (1.0 - self.p.forget) ** days


# ------------------------------------------------------------------- the engine

class Engine:
    def __init__(self, rng, front_width=FRONT_WIDTH):
        self.items = [Item(i, a, b) for i, (a, b) in enumerate(RANKING)]
        self.rng = rng
        self.front_width = front_width
        self.rejected_today = {i: set() for i in range(36)}

    def pool(self):
        """#7: {ever consolidated} u {first 10 never-consolidated of the ranking}."""
        return [it for it in self.items if it.has_ever_consolidated] + self.front()

    def front(self):
        return [it for it in self.items
                if not it.has_ever_consolidated][:self.front_width]

    def open_today(self, day):
        return [it for it in self.pool() if it.satisfied_on != day]

    def on_answer(self, item, correct, day):
        counting = item.last_counted_on < day
        if counting:
            item.last_counted_on = day
            before = item.level
            item.level = min(5, before + 1) if correct else max(1, before - 1)
            item.level_history.append((day, item.level))
            if before == 5 and item.level < 5:
                item.revocations += 1
            if item.level == 5:
                if not item.has_ever_consolidated:
                    item.has_ever_consolidated = True
                    item.consolidated_on = day
                    # the newly admitted item is not due until tomorrow (#5)
                    newly = self.front()
                    if newly:
                        newly[-1].satisfied_on = day
        if correct:
            item.satisfied_on = day
        else:
            self.rejected_today[item.idx].add(-1)  # only counted for stats
        return counting


# ------------------------------------------------------------------ one lifetime

@dataclass
class DayLog:
    day: int
    played: bool
    presentations: int = 0
    open_at_start: int = 0
    pool_size: int = 0
    consolidated: int = 0
    goal_reached: bool = False
    misses: int = 0
    longest_miss_streak: int = 0
    worst_item_reps: int = 0      # most presentations a single item needed today


def simulate(profile, days=90, seed=0, front_width=FRONT_WIDTH, mercy=MERCY_STOP):
    rng = random.Random(seed)
    eng = Engine(rng, front_width=front_width)
    learner = Learner(profile, rng)
    logs = []
    calib = {"probed": 0, "correct": 0, "stopped_at": None, "missed_known": 0}
    last_played = 0

    for day in range(1, days + 1):
        if day > 1:
            if rng.random() > profile.attendance:
                logs.append(DayLog(day, False,
                                   pool_size=len(eng.pool()),
                                   consolidated=sum(1 for i in eng.items if i.level == 5)))
                continue
            learner.decay(day - last_played)
        last_played = day
        eng.rejected_today = {i: set() for i in range(36)}
        log = DayLog(day, True)

        if day == 1:
            # ---- calibration (#7): all 36 probed once, free input, in ranking order
            streak = 0
            for it in eng.items:
                if streak >= mercy:
                    calib["stopped_at"] = it.idx
                    # would this item have been answered correctly? (false-stop cost)
                    if learner.k[it.idx] > 0.5:
                        calib["missed_known"] += 1
                    it.level = 1
                    it.seeded = True
                    continue
                correct, knew = learner.answers_correctly(it, None)
                log.presentations += 1
                calib["probed"] += 1
                it.seeded = True
                it.last_counted_on = day
                if correct:
                    calib["correct"] += 1
                    it.level = 5
                    it.has_ever_consolidated = True
                    it.consolidated_on = day
                    it.satisfied_on = day
                    streak = 0
                else:
                    it.level = 1
                    streak += 1
                # neutral feedback: no answer shown during calibration
                learner.study(it, knew, saw_answer=False)

        # ---- the day goal
        log.pool_size = len(eng.pool())
        log.open_at_start = len(eng.open_today(day))
        last_shown = None
        guard = 0
        streak = 0
        per_item_reps = {}
        while True:
            open_now = eng.open_today(day)
            if not open_now:
                log.goal_reached = True
                break
            guard += 1
            if guard > 4000:
                break
            cand = [i for i in open_now if i is not last_shown] or open_now
            it = rng.choice(cand)
            last_shown = it
            opts = BUTTONS.get(it.level)          # None for level 4/5 -> free input
            correct, knew = learner.answers_correctly(it, opts)
            log.presentations += 1
            per_item_reps[it.idx] = per_item_reps.get(it.idx, 0) + 1
            if correct:
                streak = 0
            else:
                streak += 1
                log.misses += 1
                log.longest_miss_streak = max(log.longest_miss_streak, streak)
            eng.on_answer(it, correct, day)
            learner.study(it, knew, saw_answer=not correct)

        log.worst_item_reps = max(per_item_reps.values()) if per_item_reps else 0
        log.consolidated = sum(1 for i in eng.items if i.level == 5)
        logs.append(log)

    return eng, learner, logs, calib


# ---------------------------------------------------------------------- report

def pct(xs, q):
    if not xs:
        return 0
    xs = sorted(xs)
    return xs[min(len(xs) - 1, int(q * len(xs)))]


def report(runs=200, days=90):
    print(f"{'profile':14s} {'day1':>6s} {'reps/day':>17s} {'openmax':>8s} "
          f"{'pool=36':>8s} {'allcons':>8s} {'never':>6s} {'revoc':>6s} {'infl':>6s}")
    print("-" * 96)
    for profile in PROFILES:
        d1, reps, openmax, pool36, allcons, never, revoc, inflated, fast = \
            [], [], [], [], [], [], [], [], []
        for s in range(runs):
            eng, learner, logs, calib = simulate(profile, days=days, seed=s)
            played = [l for l in logs if l.played]
            d1.append(played[0].presentations)
            reps += [l.presentations for l in played[1:]]
            openmax.append(max(l.open_at_start for l in played))
            p36 = next((l.day for l in logs if l.pool_size == 36), None)
            pool36.append(p36 if p36 else days + 1)
            ac = next((l.day for l in logs if l.consolidated == 36), None)
            allcons.append(ac if ac else days + 1)
            never.append(sum(1 for i in eng.items if not i.has_ever_consolidated))
            revoc.append(sum(i.revocations for i in eng.items))
            # guess inflation: items sitting at level 5 whose true free-recall p < 0.6
            l5 = [i for i in eng.items if i.level == 5]
            inflated.append(sum(1 for i in l5 if learner.k[i.idx] < 0.6))
            # consolidated in the theoretical minimum with weak knowledge
            fast.append(sum(1 for i in eng.items
                            if 0 <= i.consolidated_on <= 5 and learner.k[i.idx] < 0.6))
        print(f"{profile.name:14s} {statistics.mean(d1):6.0f} "
              f"{statistics.mean(reps):6.1f} p95={pct(reps,0.95):3d} max={max(reps):4d} "
              f"{statistics.mean(openmax):8.1f} "
              f"{statistics.median(pool36):8.0f} {statistics.median(allcons):8.0f} "
              f"{statistics.mean(never):6.2f} {statistics.mean(revoc):6.1f} "
              f"{statistics.mean(inflated):6.2f}")


def report_oscillation(runs=200, days=90):
    print("\n--- oscillation detail (level sign-flips per item over 90 days) ---")
    for profile in PROFILES:
        flips_all, worst, stalled = [], 0, 0
        for s in range(runs):
            eng, learner, logs, _ = simulate(profile, days=days, seed=s)
            for it in eng.items:
                lv = [l for _, l in it.level_history]
                f = sum(1 for i in range(1, len(lv) - 1)
                        if (lv[i] - lv[i - 1]) * (lv[i + 1] - lv[i]) < 0)
                flips_all.append(f)
                worst = max(worst, f)
            # stalled = an item in the front for >30 days without consolidating
            for it in eng.items:
                if not it.has_ever_consolidated and len(it.level_history) > 30:
                    stalled += 1
        print(f"{profile.name:14s} mean flips/item={statistics.mean(flips_all):5.2f} "
              f"worst={worst:3d}  stalled-items/run={stalled/runs:5.2f}")


def report_calibration(runs=500):
    print("\n--- calibration & mercy stop (constant = 6) ---")
    for profile in PROFILES:
        probed, stops, missed, d1cons = [], 0, [], []
        for s in range(runs):
            eng, learner, logs, c = simulate(profile, days=1, seed=s)
            probed.append(c["probed"])
            if c["stopped_at"] is not None:
                stops += 1
            missed.append(c["missed_known"])
            d1cons.append(sum(1 for i in eng.items if i.has_ever_consolidated))
        print(f"{profile.name:14s} probed={statistics.mean(probed):5.1f}/36 "
              f"mercy-stop fired in {100*stops/runs:5.1f}% of runs  "
              f"items-skipped-but-known={statistics.mean(missed):4.2f}  "
              f"seeded-consolidated={statistics.mean(d1cons):5.1f}")


def report_front_width(runs=200, days=90):
    print("\n--- sensitivity: learning-front width ---")
    print(f"{'width':>6s} {'profile':14s} {'reps/day':>9s} {'p95':>5s} "
          f"{'openmax':>8s} {'allcons(med)':>13s}")
    for w in (6, 8, 10, 14):
        for profile in PROFILES:
            if profile.name not in ("average", "weak", "inconsistent"):
                continue
            reps, openmax, allcons = [], [], []
            for s in range(runs):
                eng, learner, logs, _ = simulate(profile, days=days, seed=s,
                                                 front_width=w)
                played = [l for l in logs if l.played]
                reps += [l.presentations for l in played[1:]]
                openmax.append(max(l.open_at_start for l in played))
                ac = next((l.day for l in logs if l.consolidated == 36), None)
                allcons.append(ac if ac else days + 1)
            print(f"{w:6d} {profile.name:14s} {statistics.mean(reps):9.1f} "
                  f"{pct(reps,0.95):5d} {statistics.mean(openmax):8.1f} "
                  f"{statistics.median(allcons):13.0f}")


def report_frustration(runs=300, days=90):
    print("\n--- frustration & termination ---")
    print(f"{'profile':14s} {'goal-fails':>10s} {'misses/day':>11s} "
          f"{'missstreak p95/max':>19s} {'worst-item reps p95/max':>24s} "
          f"{'steady reps/day':>16s}")
    for profile in PROFILES:
        fails, misses, streaks, worst, steady = 0, [], [], [], []
        for s in range(runs):
            eng, learner, logs, _ = simulate(profile, days=days, seed=s)
            played = [l for l in logs if l.played]
            fails += sum(1 for l in played if not l.goal_reached)
            misses += [l.misses for l in played[1:]]
            streaks += [l.longest_miss_streak for l in played[1:]]
            worst += [l.worst_item_reps for l in played[1:]]
            # steady state = days after all 36 are in the pool
            steady += [l.presentations for l in played if l.pool_size == 36][-20:]
        print(f"{profile.name:14s} {fails:10d} {statistics.mean(misses):11.1f} "
              f"{pct(streaks,0.95):9d}/{max(streaks):<9d} "
              f"{pct(worst,0.95):12d}/{max(worst):<11d} "
              f"{statistics.mean(steady) if steady else 0:16.1f}")


def report_revocation_churn(runs=300, days=90):
    print("\n--- consolidation churn (progress-map tile flicker, #9) ---")
    for profile in PROFILES:
        per_item, days_cons, tiles_lost = [], [], []
        for s in range(runs):
            eng, learner, logs, _ = simulate(profile, days=days, seed=s)
            ever = [i for i in eng.items if i.has_ever_consolidated]
            per_item += [i.revocations for i in ever]
            tiles_lost.append(sum(1 for i in ever if i.level < 5))
        print(f"{profile.name:14s} revocations/consolidated-item "
              f"mean={statistics.mean(per_item):5.2f} p95={pct(per_item,0.95):3d} "
              f"max={max(per_item):3d}   tiles showing 'in play' after being "
              f"consolidated (day 90): {statistics.mean(tiles_lost):5.2f}")


def report_level4_trap(runs=300, days=90):
    """The retry-is-easier promise of #5 fails for level 4: a wrong counting answer
    at level 5 drops to 4, which is *also* free input. Only 4->3 gets easier."""
    print("\n--- the level-4 trap: reps needed to satisfy an item, by level at "
          "the counting answer ---")
    print(f"{'profile':14s} {'lvl<=3 mean/p95/max':>21s} {'lvl4+ mean/p95/max':>21s} "
          f"{'share of item-days spent at lvl4+':>34s}")
    for profile in PROFILES:
        low, high = [], []
        for s in range(runs):
            rng = random.Random(10_000 + s)
            eng = Engine(rng)
            learner = Learner(profile, rng)
            for day in range(1, days + 1):
                if day > 1 and rng.random() > profile.attendance:
                    continue
                if day > 1:
                    learner.decay()
                if day == 1:
                    streak = 0
                    for it in eng.items:
                        if streak >= MERCY_STOP:
                            it.level = 1
                            continue
                        correct, knew = learner.answers_correctly(it, None)
                        it.last_counted_on = day
                        if correct:
                            it.level, it.has_ever_consolidated = 5, True
                            it.satisfied_on = day
                            streak = 0
                        else:
                            it.level, streak = 1, streak + 1
                        learner.study(it, knew, False)
                # level at the first (counting) answer of the day, per item
                entry = {it.idx: it.level for it in eng.pool()
                         if it.satisfied_on != day}
                reps = {}
                last = None
                while True:
                    op = eng.open_today(day)
                    if not op:
                        break
                    cand = [i for i in op if i is not last] or op
                    it = rng.choice(cand)
                    last = it
                    reps[it.idx] = reps.get(it.idx, 0) + 1
                    correct, knew = learner.answers_correctly(
                        it, BUTTONS.get(it.level))
                    eng.on_answer(it, correct, day)
                    learner.study(it, knew, not correct)
                for idx, n in reps.items():
                    (high if entry.get(idx, 1) >= 4 else low).append(n)
        tot = len(low) + len(high)
        print(f"{profile.name:14s} "
              f"{statistics.mean(low):6.2f}/{pct(low,0.95):3d}/{max(low):<8d} "
              f"{statistics.mean(high):6.2f}/{pct(high,0.95):3d}/{max(high):<8d} "
              f"{100*len(high)/tot:33.1f}%")


if __name__ == "__main__":
    report()
    report_oscillation()
    report_calibration()
    report_frustration()
    report_revocation_churn()
    report_front_width()
