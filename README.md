# TerminalSimulator

A collection of terminal- and browser-based simulations and mini-games. Each
project lives in its own subdirectory with its own source and README, and
builds/runs independently of the others.

## Projects

| Project | Description | Language |
|---------|--------------|----------|
| [conway-game-of-life](conway-game-of-life) | Conway's Game of Life, rendered live in the terminal with ANSI escape codes. | Java |
| [game-of-life-interactive](game-of-life-interactive) | Click-to-draw Conway's Game of Life in the browser — place/erase cells, start/pause/step. | JavaScript |

## Quick start

```bash
# Terminal version
cd conway-game-of-life
javac -encoding UTF-8 -d out src/gameoflife/*.java
java -cp out gameoflife.GameOfLife

# Interactive browser version
cd game-of-life-interactive
start index.html   # or just open the file directly
```

See each project's README for full details and configuration.

## Requirements

- **conway-game-of-life**: JDK 8 or newer, a terminal that supports ANSI
  escape codes (Windows Terminal, macOS Terminal, iTerm2, or any Linux
  terminal emulator)
- **game-of-life-interactive**: any modern browser — no install needed

## Repository layout

```
TerminalSimulator/
├── README.md
└── <project-name>/
    ├── README.md
    ├── .gitignore
    └── src/
```
