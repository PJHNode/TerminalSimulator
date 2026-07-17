# Conway's Game of Life (Terminal Edition)

Conway's Game of Life, written in pure Java with zero external dependencies.
There are two entry points, both plain console programs — no browser, no GUI
toolkit:

- **`GameOfLife`** — starts from a random pattern and animates forever,
  flicker-free, until you hit `Ctrl+C`.
- **`InteractiveGameOfLife`** — you place and remove cells yourself by typing
  coordinates, then start/stop/step the simulation on command.

> Conway's Game of Life — Made by JUNEHYUN

## Requirements

- JDK 8 or newer (developed and tested against JDK 17.0.12 LTS)
- A terminal that supports ANSI escape codes (Windows Terminal, macOS
  Terminal, iTerm2, or any Linux terminal emulator)

## How to run

**1. Check you have a JDK installed:**

```bash
java -version
javac -version
```

If either command isn't found, install a JDK (e.g. [Adoptium
Temurin](https://adoptium.net/)) and make sure `java`/`javac` are on your
`PATH`.

**2. Get the code, if you haven't already:**

```bash
git clone https://github.com/PJHNode/TerminalSimulator.git
cd TerminalSimulator/conway-game-of-life
```

**3. Compile:**

```bash
javac -encoding UTF-8 -d out src/gameoflife/*.java
```

`-encoding UTF-8` is required because the source contains the `█` and `—`
characters directly — without it, `javac` fails with "unmappable character"
errors. This produces compiled classes under `out/gameoflife/`.

**4. Run one of the two entry points:**

```bash
# Automatic: random start, animates forever
java -cp out gameoflife.GameOfLife

# Interactive: you place the cells
java -cp out gameoflife.InteractiveGameOfLife
```

### Automatic mode

After a 2-second branding pause, the terminal fills with an 80×24 grid of
randomly placed `█` cells that animates in place, with a status line below
it (`Generation: 42 | Alive cells: 187`). Press `Ctrl+C` to stop — there's no
end condition, it's a live simulation. The cursor, hidden while it runs, is
restored automatically on exit.

### Interactive mode

The board starts empty. A coordinate ruler runs along the top and left edge
so you know which `x y` to type:

```
     0000000000111111111122222222223333333333
     0123456789012345678901234567890123456789
  0  ........................................
  1  ........................................
  2  ..███...................................
```

Type a command and press Enter:

| Command | Effect |
|---------|--------|
| `x y` | toggle the cell at column `x`, row `y` (e.g. `5 3`) |
| `s` | start running continuously |
| `p` | pause |
| `n` | advance exactly one generation (only while paused) |
| `r` | fill the board with a random ~30% pattern |
| `c` | clear the board |
| `q` | quit |

You can keep toggling cells while it's running — the terminal has no live
mouse/keystroke input in pure Java, so drawing happens between generations
rather than mid-frame, but the effect is the same: type coordinates, hit
Enter, watch it take effect on the next generation.

If the grid renders as `?` characters instead of blocks, see [Windows console
encoding](#windows-console-encoding) below.

## Project structure

```
conway-game-of-life/
├── README.md
├── .gitignore
└── src/
    └── gameoflife/
        ├── Grid.java
        ├── ConsoleUtil.java
        ├── GameOfLife.java
        └── InteractiveGameOfLife.java
```

## How it works

`Grid` holds the cell state and knows how to count neighbors, randomize
itself, toggle a single cell, and compute the next generation.
`nextGeneration()` returns a new `Grid` instead of mutating in place, so each
generation is a clean snapshot.

`GameOfLife` renders automatically: each frame is assembled into a single
string and written to `System.out` in one call, with the cursor moved back
to the top-left corner (`ESC[H`) instead of clearing the screen — since every
frame has the same dimensions, this is flicker-free.

`InteractiveGameOfLife` reads commands from a `Scanner` on the main thread.
`s` spawns a background thread that steps and reprints the board on a timer;
`p` stops it. Both threads only ever touch the grid inside a shared lock, so
typing a coordinate while the simulation is running can't corrupt a
generation that's mid-computation.

`ConsoleUtil` holds the Windows UTF-8 console setup shared by both entry
points (see below).

### Simulation rules

| Current state | Live neighbors | Next state |
|----------------|-----------------|------------|
| Alive           | 2 or 3           | stays alive |
| Alive           | anything else    | dies |
| Dead            | exactly 3         | becomes alive |
| Dead            | anything else    | stays dead |

### Grid topology

The grid is toroidal: the right edge wraps to the left edge, and the bottom
edge wraps to the top edge, so patterns travel seamlessly across boundaries
instead of being clipped at the edge of the screen.

### Windows console encoding

The JVM's default output encoding follows the Windows console codepage, not
UTF-8. On a codepage like MS949 (Korean) or CP437, `█` and `—` can't be
represented and print as garbled multi-byte text instead. To handle this,
`ConsoleUtil` switches the console to UTF-8 on startup (`chcp 65001`, only on
Windows) and writes to `System.out` through an explicit UTF-8 `PrintStream`.
macOS and Linux skip this step since their terminals default to UTF-8
already.

The `chcp` helper's stdout/stderr are inherited from the real console on
purpose — if they aren't, Windows runs the helper against a hidden,
disconnected console, and the codepage change never reaches the window
you're actually looking at. Its stdin is separately pointed at the `NUL`
device (not inherited), so it can't consume any of the real input meant for
`InteractiveGameOfLife`.

If the grid still renders as garbled text after this, or the screen scrolls
instead of redrawing in place, your terminal likely isn't processing ANSI
escape codes at all (common on an older `cmd.exe` window rather than Windows
Terminal). Try running from Windows Terminal or PowerShell instead, or run
`chcp 65001` by hand once in that window before launching the program.

## Configuration

Tunable constants live at the top of each entry point's source file:

**`GameOfLife.java`**

| Constant             | Default | Meaning |
|------------------------|---------|---------|
| `WIDTH`                 | `80`     | grid columns |
| `HEIGHT`                | `24`     | grid rows |
| `ALIVE_PROBABILITY`     | `0.3`    | chance each cell starts alive |
| `FRAME_DELAY_MS`        | `100`    | milliseconds between generations |

**`InteractiveGameOfLife.java`**

| Constant             | Default | Meaning |
|------------------------|---------|---------|
| `WIDTH`                 | `40`     | grid columns |
| `HEIGHT`                | `20`     | grid rows |
| `FRAME_DELAY_MS`        | `300`    | milliseconds between generations while running |
