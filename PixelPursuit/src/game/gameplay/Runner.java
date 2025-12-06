package game.gameplay;

import game.world.Cell;
import game.world.Maze;

/**
 * The player-controlled runner.
 *
 * Lives on the Maze grid (cell coordinates, not pixels).
 */
public class Runner {

	private final Maze maze;
	private int x;
	private int y;
	private boolean alive = true;

	// Current movement direction for "glide" behavior
	// (-1,0) left, (1,0) right, (0,-1) up, (0,1) down, (0,0) = stopped
	private int dirX = 0;
	private int dirY = 0;

	private int desiredDirX = 0;
	private int desiredDirY = 0;

	public Runner(Maze maze, int startX, int startY) {
		this.maze = maze;
		if (!maze.inBounds(startX, startY)) {
			throw new IllegalArgumentException("Runner start out of bounds");
		}

		this.x = startX;
		this.y = startY;

		maze.getCell(x, y).addEntity(this);
	}

	// --- position ---

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Cell getCell() {
		return maze.getCell(x, y);
	}

	public boolean isAlive() {
		return alive;
	}

	public void kill() {
		alive = false;
	}

	// --- direction / glide control ---

	/** Set the direction for continuous movement. (0,0) means stop. */
	public void setDirection(int dx, int dy) {
		this.desiredDirX = dx;
		this.desiredDirY = dy;
	}

	public int getDirX() {
		return dirX;
	}

	public int getDirY() {
		return dirY;
	}

	/** Stop moving (used if you want a key to cancel glide). */
	public void stop() {
		this.dirX = 0;
		this.dirY = 0;
		this.desiredDirX = 0;
		this.desiredDirY = 0;
	}

	/**
	 * Called each tick by the game loop. Moves one cell in the current direction,
	 * if any.
	 */
	public void step() {
		if (!alive)
			return;
		if (desiredDirX != dirX || desiredDirY != dirY) {
	        if (canMove(desiredDirX, desiredDirY)) {
	            dirX = desiredDirX;
	            dirY = desiredDirY;
	        }
	    }

	    // Move one step in the current direction if possible.
	    if (dirX == 0 && dirY == 0) {
	        return;  // no current direction
	    }

	    if (canMove(dirX, dirY)) {
	        moveBy(dirX, dirY);
	    } else {
	        // Ran straight into a wall, stop
	        dirX = 0;
	        dirY = 0;
	    }
	}

	private boolean canMove(int dx, int dy) {
		if (dx == 0 && dy == 0)
			return false;

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

	/**
	 * Try to move by (dx, dy) one step. Checks bounds + walkable; does nothing if
	 * blocked.
	 */
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
