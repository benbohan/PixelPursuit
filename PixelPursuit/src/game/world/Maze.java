package game.world;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.settings.GameConfig;

/**
 * Rectangular maze made of Cells:
 *  - Uses GameConfig for default width/height and difficulty-based settings.
 *  - Can load ASCII mazes from mazes.txt or procedurally generate layouts.
 *  - Tracks entrance/exit cells and offers helpers to clear entities and gold.
 */
public class Maze {

    // ---------- FIELDS ----------

    // Logical dimensions (in cells)
    private final int width;
    private final int height;

    // cells[row][col] -> cells[y][x]
    private final Cell[][] cells;

    private int entranceX, entranceY;
    private int exitX, exitY;

    private final Random rng = new Random();

    // Path relative to PROJECT ROOT:
    // PixelPursuit/src/game/resources/data/mazes.txt
    private static final String MAZES_RELATIVE_PATH = "src/game/resources/data/mazes.txt";

    // ---------- CONSTRUCTORS ----------

    // Maze - Constructs a maze using default dimensions from GameConfig
    public Maze() {
        this(GameConfig.MAZE_WIDTH, GameConfig.MAZE_HEIGHT);
    }

    // Maze - Constructs a maze with an explicit width and height
    public Maze(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Maze size must be positive");
        }

        this.width  = width;
        this.height = height;
        this.cells  = new Cell[height][width];

