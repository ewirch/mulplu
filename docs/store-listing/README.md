# Play store listing assets

The files Play will not publish a listing without (#54). Specification and
listing copy live in #48, the icon's visual direction in #49.

| File | Play slot | Format |
| --- | --- | --- |
| `icon-512.png` | App icon | 512×512, 24-bit PNG, no alpha |
| `feature-graphic.png` | Feature graphic | 1024×500, 24-bit PNG, no alpha |
| `screenshots/*.png` | Phone screenshots | 1080×1920, 24-bit PNG, no alpha |

Screenshot order is the one fixed in #48: progress map, choice question, free
input, day close, calibration. Uncaptioned by decision — no text overlays.

## The limits that actually bite

- **Aspect ratio.** The hard rule is *longest side at most twice the shortest*
  (plus 320–3840 px per side). A raw capture from a modern phone — 1080×2400,
  2.22:1 — is **rejected**; 16:9 / 9:16 is only a recommendation, but it is the
  easy way to stay inside the hard rule, so the capture forces 1080×1920.
- **No alpha.** Screenshots and the feature graphic must be 24-bit. The icon may
  carry alpha but must not use it — a transparent icon shows the Play UI's own
  background through.
- **Icon shape.** Full square, no rounded corners, no shadow: Play masks it
  itself at a 30% radius. `icon-512.svg` is therefore the launcher icon's 108dp
  canvas cropped to the 72dp square the launcher actually shows, so store and
  device carry one image; the motif lands at 341 px, inside Google's 384 px
  keyline.
- **Status bar.** Play asks for a clean one — no notifications, no carrier,
  battery and wifi full. The capture script drives SystemUI demo mode for that.

## Regenerating

The two graphics are SVG sources rasterised with `rsvg-convert`:

```sh
rsvg-convert -w 512 -h 512 icon-512.svg | magick png:- -alpha off \
  -define png:color-type=2 icon-512.png
rsvg-convert -w 1024 -h 500 feature-graphic.svg | magick png:- -alpha off \
  -define png:color-type=2 feature-graphic.png
```

The feature graphic's wordmark is **Roboto** — the face the app itself renders
in. It is not a system font on every host; pull it off a running device with
`adb pull /system/fonts/Roboto-Regular.ttf` and point fontconfig at it.

The screenshots come from `./capture-screenshots.sh`, which authors five
`app_state.json` snapshots (`make-states.py`), pushes each into the app's
DataStore and captures the screen. Read the header comment first: it needs the
test-panel handle suppressed by a local, uncommitted edit, which is the one step
the script cannot do for itself.

Re-running reproduces `01`, `04` and `05` byte for byte. `02` and `03` differ a
little every time — the presentation order of the two factors and the choice
distractors are drawn per question, by design.
