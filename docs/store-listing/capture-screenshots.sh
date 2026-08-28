#!/bin/bash
# Re-capture the five Play phone screenshots (#54) from a running emulator.
#
# Prerequisite that this script cannot do for you: the test-panel handle (#30)
# must not be on screen. Suppress it with a *local, uncommitted* early return in
# TestPanel.kt, build and install the debug variant, then run this. R8 is off
# (#46), so debug and release render identically apart from that handle.
#
#   # in TestPanel.kt, after the BuildConfig.TEST_HOOKS check:
#   if (true) return
#   ./gradlew :app:assembleDebug
#   adb install -r app/build/outputs/apk/debug/app-debug.apk
#
# Play's hard limits: longest side at most twice the shortest, 320-3840px. A
# raw 1080x2400 phone capture is 2.22:1 and gets rejected, hence the 1080x1920
# override below.
set -euo pipefail

cd "$(dirname "$0")"
OUT=${1:-screenshots}
TODAY=$(adb shell date +%F | tr -d '\r')
TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT

python3 make-states.py "$TODAY" "$TMP/states"

adb shell wm size 1080x1920

# Clean status bar: full wifi and battery, no notifications, fixed clock —
# Play asks for exactly this ("do not show service providers or notifications").
adb shell settings put global sysui_demo_allowed 1
demo() { adb shell am broadcast -a com.android.systemui.demo "$@" >/dev/null; }
demo -e command enter
demo -e command clock -e hhmm 0930
demo -e command battery -e level 100 -e plugged false
demo -e command network -e wifi show -e level 4
demo -e command network -e mobile hide
demo -e command notifications -e visible false

push() {
  adb shell am force-stop com.mulplu.app
  adb push "$TMP/states/$1.json" /data/local/tmp/app_state.json >/dev/null
  adb shell run-as com.mulplu.app cp /data/local/tmp/app_state.json \
    /data/data/com.mulplu.app/files/datastore/app_state.json
  adb shell am start -n com.mulplu.app/.ui.MainActivity >/dev/null
  sleep 3
}
shot() { adb exec-out screencap -p | magick png:- -alpha off \
  -define png:color-type=2 "$OUT/$1.png"; }

mkdir -p "$OUT"

push 01-map-mid                      && shot 01-fortschrittskarte
push 02-choice && adb shell input tap 540 1613 && sleep 2 && shot 02-auswahlfrage
push 03-free-input && adb shell input tap 540 1613 && sleep 2 && shot 03-freie-eingabe
push 04-day-done                     && shot 04-tagesabschluss
push 05-cal-intro                    && shot 05-kalibrierung

adb shell wm size reset
demo -e command exit
echo "written to $OUT"
