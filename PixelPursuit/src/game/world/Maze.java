package game.world;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.settings.GameConfig;

/**
 * A rectangular maze made of Cells.
 *
 * Default size is MAZE_WIDTH x MAZE_HEIGHT (from GameConfig).
 */
public class Maze {

    // Logical dimensions (in cells)
    private final int width;
    private final int height;

    // cells[row][col] -> cells[y][x]
    private final Cell[][] cells;

    private int entranceX, entranceY;
    private int exitX, exitY;

    private static final String MAPS_FILE_NAME = "mazes.txt";

    /**
     * Default constructor uses GameConfig dimensions.
     */
    public Maze() {
        this(GameConfig.MAZE_WIDTH, GameConfig.MAZE_HEIGHT);
    }

    /**
     * Explicit-size constructor.
     */
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

        // Try to load a preset; if that fails, fall back to a simple layout.
        if (!loadRandomPresetFromFile()) {
            generateBasicLayout();
        }
    }

    // ---------- DIMENSIONS / CELLS ----------

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width
            && y >= 0 && y < height;
    }

    public Cell getCell(int x, int y) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException(
                    "Cell coordinates out of bounds: (" + x + ", " + y + ")");
        }
        return cells[y][x];
    }

    public int getEntranceX() { return entranceX; }
    public int getEntranceY() { return entranceY; }
    public int getExitX()     { return exitX; }
    public int getExitY()     { return exitY; }

    public Cell getEntranceCell() { return getCell(entranceX, entranceY); }
    public Cell getExitCell()     { return getCell(exitX, exitY); }

    // ---------- PRESET MAP LOADING ----------

    /**
     * Try to load a random map from mazes.txt.
     *
     * File format:
     *  - each non-empty line = one map
     *  - map line contains height segments separated by '|'
     *  - each segment is a row of width characters:
     *      '#' = wall, anything else = floor
     *
     * Returns true on success, false on failure.
     */
    private boolean loadRandomPresetFromFile() {
        File file = new File(MAPS_FILE_NAME);
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

        Random rand = new Random();
        String chosen = lines.get(rand.nextInt(lines.size()));

        return applyMapLine(chosen);
    }

    /**
     * Apply a single map definition line to the maze.
     */
    private boolean applyMapLine(String mapLine) {
        int w = getWidth();
        int h = getHeight();

        String[] rows = mapLine.split("\\|");
        if (rows.length != h) {
            System.err.println("Map row count mismatch. Expected " + h +
                               " but got " + rows.length);
            return false;
        }

        // Clear: floor, no gold
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

        // guarantee a path straight across the middle row
        for (int x = 1; x < w - 1; x++) {
            getCell(x, midRow).setWalkable(true);
        }

        return true;
    }

    // ---------- FALLBACK BASIC LAYOUT ----------

    /**
     * Simple built-in layout if mazes.txt is missing or invalid.
     */
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

    // ---------- helpers ----------

    public void clearAllEntities() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                getCell(x, y).clearEntities();
            }
        }
    }

    public void clearAllGold() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                getCell(x, y).setGold(0);
            }
        }
    }
}
