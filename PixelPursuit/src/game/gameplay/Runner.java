package game.gameplay;

import game.world.Cell;
import game.world.Maze;

/**
 * Player-controlled runner on the Maze grid:
 *  - Lives in maze cell coordinates (x, y), not pixels.
 *  - Supports “glide” movement that continues in a direction until stopped or blocked.
 *  - Updates once per tick via step(), applying desired direction changes first.
 */
public class Runner {

    // ---------- FIELDS ----------

    private final Maze maze;
    private int x;
    private int y;
    private boolean alive = true;

    // Current movement direction for "glide" behavior
    // (-1,0) left, (1,0) right, (0,-1) up, (0,1) down, (0,0) = stopped
    private int dirX = 0;
    private int dirY = 0;

    // Direction the player wants to move next (used to change direction cleanly)
    private int desiredDirX = 0;
    private int desiredDirY = 0;

    // ---------- CONSTRUCTORS ----------

    // Runner - Creates a runner at (startX, startY) and registers it with the maze
    public Runner(Maze maze, int startX, int startY) {
        this.maze = maze;
        if (!maze.inBounds(startX, startY)) {
            throw new IllegalArgumentException("Runner start out of bounds");
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

    // getCell - Returns the maze Cell currently occupied by this runner
    public Cell getCell() {
        return maze.getCell(x, y);
    }

    // ---------- STATE ----------

    // isAlive - Returns true if the runner is still alive
    public boolean isAlive() {
        return alive;
    }

    // kill - Marks the runner as dead so it no longer moves
    public void kill() {
        alive = false;
    }

    // ---------- DIRECTION / GLIDE CONTROL ----------

    // setDirection - Sets the desired glide direction; (0,0) means stop
    public void setDirection(int dx, int dy) {
        this.desiredDirX = dx;
        this.desiredDirY = dy;
    }

    // getDirX - Returns the current x-direction of glide movement
    public int getDirX() {
        return dirX;
    }

    // getDirY - Returns the current y-direction of glide movement
    public int getDirY() {
        return dirY;
    }

    // stop - Immediately stops movement and clears desired direction
    public void stop() {
        this.dirX = 0;
        this.dirY = 0;
        this.desiredDirX = 0;
        this.desiredDirY = 0;
    }

    // ---------- TICK / UPDATE ----------

    // step - Called each tick; updates direction then moves one cell if possible
    public void step() {
        if (!alive) return;

        // Try to switch to the desired direction if it is different and valid
        if (desiredDirX != dirX || desiredDirY != dirY) {
            if (canMove(desiredDirX, desiredDirY)) {
                dirX = desiredDirX;
                dirY = desiredDirY;
            }
        }

        // Move one step in the current direction if possible
        if (dirX == 0 && dirY == 0) {
            return; // no current direction
        }

        if (canMove(dirX, dirY)) {
            moveBy(dirX, dirY);
        } else {
            // Ran straight into a wall, stop
            dirX = 0;
            dirY = 0;
        }
    }

    // ---------- MOVEMENT HELPERS ----------

    // canMove - Returns true if moving by (dx, dy) stays in-bounds and walkable
    private boolean canMove(int dx, int dy) {
        if (dx == 0 && dy == 0) return false;

        int newX = x + dx;
        int newY = y + dy;

        if (!maze.inBounds(newX, newY)) {
            return false;
        }
        if (!maze.getCell(newX, newY).isWalkable()) {
            return false;
        }
        return true;
    }

    // moveBy - Moves by (dx, dy) one step if not blocked and runner is alive
    public void moveBy(int dx, int dy) {
        if (!alive) return;
        if (!canMove(dx, dy)) return;

        int newX = x + dx;
        int newY = y + dy;

        Cell current = maze.getCell(x, y);
        Cell target  = maze.getCell(newX, newY);

        current.removeEntity(this);
        target.addEntity(this);

        x = newX;
        y = newY;
    }
}
