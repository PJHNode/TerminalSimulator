package gameoflife;

public class GameOfLife {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;
    private static final double ALIVE_PROBABILITY = 0.3;
    private static final int FRAME_DELAY_MS = 100;
    private static final char ALIVE_CHAR = '█';
    private static final char DEAD_CHAR = ' ';

    private static final String MOVE_CURSOR_HOME = "[H";
    private static final String CLEAR_SCREEN = "[2J[H";
    private static final String HIDE_CURSOR = "[?25l";
    private static final String SHOW_CURSOR = "[?25h";

    public static void main(String[] args) throws InterruptedException {
        ConsoleUtil.configureUtf8Console();
        showBranding();

        // Restore the cursor if the user stops the loop with Ctrl+C.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.print(SHOW_CURSOR)));

        Grid grid = new Grid(WIDTH, HEIGHT);
        grid.randomize(ALIVE_PROBABILITY);

        System.out.print(HIDE_CURSOR);
        System.out.print(CLEAR_SCREEN);

        long generation = 0;
        while (true) {
            render(grid, generation);
            grid = grid.nextGeneration();
            generation++;
            Thread.sleep(FRAME_DELAY_MS);
        }
    }

    private static void showBranding() throws InterruptedException {
        System.out.println("Conway's Game of Life — Made by JUNEHYUN");
        Thread.sleep(2000);
    }

    // Builds the full frame in one buffer, then writes it in a single call so the
    // screen updates without flicker (cursor is moved home instead of clearing each frame).
    private static void render(Grid grid, long generation) {
        StringBuilder frame = new StringBuilder();
        frame.append(MOVE_CURSOR_HOME);
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                frame.append(grid.isAlive(x, y) ? ALIVE_CHAR : DEAD_CHAR);
            }
            frame.append('\n');
        }
        frame.append("Generation: ").append(generation)
                .append(" | Alive cells: ").append(grid.countAliveCells())
                .append('\n');
        System.out.print(frame);
        System.out.flush();
    }
}
