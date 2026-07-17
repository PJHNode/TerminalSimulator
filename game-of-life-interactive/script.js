(function () {
  const COLS = 60;
  const ROWS = 35;
  const CELL_SIZE = 14;
  const STEP_DELAY_MS = 120;
  const RANDOM_ALIVE_PROBABILITY = 0.3;

  const canvas = document.getElementById("grid");
  canvas.width = COLS * CELL_SIZE;
  canvas.height = ROWS * CELL_SIZE;
  const ctx = canvas.getContext("2d");

  const toggleBtn = document.getElementById("toggleBtn");
  const stepBtn = document.getElementById("stepBtn");
  const randomBtn = document.getElementById("randomBtn");
  const clearBtn = document.getElementById("clearBtn");
  const statusEl = document.getElementById("status");

  let grid = createEmptyGrid();
  let generation = 0;
  let running = false;
  let timerId = null;
  let isPainting = false;
  let paintAlive = true;

  function createEmptyGrid() {
    return Array.from({ length: ROWS }, () => new Uint8Array(COLS));
  }

  function randomizeGrid() {
    for (let y = 0; y < ROWS; y++) {
      for (let x = 0; x < COLS; x++) {
        grid[y][x] = Math.random() < RANDOM_ALIVE_PROBABILITY ? 1 : 0;
      }
    }
    generation = 0;
    draw();
  }

  // Wraps at the edges so the grid behaves like a torus, same as the terminal version.
  function countAliveNeighbors(x, y) {
    let count = 0;
    for (let dy = -1; dy <= 1; dy++) {
      for (let dx = -1; dx <= 1; dx++) {
        if (dx === 0 && dy === 0) continue;
        const nx = (x + dx + COLS) % COLS;
        const ny = (y + dy + ROWS) % ROWS;
        count += grid[ny][nx];
      }
    }
    return count;
  }

  function step() {
    const next = createEmptyGrid();
    for (let y = 0; y < ROWS; y++) {
      for (let x = 0; x < COLS; x++) {
        const neighbors = countAliveNeighbors(x, y);
        const alive = grid[y][x] === 1;
        next[y][x] = alive ? (neighbors === 2 || neighbors === 3 ? 1 : 0) : (neighbors === 3 ? 1 : 0);
      }
    }
    grid = next;
    generation++;
    draw();
  }

  function countAliveCells() {
    let count = 0;
    for (let y = 0; y < ROWS; y++) {
      for (let x = 0; x < COLS; x++) {
        count += grid[y][x];
      }
    }
    return count;
  }

  function draw() {
    ctx.fillStyle = "#0b0d12";
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = "#4ade80";
    for (let y = 0; y < ROWS; y++) {
      for (let x = 0; x < COLS; x++) {
        if (grid[y][x]) {
          ctx.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
        }
      }
    }
    statusEl.textContent = `Generation: ${generation} | Alive: ${countAliveCells()}`;
  }

  function setRunning(value) {
    running = value;
    toggleBtn.textContent = running ? "Pause" : "Start";
    if (running) {
      scheduleNextStep();
    } else if (timerId !== null) {
      clearTimeout(timerId);
      timerId = null;
    }
  }

  function scheduleNextStep() {
    timerId = setTimeout(() => {
      step();
      if (running) {
        scheduleNextStep();
      }
    }, STEP_DELAY_MS);
  }

  function cellFromPoint(clientX, clientY) {
    const rect = canvas.getBoundingClientRect();
    const x = Math.floor(((clientX - rect.left) / rect.width) * COLS);
    const y = Math.floor(((clientY - rect.top) / rect.height) * ROWS);
    return { x, y };
  }

  function paintCell(x, y) {
    if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
      return;
    }
    grid[y][x] = paintAlive ? 1 : 0;
    draw();
  }

  function beginPaint(clientX, clientY) {
    const { x, y } = cellFromPoint(clientX, clientY);
    if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
      return;
    }
    isPainting = true;
    // Flip whatever cell the drag starts on, then drag paints the rest with that same value.
    paintAlive = grid[y][x] === 0;
    paintCell(x, y);
  }

  canvas.addEventListener("mousedown", (evt) => beginPaint(evt.clientX, evt.clientY));
  canvas.addEventListener("mousemove", (evt) => {
    if (isPainting) {
      const { x, y } = cellFromPoint(evt.clientX, evt.clientY);
      paintCell(x, y);
    }
  });
  window.addEventListener("mouseup", () => {
    isPainting = false;
  });

  canvas.addEventListener("touchstart", (evt) => {
    evt.preventDefault();
    const touch = evt.touches[0];
    beginPaint(touch.clientX, touch.clientY);
  });
  canvas.addEventListener("touchmove", (evt) => {
    evt.preventDefault();
    if (isPainting) {
      const touch = evt.touches[0];
      const { x, y } = cellFromPoint(touch.clientX, touch.clientY);
      paintCell(x, y);
    }
  });
  window.addEventListener("touchend", () => {
    isPainting = false;
  });

  toggleBtn.addEventListener("click", () => setRunning(!running));
  stepBtn.addEventListener("click", () => {
    setRunning(false);
    step();
  });
  randomBtn.addEventListener("click", () => {
    setRunning(false);
    randomizeGrid();
  });
  clearBtn.addEventListener("click", () => {
    setRunning(false);
    grid = createEmptyGrid();
    generation = 0;
    draw();
  });

  randomizeGrid();
})();