        // Start with everything walkable; we'll apply a layout next.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(x, y, true);
            }
        }

        // Try to load a preset; if that fails, fall back to a random layout.
        if (!loadRandomPresetFromFile()) {
            generateRandomLayout();
        }
    }

    // ---------- DIMENSIONS / CELLS ----------

    // getWidth - Returns the maze width in cells
    public int getWidth() {
        return width;
    }

    // getHeight - Returns the maze height in cells
    public int getHeight() {
        return height;
    }

    // inBounds - Returns true if (x, y) is inside the maze grid
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width
            && y >= 0 && y < height;
    }

    // getCell - Returns the Cell at (x, y) or throws if out of bounds
    public Cell getCell(int x, int y) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException(
                    "Cell coordinates out of bounds: (" + x + ", " + y + ")");
        }
        return cells[y][x];
    }

    // getEntranceX - Returns the entrance x-coordinate
    public int getEntranceX() { return entranceX; }

    // getEntranceY - Returns the entrance y-coordinate
    public int getEntranceY() { return entranceY; }

    // getExitX - Returns the exit x-coordinate
    public int getExitX()     { return exitX; }

    // getExitY - Returns the exit y-coordinate
    public int getExitY()     { return exitY; }

    // getEntranceCell - Returns the entrance cell
    public Cell getEntranceCell() { return getCell(entranceX, entranceY); }

    // getExitCell - Returns the exit cell
    public Cell getExitCell()     { return getCell(exitX, exitY); }

    // ---------- FILE RESOLUTION ----------

    // findMazesFile - Attempts to locate mazes.txt from common working directories
    private File findMazesFile() {
        // Helpful debug if something goes wrong
        System.out.println("Maze: working dir = " + new File(".").getAbsolutePath());

        // 1) Try from current working directory (usually project root)
        File f1 = new File(MAZES_RELATIVE_PATH);
        if (f1.exists()) {
            System.out.println("Maze: using mazes.txt at " + f1.getAbsolutePath());
            return f1;
        }

        // 2) If we're running from bin/, go one level up
        File f2 = new File(".." + File.separator + MAZES_RELATIVE_PATH);
        if (f2.exists()) {
            System.out.println("Maze: using mazes.txt at " + f2.getAbsolutePath());
            return f2;
        }

        // 3) Not found → report and let caller handle fallback layout
        System.out.println("Maze: mazes.txt not found. Expected at " + f1.getAbsolutePath());
        return f1;
    }

    // ---------- PRESET MAP LOADING ----------

    // loadRandomPresetFromFile - Loads a random maze line from mazes.txt or falls back
    private boolean loadRandomPresetFromFile() {
        File file = findMazesFile();
        if (!file.exists()) {
            return false;
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (lines.isEmpty()) {
            return false;
        }

        // Pick a random entry
        String chosen = lines.get(rng.nextInt(lines.size()));

        // Special case: "RANDOM" means use the procedural generator
        if ("RANDOM".equalsIgnoreCase(chosen)) {
            generateRandomLayout();
            return true;
        }

        // Otherwise interpret it as an ASCII maze line.
        return applyMapLine(chosen);
    }

    // applyMapLine - Applies a single ASCII map line to the maze grid
    private boolean applyMapLine(String mapLine) {
        int w = getWidth();
        int h = getHeight();

        String[] rows = mapLine.split("\\|");
        if (rows.length != h) {
            System.err.println("Map row count mismatch. Expected " + h +
                               " but got " + rows.length);
            return false;
        }

        // Clear: floor, no gold, no diamonds
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Cell c = getCell(x, y);
                c.setWalkable(true);
                c.setGold(0);
                c.setDiamond(false);
            }
        }

        // Outer border walls
        for (int x = 0; x < w; x++) {
            getCell(x, 0).setWall(true);
            getCell(x, h - 1).setWall(true);
        }
        for (int y = 0; y < h; y++) {
            getCell(0, y).setWall(true);
            getCell(w - 1, y).setWall(true);
        }

        // Apply interior walls from map data
        for (int y = 1; y < h - 1; y++) {
            String row = rows[y];
            for (int x = 1; x < w - 1; x++) {
                char c = (x < row.length()) ? row.charAt(x) : '.';
                if (c == '#') {
                    getCell(x, y).setWall(true);
                } else {
                    getCell(x, y).setWalkable(true);
                }
            }
        }

        // Set entrance/exit and ensure corridor between them is clear
        int midRow = h / 2;
        entranceX = 0;
        entranceY = midRow;
        exitX = w - 1;
        exitY = midRow;

        getCell(entranceX, entranceY).setWalkable(true);
        getCell(exitX, exitY).setWalkable(true);

        // Guarantee a path straight across the middle row
        for (int x = 1; x < w - 1; x++) {
            getCell(x, midRow).setWalkable(true);
        }

        return true;
    }

    // ---------- FALLBACK BASIC LAYOUT ----------

    // generateBasicLayout - Builds a simple built-in maze if presets are missing
    public final void generateBasicLayout() {
        int w = getWidth();
        int h = getHeight();

        // Clear all: floor, no gold
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Cell c = getCell(x, y);
                c.setWalkable(true);
                c.setGold(0);
            }
        }

        // Outer border walls
        for (int x = 0; x < w; x++) {
            getCell(x, 0).setWall(true);
            getCell(x, h - 1).setWall(true);
        }
        for (int y = 0; y < h; y++) {
            getCell(0, y).setWall(true);
            getCell(w - 1, y).setWall(true);
        }

        // Entrance on left, exit on right, same row
        int midRow = h / 2;
        entranceX = 0;
        entranceY = midRow;
        exitX = w - 1;
        exitY = midRow;

        getCell(entranceX, entranceY).setWalkable(true);
        getCell(exitX, exitY).setWalkable(true);

        // Carve a horizontal corridor between entrance and exit
        for (int x = 1; x < w - 1; x++) {
            getCell(x, midRow).setWalkable(true);
        }

        // Some simple interior walls just so it's not boring
        int wallCol = w / 3;
        for (int y = 2; y < h - 2; y++) {
            getCell(wallCol, y).setWall(true);
        }
        getCell(wallCol, midRow).setWalkable(true);

        int wallCol2 = 2 * w / 3;
        for (int y = 1; y < h - 1; y++) {
            if (y == midRow - 2 || y == midRow + 2) continue;
            getCell(wallCol2, y).setWall(true);
        }
    }

    // generateRandomLayout - Builds a maze using DFS carving plus extra openings
    private void generateRandomLayout() {
        int w = getWidth();
        int h = getHeight();

        // 1) Start with everything as a wall and no gold/diamonds.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Cell c = getCell(x, y);
                c.setWall(true);
                c.setGold(0);
                c.setDiamond(false);
            }
        }

        // 2) Outer border remains walls. Carve entrance and exit openings.
        int midRow = h / 2;
        entranceX = 0;
        entranceY = midRow;
        exitX = w - 1;
        exitY = midRow;

        getCell(entranceX, entranceY).setWalkable(true);
        getCell(exitX, exitY).setWalkable(true);

        // 3) Choose a starting cell just inside the entrance.
        // Use odd coordinates for nicer wall structure.
        int startX = 1;
        int startY = entranceY;
        if (startY <= 0)         startY = 1;
        if (startY >= h - 1)     startY = h - 2;
        if (startY % 2 == 0 && startY + 1 < h - 1) {
            startY++;
        }

        boolean[][] visited = new boolean[h][w];
        carveMazeDFS(startX, startY, visited);

        // 4) Ensure the cell just inside the exit is open and connected.
        int exitInnerX = w - 2;
        int exitInnerY = entranceY;
        getCell(exitInnerX, exitInnerY).setWalkable(true);

        // 5) Add some loops to avoid a single long snake.
        addRandomLoops(w, h, (w * h) / 10);

        // 6) Soften walls across the whole map, more open on the right side.
        softenWalls(w, h);
    }

    // ---------- MAZE GENERATION HELPERS ----------

    // carveMazeDFS - Depth-first maze carving using 2-cell steps
    private void carveMazeDFS(int x, int y, boolean[][] visited) {
        visited[y][x] = true;
        getCell(x, y).setWalkable(true);

        // Moves are 2 cells at a time (so we leave walls between rooms)
        int[][] dirs = { { 2, 0 }, { -2, 0 }, { 0, 2 }, { 0, -2 } };
        shuffleDirections(dirs);

        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];

            // stay inside interior, leave border as walls
            if (nx <= 0 || nx >= width - 1 || ny <= 0 || ny >= height - 1) {
                continue;
            }
            if (visited[ny][nx]) {
                continue;
            }

            // Carve the wall cell between (x,y) and (nx,ny)
            int wx = x + d[0] / 2;
            int wy = y + d[1] / 2;
            getCell(wx, wy).setWalkable(true);

            carveMazeDFS(nx, ny, visited);
        }
    }

    // shuffleDirections - Fisher–Yates shuffle for direction arrays
    private void shuffleDirections(int[][] dirs) {
        for (int i = dirs.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int[] tmp = dirs[i];
            dirs[i] = dirs[j];
            dirs[j] = tmp;
        }
    }

    // addRandomLoops - Punches holes in walls to introduce loops / alternate routes
    private void addRandomLoops(int w, int h, int attempts) {
        for (int i = 0; i < attempts; i++) {
            int x = 1 + rng.nextInt(w - 2);
            int y = 1 + rng.nextInt(h - 2);

            Cell c = getCell(x, y);
            if (!c.isWall()) {
                continue; // already open, skip
            }

            int openNeighbors = 0;
            if (getCell(x + 1, y).isWalkable()) openNeighbors++;
            if (getCell(x - 1, y).isWalkable()) openNeighbors++;
            if (getCell(x, y + 1).isWalkable()) openNeighbors++;
            if (getCell(x, y - 1).isWalkable()) openNeighbors++;

            // Open walls that touch corridors to build loops (with some randomness)
            if (openNeighbors >= 1 && rng.nextDouble() < 0.6) {
                c.setWalkable(true);
            }
        }
    }

    // softenWalls - Softens walls based on neighbor openness, more on the right side
    private void softenWalls(int w, int h) {
        for (int x = 1; x < w - 1; x++) {
            double t = (double) x / (w - 1);  // 0.0 at left, 1.0 at right

            // Base chance to soften on the left, higher on the right
            double baseProb  = 0.10; // ~10% chance on the left side
            double extraProb = 0.18; // up to ~28% on the far right
            double openProb  = baseProb + extraProb * t;

            for (int y = 1; y < h - 1; y++) {
                Cell c = getCell(x, y);
                if (!c.isWall()) {
                    continue;
                }

                int openNeighbors = 0;
                if (getCell(x + 1, y).isWalkable()) openNeighbors++;
                if (getCell(x - 1, y).isWalkable()) openNeighbors++;
                if (getCell(x, y + 1).isWalkable()) openNeighbors++;
                if (getCell(x, y - 1).isWalkable()) openNeighbors++;

                // Only soften walls that are already adjacent to corridors,
                // and more aggressively if there are 2+ open neighbors.
                if (openNeighbors >= 1 && rng.nextDouble() < openProb) {
                    c.setWalkable(true);
                }
            }
        }
    }

    // ---------- BULK HELPERS ----------

    // clearAllEntities - Removes all entities from every cell
    public void clearAllEntities() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                getCell(x, y).clearEntities();
            }
        }
    }

    // clearAllGold - Sets gold to 0 on every cell
    public void clearAllGold() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                getCell(x, y).setGold(0);
            }
        }
    }
}
