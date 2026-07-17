# Conway's Game of Life (Terminal Edition)

A terminal-rendered implementation of Conway's Game of Life, written in pure Java
with no external dependencies. The simulation runs forever, redrawing the grid
in place using ANSI escape codes so the terminal never scrolls or flickers.

> Conway's Game of Life — Made by JUNEHYUN

## Requirements

- JDK 8 or newer
- A terminal that supports ANSI escape codes (Windows Terminal, macOS Terminal,
  most Linux terminal emulators)

## How to run

From the `conway-game-of-life` directory:

```bash
# Compile
javac -d out src/gameoflife/*.java

# Run
java -cp out gameoflife.GameOfLife
```

Press `Ctrl+C` to stop the simulation.

## Screenshot

![Conway's Game of Life running in a terminal](screenshots/demo.png)

*(Place a screenshot of the running simulation at `screenshots/demo.png`.)*

## How it works

- **Grid** (`src/gameoflife/Grid.java`) holds the cell state and knows how to
  count neighbors, randomize itself, and compute the next generation.
- **GameOfLife** (`src/gameoflife/GameOfLife.java`) is the entry point. It
  creates the grid, drives the simulation loop, and renders each frame to the
  terminal.

### Rendering

- Alive cells are drawn as `█`, dead cells as a space.
- Each frame is built into a single string and printed in one call, with the
  cursor moved back to the top-left (`\033[H`) instead of clearing the screen,
  so the redraw is flicker-free.
- The current generation number and the count of alive cells are printed on
  the line below the grid.

### Simulation rules (Conway's original rules)

- An **alive** cell with 2 or 3 alive neighbors survives; otherwise it dies.
- A **dead** cell with exactly 3 alive neighbors becomes alive.

### Grid topology

The grid is **toroidal**: the left/right edges wrap around to each other, as
do the top/bottom edges, so patterns can travel seamlessly across boundaries.

### Defaults

| Setting              | Value                     |
|-----------------------|---------------------------|
| Grid size              | 80 columns × 24 rows      |
| Initial alive chance   | ~30% per cell             |
| Frame interval         | 100 ms                    |
