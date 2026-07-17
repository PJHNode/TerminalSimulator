# TerminalSimulator

A collection of terminal-based simulations and mini-games. Each project lives
in its own subdirectory, is self-contained (its own source, README, and build
instructions), and can be built/run independently of the others.

## Projects

| Project | Description | Language | Status |
|---------|--------------|----------|--------|
| [conway-game-of-life](conway-game-of-life) | Conway's Game of Life, rendered live in the terminal using ANSI escape codes (no flicker, no external libraries). | Pure Java | ✅ Playable |

More simulations will be added over time as new subdirectories, following the
same layout described below.

## Quick start

Every project only requires a JDK — there's no build tool (Maven/Gradle) or
external dependency to install. To try the Game of Life, for example:

```bash
cd conway-game-of-life
javac -encoding UTF-8 -d out src/gameoflife/*.java
java -cp out gameoflife.GameOfLife
```

See each project's own README for full details, controls, and configuration
options.

## Requirements

- JDK 8 or newer (developed against JDK 17 LTS)
- A terminal that supports ANSI escape codes (Windows Terminal, macOS
  Terminal, iTerm2, or essentially any Linux terminal emulator)

## Repository layout

```
TerminalSimulator/
├── README.md                 # this file — index of all projects
└── <project-name>/           # one folder per simulation, fully self-contained
    ├── README.md              # project-specific docs (setup, rules, screenshots)
    ├── .gitignore              # excludes that project's own build output
    └── src/                    # project-specific source code
```

### Adding a new project

1. Create a new top-level folder named after the project (kebab-case).
2. Keep it self-contained: its own `src/`, `.gitignore`, and `README.md`
   describing what it does and how to run it — don't share code or a build
   file across projects.
3. Add a row for it in the [Projects](#projects) table above.

## License

No license has been declared yet — all rights reserved by default until one
is added.
