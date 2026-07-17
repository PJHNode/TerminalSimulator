package gameoflife;

import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

// Terminal input has no raw mouse/keystroke mode in pure Java, so cells are placed
// and removed by typing "x y" coordinates instead of clicking.
public class InteractiveGameOfLife {

    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    private static final int FRAME_DELAY_MS = 300;
    private static final char ALIVE_CHAR = '█';
    private static final char DEAD_CHAR = '.';

    private static final Object gridLock = new Object();
    private static Grid grid = new Grid(WIDTH, HEIGHT);
    private static long generation = 0;
    private static final AtomicBoolean running = new AtomicBoolean(false);

    public static void main(String[] args) {
        ConsoleUtil.configureUtf8Console();
        showBranding();
        printHelp();
        printBoard();

        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        while (!quit && scanner.hasNextLine()) {
            System.out.print("> ");
            quit = handleCommand(scanner.nextLine().trim());
        }
        stopRunning();
        scanner.close();
        System.out.println("Bye.");
    }

    private static void showBranding() {
        System.out.println("Conway's Game of Life — Interactive — Made by JUNEHYUN");
        sleep(1000);
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("  x y   toggle the cell at column x, row y (e.g. \"5 3\")");
        System.out.println("  s     start running continuously");
        System.out.println("  p     pause");
        System.out.println("  n     advance one generation (only while paused)");
        System.out.println("  r     fill the board with a random pattern");
        System.out.println("  c     clear the board");
        System.out.println("  q     quit");
    }

    private static boolean handleCommand(String line) {
        switch (line.toLowerCase(Locale.ROOT)) {
            case "":
                return false;
            case "q":
                return true;
            case "s":
                startRunning();
                return false;
            case "p":
                stopRunning();
                printBoard();
                return false;
            case "n":
                stepOnce();
                return false;
            case "r":
                stopRunning();
                synchronized (gridLock) {
                    grid.randomize(0.3);
                    generation = 0;
                }
                printBoard();
                return false;
            case "c":
                stopRunning();
                synchronized (gridLock) {
                    grid = new Grid(WIDTH, HEIGHT);
                    generation = 0;
                }
                printBoard();
                return false;
            default:
                handleToggleCommand(line);
                return false;
        }
    }

    private static void stepOnce() {
        if (running.get()) {
            System.out.println("Pause first with 'p' before stepping manually.");
            return;
        }
        synchronized (gridLock) {
            grid = grid.nextGeneration();
            generation++;
        }
        printBoard();
    }

    private static void handleToggleCommand(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2) {
            System.out.println("Unknown command. Type 'x y' to toggle a cell, or one of s/p/n/r/c/q.");
            return;
        }
        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
                System.out.printf("x must be 0-%d and y must be 0-%d.%n", WIDTH - 1, HEIGHT - 1);
                return;
            }
            synchronized (gridLock) {
                grid.toggle(x, y);
            }
            printBoard();
        } catch (NumberFormatException e) {
            System.out.println("Unknown command. Type 'x y' to toggle a cell, or one of s/p/n/r/c/q.");
        }
    }

    private static void startRunning() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        Thread runner = new Thread(() -> {
            while (running.get()) {
                synchronized (gridLock) {
                    grid = grid.nextGeneration();
                    generation++;
                }
                printBoard();
                sleep(FRAME_DELAY_MS);
            }
        });
        runner.setDaemon(true);
        runner.start();
    }

    private static void stopRunning() {
        running.set(false);
    }

    private static void printBoard() {
        StringBuilder sb = new StringBuilder();
        synchronized (gridLock) {
            sb.append("     ");
            for (int x = 0; x < WIDTH; x++) {
                sb.append(x / 10);
            }
            sb.append('\n');
            sb.append("     ");
            for (int x = 0; x < WIDTH; x++) {
                sb.append(x % 10);
            }
            sb.append('\n');
            for (int y = 0; y < HEIGHT; y++) {
                sb.append(String.format("%3d  ", y));
                for (int x = 0; x < WIDTH; x++) {
                    sb.append(grid.isAlive(x, y) ? ALIVE_CHAR : DEAD_CHAR);
                }
                sb.append('\n');
            }
            sb.append("Generation: ").append(generation)
                    .append(" | Alive cells: ").append(grid.countAliveCells())
                    .append(running.get() ? " | RUNNING (type p + Enter to pause)" : " | PAUSED")
                    .append('\n');
        }
        System.out.print(sb);
        System.out.flush();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
