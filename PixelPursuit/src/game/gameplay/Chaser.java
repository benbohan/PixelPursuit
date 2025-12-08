package game.gameplay;

import game.world.Cell;
import game.world.Maze;

/**
 * Chaser enemy controlled by a ChaserAI:
 *  - Lives on the Maze grid and occupies a single Cell at (x, y).
 *  - Updates once per tick and delegates movement decisions to its AI.
 *  - Moves only into in-bounds, walkable cells (multiple chasers can share a cell).
 */
public class Chaser {

    // ---------- FIELDS ----------

    private final Maze maze;
    private final ChaserAI ai;

    private int x;
    private int y;
    private boolean active = true;

    // ---------- CONSTRUCTORS ----------

    // Chaser - Creates a chaser at (startX, startY) and registers it with the maze
    public Chaser(Maze maze, int startX, int startY, ChaserAI ai) {
        this.maze = maze;
        this.ai = ai;

        if (!maze.inBounds(startX, startY)) {
            throw new IllegalArgumentException("Chaser start out of bounds");
        }

        this.x = startX;
        this.y = startY;

        maze.getCell(x, y).addEntity(this);
    }

    // ---------- POSITION ----------

    // getX - Returns the current x-coordinate in the maze grid
    public int getX() { 
        return x; 
    }

    // getY - Returns the current y-coordinate in the maze grid
    public int getY() { 
        return y; 
    }

    // getCell - Returns the maze Cell currently occupied by this chaser
    public Cell getCell() {
        return maze.getCell(x, y);
    }

    // ---------- STATE ----------

    // isActive - Returns true if this chaser is still active in the session
    public boolean isActive() {
        return active;
    }

    // deactivate - Marks this chaser as inactive so it no longer updates or moves
    public void deactivate() {
        active = false;
    }

    // ---------- TICK / UPDATE ----------

    // update - Called each tick by Session to let the AI choose movement
    public void update(Session session) {
        if (!active) return;
        if (ai != null) {
            ai.update(this, session);
        }
    }

    // ---------- MOVEMENT ----------

    // moveBy - Attempts to move by (dx, dy) if the target cell is in-bounds and walkable
    public void moveBy(int dx, int dy) {
        if (!active) return;
        if (dx == 0 && dy == 0) return;

        int newX = x + dx;
        int newY = y + dy;

        if (!maze.inBounds(newX, newY)) {
            return;
        }

        Cell target = maze.getCell(newX, newY);
        if (!target.isWalkable()) {
            return;
        }

        // Multiple chasers can share a cell – they phase through each other
        Cell current = maze.getCell(x, y);
        current.removeEntity(this);
        target.addEntity(this);

        x = newX;
        y = newY;
    }
}
