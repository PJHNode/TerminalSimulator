# Conway's Game of Life (Terminal Edition)

A terminal-rendered implementation of Conway's Game of Life, written in pure
Java with zero external dependencies. The simulation runs forever, redrawing
the grid in place with ANSI escape codes so the terminal never scrolls and
the animation never flickers.

> Conway's Game of Life — Made by JUNEHYUN

## Requirements

- JDK 8 or newer (developed and tested against JDK 17.0.12 LTS)
- A terminal that supports ANSI escape codes (Windows Terminal, macOS
  Terminal, iTerm2, or any Linux terminal emulator)

## How to run

From the `conway-game-of-life` directory:

```bash
javac -encoding UTF-8 -d out src/gameoflife/*.java
java -cp out gameoflife.GameOfLife
```

`-encoding UTF-8` is required because the source contains the `█` and `—`
characters directly. Press `Ctrl+C` to stop; the terminal cursor is restored
automatically on exit.

## Project structure

```
conway-game-of-life/
├── README.md
├── .gitignore
└── src/
    └── gameoflife/
        ├── Grid.java
        └── GameOfLife.java
```

## How it works

`Grid` holds the cell state and knows how to count neighbors, randomize
itself, and compute the next generation. `nextGeneration()` returns a new
`Grid` instead of mutating in place, so each generation is a clean snapshot.

`GameOfLife` is the entry point. It creates the grid, then loops: render the
current generation, advance to the next one, sleep for the frame interval.

Each frame is assembled into a single string and written to `System.out` in
one call. Instead of clearing the screen every frame, the cursor is moved
back to the top-left corner (`ESC[H`) and the previous frame is simply
overwritten — since every frame has the same dimensions, this is flicker-free.
Alive cells are drawn as `█` (U+2588), dead cells as a space.

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
represented and print as `?` instead. To handle this, `GameOfLife` switches
the console to UTF-8 on startup (`chcp 65001`, only on Windows) and writes to
`System.out` through an explicit UTF-8 `PrintStream`. macOS and Linux skip
this step since their terminals default to UTF-8 already.

## Configuration

Tunable constants live at the top of `GameOfLife.java`:

| Constant             | Default | Meaning |
|------------------------|---------|---------|
| `WIDTH`                 | `80`     | grid columns |
| `HEIGHT`                | `24`     | grid rows |
| `ALIVE_PROBABILITY`     | `0.3`    | chance each cell starts alive |
| `FRAME_DELAY_MS`        | `100`    | milliseconds between generations |
