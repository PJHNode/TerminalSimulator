# TerminalSimulator

A collection of terminal-based simulations and mini-games. Each project lives
in its own subdirectory with its own source and README, and builds/runs
independently of the others.

## Projects

| Project | Description | Language |
|---------|--------------|----------|
| [conway-game-of-life](conway-game-of-life) | Conway's Game of Life, rendered live in the terminal with ANSI escape codes. | Java |

## Quick start

```bash
cd conway-game-of-life
javac -encoding UTF-8 -d out src/gameoflife/*.java
java -cp out gameoflife.GameOfLife
```

See each project's README for full details and configuration.

## Requirements

- JDK 8 or newer (developed against JDK 17 LTS)
- A terminal that supports ANSI escape codes (Windows Terminal, macOS
  Terminal, iTerm2, or any Linux terminal emulator)

## Repository layout

```
TerminalSimulator/
├── README.md
└── <project-name>/
    ├── README.md
    ├── .gitignore
    └── src/
```
