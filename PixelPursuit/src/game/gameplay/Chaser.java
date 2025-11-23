package game.gameplay;

import game.world.Cell;
import game.world.Maze;

/**
 * Enemy that chases the Runner.
 */
public class Chaser {

    private final Maze maze;
    private final ChaserAI ai;

    private int x;
    private int y;
    private boolean active = true;

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

    // --- position ---

    public int getX() { return x; }
    public int getY() { return y; }

    public Cell getCell() {
        return maze.getCell(x, y);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    /**
     * Called each tick by Session.
     */
    public void update(Session session) {
        if (!active) return;
        if (ai != null) {
            ai.update(this, session);
        }
    }

    /**
     * Move by (dx, dy) if in-bounds + walkable.
     * Multiple chasers can share a cell – they phase through each other.
     */
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

        Cell current = maze.getCell(x, y);
        current.removeEntity(this);
        target.addEntity(this);

        x = newX;
        y = newY;
    }
}
