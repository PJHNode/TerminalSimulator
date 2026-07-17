package gameoflife;

import java.util.Random;

public class Grid {

    private final int width;
    private final int height;
    private final boolean[][] cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new boolean[height][width];
    }

    private Grid(int width, int height, boolean[][] cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    public void randomize(double aliveProbability) {
        Random random = new Random();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = random.nextDouble() < aliveProbability;
            }
        }
    }

    public boolean isAlive(int x, int y) {
        return cells[y][x];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Wraps at the edges so the grid behaves like a torus.
    public int countAliveNeighbors(int x, int y) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                int nx = wrap(x + dx, width);
                int ny = wrap(y + dy, height);
                if (cells[ny][nx]) {
                    count++;
                }
            }
        }
        return count;
    }

    private int wrap(int value, int max) {
        return ((value % max) + max) % max;
    }

    public Grid nextGeneration() {
        boolean[][] next = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int neighbors = countAliveNeighbors(x, y);
                boolean alive = cells[y][x];
                next[y][x] = alive ? (neighbors == 2 || neighbors == 3) : (neighbors == 3);
            }
        }
        return new Grid(width, height, next);
    }

    public int countAliveCells() {
        int count = 0;
        for (boolean[] row : cells) {
            for (boolean cell : row) {
                if (cell) {
                    count++;
                }
            }
        }
        return count;
    }
}
