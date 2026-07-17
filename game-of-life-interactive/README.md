# Conway's Game of Life (Interactive, Browser)

A click-to-draw version of Conway's Game of Life that runs in the browser,
written in plain JavaScript with no build step and no dependencies. Unlike
the [terminal version](../conway-game-of-life), you control the board
directly: place cells, erase them, and start/stop the simulation whenever
you want.

> Conway's Game of Life — Made by JUNEHYUN

## How to run

No install, no server required — just open the file:

```bash
cd game-of-life-interactive
start index.html      # Windows
open index.html        # macOS
xdg-open index.html    # Linux
```

Or just double-click `index.html` in a file browser.

## Controls

| Control | Effect |
|---------|--------|
| Click a cell | Toggles it alive/dead |
| Click and drag | Paints a run of cells with the same value as the first cell you touched (draws if it was dead, erases if it was alive) |
| **Start / Pause** | Runs or stops the simulation |
| **Step** | Advances exactly one generation (only works while paused) |
| **Random** | Pauses and refills the board with a fresh ~30% random state |
| **Clear** | Pauses and wipes the board to all-dead |

You can also draw while the simulation is running — cells you toggle take
effect on the next generation.

## Project structure

```
game-of-life-interactive/
├── README.md
├── index.html
├── style.css
└── script.js
```

## How it works

`script.js` keeps the board as a 2D array of `0`/`1` and renders it to a
`<canvas>` each frame — no DOM elements per cell, so redraws stay cheap even
while dragging. The simulation rules and the toroidal (edge-wrapping)
neighbor counting are the same ones used in the [terminal
version](../conway-game-of-life): a live cell survives with 2–3 live
neighbors, and a dead cell is born with exactly 3.

Dragging determines whether it draws or erases from the *first* cell the
drag touches: if that cell was dead, the whole drag paints alive cells; if it
was alive, the whole drag erases. This matches how most Game of Life editors
handle click-and-drag.

## Configuration

Tunable constants live at the top of `script.js`:

| Constant | Default | Meaning |
|----------|---------|---------|
| `COLS` | `60` | grid columns |
| `ROWS` | `35` | grid rows |
| `CELL_SIZE` | `14` | pixel size per cell |
| `STEP_DELAY_MS` | `120` | milliseconds between generations while running |
| `RANDOM_ALIVE_PROBABILITY` | `0.3` | chance each cell starts alive on Random |
