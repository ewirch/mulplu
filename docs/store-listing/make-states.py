#!/usr/bin/env python3
"""Author the five app_state.json snapshots the store screenshots need (#54).

Usage: make-states.py <today, YYYY-MM-DD> <output dir>
Driven by capture-screenshots.sh; the dates must match the device's own today,
or the "day done" state reads as a fresh day.
"""
import json, sys, os

TODAY = sys.argv[1]
OUT = sys.argv[2]

ORDER = [(2,2),(2,3),(3,3),(2,4),(2,5),(4,4),(2,6),(3,4),(2,9),(2,7),
         (3,5),(2,8),(5,5),(3,6),(3,9),(4,5),(3,7),(3,8),(4,6),(4,9),
         (6,6),(4,7),(5,6),(5,9),(4,8),(7,7),(5,7),(6,9),(9,9),(5,8),
         (6,7),(7,9),(8,8),(6,8),(8,9),(7,8)]
KEY = ["%dx%d" % k for k in ORDER]

def item(level=1, counted=None, satisfied=None, cons=False, promoted=None):
    return {"level": level, "lastCountedOn": counted, "satisfiedOn": satisfied,
            "hasEverConsolidated": cons, "lastPromotedOn": promoted}

def state(items, cal_index=36, cal_streak=0, completed=False):
    full = {k: item() for k in KEY}
    full.update(items)
    return {"version": 1, "items": full, "calibrationIndex": cal_index,
            "calibrationMissStreak": cal_streak, "wasEverCompleted": completed}

# --- the shared mid-progress shape -----------------------------------------
# 0..13 have all consolidated once; two of them sit demoted, so they read as
# "in play" again. 14..23 are the learning front. 24..35 untouched.
CONSOLIDATED = list(range(14))
DEMOTED = {11: 4, 13: 4}          # 2x8, 3x6 — consolidated once, back in play
FRONT_LEVELS = {14: 3, 15: 2, 16: 1, 17: 2, 18: 2, 19: 1, 20: 3, 21: 1, 22: 4, 23: 1}

def mid(satisfied_all=False, promoted=(), level_overrides=None, open_items=()):
    """satisfied_all: every pool item answered today (day done).
       open_items: indices deliberately left open when satisfied_all is set."""
    items = {}
    for i in CONSOLIDATED:
        lvl = DEMOTED.get(i, 5)
        items[KEY[i]] = item(level=lvl, cons=True)
    for i, lvl in FRONT_LEVELS.items():
        items[KEY[i]] = item(level=lvl)
    if level_overrides:
        for i, lvl in level_overrides.items():
            items[KEY[i]]["level"] = lvl
    pool = CONSOLIDATED + sorted(FRONT_LEVELS)
    if satisfied_all:
        for i in pool:
            if i in open_items:
                continue
            items[KEY[i]]["satisfiedOn"] = TODAY
            items[KEY[i]]["lastCountedOn"] = TODAY
    for i in promoted:
        items[KEY[i]]["lastPromotedOn"] = TODAY
    return items

STATES = {
    # 1 — progress map, day still open: start button, no arrows, no rosette.
    "01-map-mid": state(mid()),

    # 4 — the same map after the day's round: "Für heute fertig" plus the
    #     promoted-today arrows the day close leaves behind.
    "04-day-done": state(mid(satisfied_all=True,
                             promoted=(14, 16, 18, 21, 23),
                             level_overrides={14: 4, 16: 2, 18: 3, 21: 2, 23: 2})),

    # 2 — one open item at level 2 -> multiple choice with three options.
    "02-choice": state(mid(satisfied_all=True, open_items=(18,),
                           level_overrides={18: 2})),

    # 3 — the same item (4x6) at level 4 -> free input.
    "03-free-input": state(mid(satisfied_all=True, open_items=(18,),
                               level_overrides={18: 4})),

    # 5 — first run: the companion introduces the calibration pass. The probe
    #     screen itself is not used here, it is the free-input screen twice.
    "05-cal-intro": state({}, cal_index=0),
}

os.makedirs(OUT, exist_ok=True)
for name, st in STATES.items():
    with open(os.path.join(OUT, name + ".json"), "w") as f:
        json.dump(st, f)
    print(name)
