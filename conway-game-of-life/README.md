# Conway's Game of Life (Terminal Edition)

A terminal-rendered implementation of Conway's Game of Life, written in pure
Java with **zero external dependencies** — no build tool, no libraries, just
the JDK. The simulation runs forever, redrawing the grid in place with ANSI
escape codes so the terminal never scrolls and the animation never flickers.

> Conway's Game of Life — Made by JUNEHYUN

## Table of contents

- [Requirements](#requirements)
- [How to run](#how-to-run)
- [Screenshot](#screenshot)
- [Project structure](#project-structure)
- [How it works](#how-it-works)
  - [Rendering the frame](#rendering-the-frame)
  - [Simulation rules](#simulation-rules-conways-original-rules)
  - [Grid topology](#grid-topology)
  - [Windows console encoding](#windows-console-encoding)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

## Requirements

- **JDK 8 or newer** (developed and tested against JDK 17.0.12 LTS)
- A terminal that understands ANSI escape codes:
  - Windows: Windows Terminal, or the modern `conhost` shipped with
    Windows 10/11 (the app switches the codepage to UTF-8 for you at
    startup — see [Windows console encoding](#windows-console-encoding))
  - macOS: Terminal.app, iTerm2
  - Linux: essentially any terminal emulator

No Maven, Gradle, or third-party JARs are involved — everything is compiled
directly with `javac`.

## How to run

From the `conway-game-of-life` directory:

```bash
# 1. Compile (UTF-8 source encoding is required because the source files
#    contain the "█" and "—" characters directly)
javac -encoding UTF-8 -d out src/gameoflife/*.java

# 2. Run
java -cp out gameoflife.GameOfLife
```

You should see the branding line, a 2-second pause, and then the grid
animating in place. Press `Ctrl+C` to stop the simulation at any time; the
terminal cursor is restored automatically on exit.

## Screenshot

![Conway's Game of Life running in a terminal](screenshots/demo.png)

*(Place a screenshot of the running simulation at `screenshots/demo.png`. A
terminal window showing a mid-generation grid plus the
`Generation: N | Alive cells: M` status line works well.)*

## Project structure

```
conway-game-of-life/
├── README.md
├── .gitignore
├── screenshots/            # put demo.png here
└── src/
    └── gameoflife/
        ├── Grid.java       # cell state, neighbor counting, next-generation logic
        └── GameOfLife.java # entry point: simulation loop + terminal rendering
```

## How it works

The project is intentionally split into exactly two classes, each with a
single responsibility:

- **`Grid`** is a plain data class that knows nothing about the terminal. It
  owns a `boolean[][]` of cell states and exposes:
  - `randomize(double aliveProbability)` — seeds the grid randomly
  - `isAlive(x, y)` — reads a cell's state
  - `countAliveNeighbors(x, y)` — counts live neighbors with toroidal wrapping
  - `nextGeneration()` — applies Conway's rules and returns a **new** `Grid`
    for the next generation (the current grid is never mutated in place)
  - `countAliveCells()` — total number of live cells, used for the status line
- **`GameOfLife`** is the entry point. It owns the simulation loop: create a
  `Grid`, randomize it, then repeatedly render the current generation, advance
  to the next one, and sleep for the frame interval.

### Rendering the frame

Every frame is assembled into a single `StringBuilder` — the grid rows plus
the trailing status line — and written to `System.out` in one `print` call.
Instead of clearing the screen every frame (which causes visible flicker),
the renderer moves the cursor back to the top-left corner with the ANSI
sequence `ESC[H` and simply overwrites the previous frame in place. Because
every frame has exactly the same dimensions (80×24 plus one status line), the
overwrite fully covers the previous contents with no leftover artifacts.

The cursor itself is hidden for the duration of the run (`ESC[?25l`) and
restored on exit via a JVM shutdown hook (`ESC[?25h`), so a `Ctrl+C` doesn't
leave the terminal cursor invisible afterwards.

Alive cells are drawn as the full block character `█` (U+2588); dead cells
are a plain space.

### Simulation rules (Conway's original rules)

Applied to every cell simultaneously, based on the *current* generation:

| Current state | Live neighbors | Next state |
|----------------|-----------------|------------|
| Alive           | 2 or 3           | stays alive (survival) |
| Alive           | anything else    | dies (under- or over-population) |
| Dead            | exactly 3         | becomes alive (birth) |
| Dead            | anything else    | stays dead |

### Grid topology

The grid is **toroidal**, not bounded: the right edge wraps to the left edge,
and the bottom edge wraps to the top edge (`Grid.countAliveNeighbors` uses
modular arithmetic for this). This means gliders and other patterns can
travel indefinitely across a boundary instead of being clipped or reflected
at the edge of the screen.

### Windows console encoding

This is a subtlety worth calling out explicitly, since it's the one thing
that actually broke during testing on a Korean-locale Windows machine: the
JVM's default output encoding follows the **Windows console codepage**, not
UTF-8. On a system where that codepage is something like MS949 (Korean) or
CP437, the `█` and `—` characters used by this program cannot be represented
and print as `?` instead of rendering correctly — even though the compiled
`.class` files are perfectly fine.

To make the program render correctly regardless of the host's locale,
`GameOfLife.configureUtf8Console()` does two things on startup, only on
Windows (`os.name` contains `"win"`):

1. Shells out to `cmd.exe /c chcp 65001` to switch the **active console's**
   codepage to UTF-8. Because the child process shares the same console
   handle as the JVM, this actually changes the codepage the terminal is
   using, not just the child process's own.
2. Replaces `System.out` with a `PrintStream` explicitly constructed with
   `StandardCharsets.UTF_8`, so the bytes Java writes are UTF-8 regardless of
   what `file.encoding`/`native.encoding` the JVM started with.

On macOS and Linux this step is skipped entirely, since terminals there
default to UTF-8 already.

## Configuration

All the tunable constants live at the top of `GameOfLife.java`:

| Constant             | Default | Meaning |
|------------------------|---------|---------|
| `WIDTH`                 | `80`     | grid columns |
| `HEIGHT`                | `24`     | grid rows |
| `ALIVE_PROBABILITY`     | `0.3`    | chance each cell starts alive (~30%) |
| `FRAME_DELAY_MS`        | `100`    | milliseconds between generations |

Change a value and recompile to experiment with larger grids, denser/sparser
random seeds, or faster/slower playback.

## Troubleshooting

- **I see `?` instead of block characters.** Your terminal isn't in UTF-8
  mode. On Windows this should be handled automatically at startup; if it
  still doesn't work, run `chcp 65001` manually before launching the program,
  or switch to Windows Terminal.
- **`javac` fails with "unmappable character" errors.** You compiled without
  `-encoding UTF-8`. The source files contain literal `█` and `—` characters
  that require it — use the exact compile command shown above.
- **The grid looks torn or partially overwritten.** Your terminal window is
  smaller than 80×24. Resize it (or lower `WIDTH`/`HEIGHT` and recompile) so
  the whole frame fits without wrapping.
